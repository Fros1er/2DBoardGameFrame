# 棋盘相关

有关的包：
```
frame.board
```

这一部分包括棋盘，棋盘上的格子和棋子的数据。

框架是前后端分离的，所以不要在这里写界面相关的东西，也不要在另一个Board里写游戏逻辑。

## 如何继承

写一个你自己的类继承BaseBoard。这个类的构造函数必须有且仅有两个参数(int width, int height)。 然后你需要实现Board的init方法。  
然后，如果你想在格子里存棋子之外的东西，写个类继承BaseGrid。你自己的Grid（或者BaseGrid）就是Board.init()里需要往grids里填的东西。  
再然后，写个棋子类继承BasePiece，存你需要的数据（颜色等）。

## BaseBoard

棋盘，抽象类。棋盘的0,0在左上角。

### BaseBoard的构造函数

`public BaseBoard(int width, int height);`

设置宽和高。

### BaseBoard的方法：

`public abstract void init();`

棋盘的初始化。基本来说就是把你的grid填进去。如果要初始放棋子也是在这里面。看example。

`public BasePiece movePiece(int srcX, int srcY, int destX, int destY);`  
把棋子从(srcX, srcY)移动到(destX, destY)。  
如果(srcX, srcY)没棋，或者(destX, destY)没棋，返回null，否则返回(destX, destY)上的棋，然后在棋盘上把它覆盖掉。

`public void forEach(BiConsumer<Point2D, BaseGrid> action);`  
遍历board。和Map的forEach差不多，不过我没在例子里用。

### BaseBoard的getter & setter

`getWidth(), getHeight()`

获取宽和高。

`public BaseGrid getGrid(int x, int y);`

获取x, y坐标上的格子。拿到的是BaseGrid这个基类，所以需要对结果进行一个强制类型转换，转成你自己的Grid类。


## BaseGrid

放棋子用的那个格子。

### BaseGrid的构造函数

`public BaseGrid(int x, int y);`

构造一个坐标为x, y的格子。

### BaseGrid的getter & setter

x和y是public final的，可以直接拿去用，但是不能改。

`boolean hasPiece()`

判断格子上有没有棋子。

`boolean setOwnedPiece(BasePiece piece)`

尝试把棋子放到格子上。如果格子上已经有棋子就返回false，并且不放。

`BasePiece getOwnedPiece()`

返回格子上的棋子，没有则返回null。

`BasePiece removeOwnedPiece()`

删除格子上的棋子，并且返回被删掉的那个棋子。没有则返回null。

### BaseGrid不推荐使用的方法

变量actionFactory。Game里有更好的办法去设置。总之不要动它（

## BasePiece

棋子，抽象类，里面只记录了坐标。你需要继承一下然后往里加自己的东西。