# Action和Event

有关的包：
```
frame.action
frame.event
```

我不知道该怎么正确的翻译这两个东西，所以标题就。。。比较怪。。。

## Action和Event的区别

总之，它们是两种事件。  
Action代表玩家去执行的事件，Event代表已经发生的事件。

比如，你自己写的Action往棋盘上放了一个棋子，并且成功了。这时候就会触发一个BoardChangeEvent来通知前端棋盘改变了。

Action只和后端产生的事件有关（但可能从前端触发）  
Event会被前端和后端一起接受，可能一起使用，但只会从后端触发。  
所以，如果想加动画一类的东西，请不要试着去注册Action。它不是用在这里的。

## Action

Action是一个抽象类。

### Action的构造函数：  
`public Action(boolean endTurn);`  
endTurn代表玩家在成功执行这个action之后会结束自己的回合。  
在Game里的一个函数会使用到。

### Action的抽象方法：

`public abstract ActionType perform();`  
这个方法用于实际执行一个Action，返回一个ActionType。
举个例子，五子棋的（唯一一个）Action里，这个方法用来给棋盘上某一个格子上放棋，如果对应格子已经有了棋就返回FAIL。
ActionType和撤销有关。具体如下：
1. 如果撤销时最后一个执行的Action返回SUCCESS，则撤销这个Action，并撤销这一个和上一个SUCCESS之间的所有PENDING。
2. 如果撤销时最后一个执行的Action返回PENDING，则撤销上一个SUCCESS之后的所有PENDING。  

另外，如果有一个Action返回FAIL，也撤销上一个SUCCESS之后的所有PENDING。

再举个例子。移动棋子需要先拿起来（指定走的棋子）再放下（落子）。拿起来是一个返回PENDING的Action（不改变棋盘），放下是返回SUCCESS的Action。
撤销时，如果当前玩家拿起了棋子就让它把棋子放回原位，如果已经下完了，就连着拿起来的动作一起撤销。

`public abstract void undo();`  
用于撤销上一步的操作。基本就是复原返回SUCCESS的perform干的事情，看example就好。

`public void removePending()`  
在PENDING被撤销时调用。还是看example。

这三个方法不需要手动调用，但你自己的Action需要实现其中的两个abstract方法。其他的框架会处理好。

### Action的getter & setter

无。

### Action不推荐使用的方法

`getChangedPlayer();`和`setChangedPlayer();`。  
这是框架自己会用的函数，用于记录Action调用后有没有玩家出局。手动调用可能会导致撤销出现错误。

## Event

Event的基类使用`java.util.EventObject`。

**不要使用EventObject.getSource();** 代码框架暂时没有正确的实现这一部分。

如果你不打算定义你自己的Event（大概率不需要），或者接收系统的Event（应该只会在前端用），可以跳过这一部分。

### Event的发布和接收

Event的发布和订阅通过EventCenter类进行。
`EventCenter.subscribe(Class, Consumer)`
订阅一个事件。传入的第一个参数是你要订阅的事件的class，第二个参数是接收到事件要执行的lambda函数。lambda函数会接受对应那个事件的对象作为参数。  
比如，订阅BoardChangeEvent的方式是：  
``` java
EventCenter.subscribe(BoardChangeEvent.class, (e) -> {
    //do something
});
```

写了这三行之后，在BoardChangeEvent被触发的时候 `//do something` 这部分的代码就会执行。

`EventCenter.publish(EventObject);`  
发布一个事件。函数里面传一个继承EventObject的对象。  
继承EventObject的对象里面可以放一些成员变量（但subscribe的lambda里传入的变量是EventObject基类，通过这个传递变量的话需要强制类型转换），所以可以方便的把参数在全局传来传去。

除非你确定内置Event的含义（见下文），否则不要手动触发内置的Event，否则会有各种奇怪的bug。

### 各种内置Event

每个Event里面的参数可以用idea跳进去看。不在这说了。  
下面说的触发是框架默认内置的行为。你可以通过上面说的订阅添加新的行为。

`BoardChangeEvent`  
在第一次进入游戏，任意Action被执行或撤销时触发。触发后会调用棋盘和所有格子的redraw方法。

`GameEndEvent`  
一局游戏结束时触发。触发后会更新玩家排行榜的信息，并且调用View里注册的onGameEnd方法。

`PlayerWinEvent`  
有玩家胜利时触发。触发后会调用View里注册的onPlayerWin方法。

`PlayerLoseEvent`  
有玩家失败时触发。触发后会调用View里注册的onPlayerLose方法。

`PlayerChangeEvent`  
在RoomStage里有玩家姓名或者类型发生变化的时候触发。触发后更新RoomStage里玩家显示，以及如果开启联网对战的话也会更新其他客户端的玩家显示。