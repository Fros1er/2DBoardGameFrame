# Stage

这个名字可能起的不太好，但是“舞台”或者“阶段”这两个翻译加起来大概能表达意思。

以下的组件指的是swing的Component。

总体而言，一个Stage代表一个界面（也是个JPanel）。比如菜单，选择玩家的房间，棋盘，排行榜等等，每一个都是一个Stage。

当处于一个Stage时，该Stage上的组件会显示，而其他的Stage会隐藏。这是通过CardLayout实现的。

Stage本身是单例，详见[一些基础知识](../engine/Basic-Requirements.md)。

View里提供了一些方法来在Stage之间切换，禁用某一个Stage，或者新增Stage。

下面的目录是框架内建的Stage（如果你不改某一个Stage的显示，就不用看那个的文档）：

- [MenuStage](MenuStage.md)
- [RoomStage](RoomStage.md)
- [LoadStage](LoadStage.md)
- [GameStage](GameStage.md)
- [RankingStage](RankingStage.md)

# Stage中组件的渲染过程
Stage中所有的Component会在构造函数中创建完毕，并设置好有关的样式。由于Stage是个单例，所以你可以认为这些东西在你所有自己的代码之前就弄好了。  
接下来是View.start()。这里会调用xxxStage.init()把所有的Component添加进Stage或它的父组件（父组件也在Stage里）。  
唯一的例外是棋盘会在进入GameStage的时候动态绘制。

## 修改Stage里的界面

每一个内建的Stage对应的文档里会说明Stage本身的布局（Stage自己就是个JPanel），和Stage里所有添加的组件。  
所有的组件都是public的，所以可以通过XXXStage.instance().xxx直接访问。之后，你可以对这些组件的样式，内容或者listener进行修改，或者隐藏（`setVisible(false)`）自己不需要的组件。

Stage本身就是个[BackgroundImagePanel](Misc.md)，且所有的Stage里的Panel都是BackgroundImagePanel,所以可以方便的设背景图片。

如果你要自行**添加**组件，请遵循以下几点：
- 在`View.start()`之前添加到某个panel的组件会出现在所有panel自带的组件之前。（详见初始化过程一节）
- 在`View.start()`之后添加到某个panel的组件会出现在所有panel自带的组件之后。（详见初始化过程一节）
- 如果你需要在自带的组件中间插入或修改layout，请使用`setCustomDrawMethod`（看下面）。
- GameStage.board以及其他在View类里有相关set的东西不遵循以上内容。请使用View里的对应方法。
- 如果要添加依赖于玩家在游戏开始一段时间之后输入的组件，请想个办法延后添加。比如，如果要在GameStage里根据AI难度来显示不同图片的话，你需要通过订阅BoardChangeEvent来在进入游戏后做这件事。这是因为main函数是在所有部分之前执行，执行时无法预测玩家会选择什么难度的AI。所以必须延后。
- 如果组件内容要根据游戏内容动态变化，请订阅有关的Event。如果要接收点击事件，请实现对应listener。对于鼠标点击的listener可以参考GameStage里的实现。

以上内容是有关**添加**组件的。如果你只想加个背景图片或者音乐，改个字体，隐藏组件什么的，不用考虑这么多。

# setCustomDrawMethod(Procedure)
上文说到要把Component添加进Stage。。。那该怎么摆呢？  
有默认的一套东西，但你如果不喜欢，用这个可以自定义。
在View.start()之前调用这个，往里扔个lambda，然后开写。。  
这个函数会完全覆盖掉默认的添加方式，所以你大概需要把原来的复制出来（在Stage构造函数最下面有个drawComponents = () -> {}），在这个基础上改。  
因为这里面没有this，所以还需要手动获取一下。。总之就非常麻烦，但我没有更好的写法了
此外，RoomBlock虽然不是Stage，但也有这一套东西（有细微的差别）。

## 自己写一个Stage

一般不需要，但万一有大佬想做呢。。。  
所有的自定义Stage都需要把单例的代码复制进去一份 大概。  
然后需要把Stage添加进View里。如果你自己的Stage功能是替换我原来的Stage的功能，Stage名字得一样  
然后如果你把MenuStage改了，需要在main函数里调用一下`View.changeStage("MenuStage");`，覆盖掉默认的。  
具体做法：
继承BaseStage，然后有需要的话重写里面的方法，再调用View里那几个函数。建议直接参考源码。  
如果你觉得我提供的各种自定义方法还是不太能不太能满足需求，也可以继承某一个Stage，重写某个函数，然后调一遍View那几个方法把原来的删了，新的加进去。