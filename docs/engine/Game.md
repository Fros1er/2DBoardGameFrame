# 控制器

具体控制游戏逻辑的类。  
里面全都是静态方法，所以在哪都可以访问到。

## 游戏执行的大致步骤

进入游戏（进入GameStage）：框架调用Game.init()方法。  
↓  
进行游戏（执行一堆performAction）  
↓  
游戏结束（Game.setGameEndingJudge()设置的函数返回true时）

后文会提到进入游戏和游戏结束，意思就是上面提到的部分。

## 游戏属性相关

`Game.setMaximumPlayer(int num)`

设置最大玩家数。两人对战写成2就好。

`Game.setBoardSize(int width, int height)`

设置棋盘的大小。

`Game.getMaximumPlayer()`

获取最大玩家数。

`Game.getWidth(); Game.getHeight()`

获取棋盘的宽和高。

## 棋盘注册和获取

`Game.registerBoard(Class board)`

你需要参考[棋盘相关](Board-Grid-and-Piece.md)一节的开头来继承你的棋盘类。之后，假设你的棋盘类叫`Board`，那么这个方法的需要填的参数就是`Board.class`。如果没懂这个参数，看example。

`Game.getBoard()`

在游戏开始后（指你看到棋盘以后）返回棋盘(BaseBoard)类的实例。游戏开始前调用会返回null。建议只在你写的lambda表达式里使用。

## 游戏逻辑部分

`Game.registerGridAction(Range range, ActionFactory factory)`

用于设置所有格子的[Action](Action-and-Event.md)。  
第一个参数Range填一个参数为两个int(代表坐标x, y)的lambda表达式。如果lambda表达式返回true，则代表给坐标为(x, y)的格子设置Action。返回false则不设置。  
第二个参数是填用来返回一个Action的lambda表达式。参数为(x, y, mouseButton)。  
x, y代表坐标，mouseButton代表点击格子的鼠标按键（1代表左键，2代表中键，3代表右键）。  

在你返回的Action里需要做的：
- 实现Action的perform方法，在那个方法里用getBoard方法获取棋盘，然后依据坐标和鼠标案件操作棋盘（给某些格子上放一个棋子或者删除棋子等等）。
- 实现Action的undo方法，基本就是把你刚才做的操作反着来一遍。放的棋子删掉，删的棋子放回去。

在这里不需要考虑前端怎么画，也不要加UI代码。唯一的例外是播放音效。

在给格子注册Action后，玩家在点击这个格子时就会触发你用这个方法设置的Action。你可以先只在返回的Action里写个打印试试。

注意，一个格子只能存在一个Action，后加的会覆盖前面的。

`Game.setPlayerWinningJudge(Predicate<Player> predicate)`  

用于设置用来判断玩家是否胜利的函数。   
这句话（包括以下类似的）的断句为：用于设置 | 用来判断玩家是否胜利的函数 | (的函数)。  
参数是一个传入Player类的lambda表达式，其返回值是boolean，代表当前玩家是否胜利。

`Game.setPlayerLosingJudge(Predicate<Player> predicate)`  

用于设置用来判断玩家是否失败的函数。  
注意，这里的失败的意思是类似于扫雷踩中了雷，或者投降了，而不是说因为有其他玩家赢了所以这个玩家输了。
一般的棋类游戏里不需要设置这个。
有一个一直返回false的函数作为默认值，其他同setPlayerWinningJudge。

`Game.setGameEndingJudge(BooleanSupplier supplier)`

用于设置用来判断游戏是否结束的函数。  
参数是一个没有参数，返回boolean的lambda表达式，返回true代表游戏结束。
框架里默认是有人出局（无论输赢）就结束。  
[PlayerManager](Player.md)中提供了几种默认设置，可以直接传入。不过如果存在平局的设定，需要自己写函数判断。

`setInitFunction(Procedure procedure)`

用于设置游戏初始化时额外执行的函数。  
参数是一个没有参数没有返回值的lambda表达式。  
传入的lambda在每次进入游戏时触发一次。  
一般不需要，我也没想出来可能的用处，但放在这里了。

`setGameEndFunction(Procedure procedure)`

用于设置游戏结束时额外执行的函数。  
参数是一个没有参数没有返回值的lambda表达式。  
传入的lambda在每次游戏结束时触发一次。  
一般不需要，可能的用处是扫雷踩雷了以后显示没被标记的雷。

视觉效果不要在这里写，View里有个长得很像的函数，在那边写。

## 玩家相关

`Game.getCurrentPlayer()`

返回当前回合的玩家（Player实例）。

`Game.getCurrentPlayerIndex()`  
`Game.getNextPlayerIndex()`

返回当前回合/下一回合的玩家id。可以与自定义的颜色枚举搭配使用。  
建议参考example。

## 存档相关

一般不需要使用，框架都处理好了。  
如果一定需要的话，详见[Saver](Save-and-Saver.md)。  
Game里有初始化好的默认Saver，`Game.saver`。这个是public static的，直接用就行。

## 其他操作

`Game.init()`

进入游戏时自动调用。如果你需要reset按钮，在listener里把你自己的static变量（如果有的话）初始化，然后调用这个。   
init会干这些事情：
- 终止之前的读档过程
- 重新开始游戏，清空之前的action
- 创建新的Board实例，并调用Board.init()。
- 调用Game.registerGridAction里传入的lambda表达式给格子注册事件。
- 复活所有玩家。
- 调用Game.setInitFunction里传入的的lambda表达式。
- 如果需要的话，读档并回放之前的步骤。
- 通知第一个玩家（让AI下棋）。
- 发布BoardChangeEvent。

`Game.cancelLastAction()`

撤销上一步。如果你想自定义撤销按钮可能用得到。

`Game.performAction(Action action)`

执行一个Action并判断是否有玩家胜利以及本回合和游戏是否结束。框架会帮你处理，除了特殊情况一般不需要手动调。

## 不许手动调用的方法

`Game.nextTurn()`
`Game.previousTurn()`

切换玩家用。手动调用会把玩家系统搞乱。