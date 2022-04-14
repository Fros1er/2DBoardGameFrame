# 前端的控制器

用于控制视觉显示的类。
里面全是静态方法，随便调吧。

## 设置棋盘模板

`void setGridViewPattern(GridViewFactory<T> factory)`

设置格子如何绘制。参数是一个返回GridView的lambda表达式。参考[棋盘](Board.md)，你需要在GridPanelView和GridButtonView中选择一个类继承，然后实现init和redraw两个方法，然后返回new出来的实例。

`void setBoardViewPattern(BoardViewFactory factory)`

设置棋盘如何绘制。和上面的设置格子一样，不同之处是BoardView不是接口而是类。如果不需要在init和redraw时对整个棋盘（不包括格子）做任何额外的事情，可以直接new一个BoardView返回。你也可以重写这两个方法，做你想做的事情。

## 杂项

`void setName(String name)`

设置游戏的标题。会显示在主菜单上。

`void start()`

初始化所有的stage。一般在你main函数的最后一行，并且一定要有。例外见[Stage](Stage.md)中“修改Stage里的界面”一节。

## 视觉效果

`void setPlayerWinView(Consumer<Player> onPlayerWin)`  

玩家赢了的时候触发传入里面的lambda表达式。参数是一个lambda表达式，其参数是一个Player，没有返回值。这里只写视觉效果。

`void setPlayerLoseView(Consumer<Player> onPlayerLose)`

同上，玩家输了的时候触发。

`void setGameEndView(Consumer<Boolean> onGameEnd)`

同上，游戏结束时触发。传入的boolean代表是否平局。

**如果你刚开始看文档，这一节看到这里就行了。**

## Stage相关

`void addStage(String name, BaseStage stage)`

添加一个Stage。

`BaseStage getStage(String name)`

通过name获取一个Stage。

`void changeStage(String name)`

切换到名字为name的Stage。

`void disableStage(String name, BaseStage jumpTo)`

禁用某个Stage，并且将所有跳转到该Stage的请求全部重定向到jumpTo对应的Stage。  
没有经过实践，可能会出一堆bug，慎用。

## 静态成员

`JFrame window`  
窗口。  
`CardLayout layout`  
sceneHolder的布局管理器。  
`JPanel sceneHolder`  
装着所有Stage的JPanel。