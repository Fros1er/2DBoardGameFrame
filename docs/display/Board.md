# 棋盘和格子的前端

框架是前后端分离的，所以不要在这里写游戏逻辑，也不要在另一个Board里写界面相关的东西。

## BoardView

棋盘前端的类，继承自JPanel。

使用GridBagLayout。

方法和下面的GridView一样，不过不是抽象的。如果不做额外的事情就不需要重写。

## GridView

格子前端的接口。拥有init和redraw两个方法需要实现。不过在设置格子样式的时候不用这个接口，用下面两个类之一继承一个新的匿名内部类返回。

init方法在开始游戏时执行一次，用于修改格子的初始显示。  
所有格子的redraw方法会在棋盘有变化（比如说Action执行）时全部执行一次。

所以，你可以用init给格子的边框换个颜色，然后用redraw方法把棋子画在格子里。

## GridButtonView

显示为JButton的格子，一般不用。

## GridPanelView

显示为JPanel的格子，里面默认有一个JLabel。使用BoxLayout，并且添加了奇怪的东西，使得JLabel可以居中。默认有一个黑色边框，并且会保证自己为方形。