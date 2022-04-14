# 玩家，玩家管理器，以及排行榜相关

有关的包：
```
frame.player
```

玩家类，和用来管理玩家的工具类。

# 玩家

## PlayerType

枚举类，有两个个value：LOCAL, AI，分别对应两种Player。

## Player

玩家，抽象类。就是。。。一个玩家。

### Player的构造函数

`Player(int id, String name, PlayerType type);`

普通的id，玩家名，还有一个枚举类型用来判断玩家是本地还是AI还是联网的。  
不需要自己调，有个工厂方法干这事。但工厂方法也是框架自己处理。

### Player的getter & setter

`getId(), getName(), getType()`

见构造函数。

### Player不推荐使用的方法

所有除了getter之外的方法。基本除了PlayerManager里有就是框架自己封好了。

## LocalPlayer

在本地执行操作的玩家。除了PlayerType其他和Player一样。

## AIPlayer

人工智障玩家。  
如果你要做多个难度的人工智障，可以考虑做这三个难度：
- 只会投降的法国AI
- 只会按照先行后列下的AI
- 随机往棋盘上扔子的AI

### 如何添加一个AI
调用下面的addAIType函数。

### AIPlayer的构造函数

`AIPlayer(int id, String name, int delay);`

前两个参数和Player一样。  
众所周知，让AI歇一会再下棋会显得你的人工智障更聪明。  
第三个参数用来设置你这个名字的AI下一步棋之前歇多久。

### AIPlayer的抽象方法

`protected abstract boolean calculateNextMove();`

这是让AI下棋的函数。在里面直接拿Game.getBoard()获取整个棋盘，然后想办法算个坐标，然后调用下面提到的performGridAction让AI执行你的Action。然后AI就下棋了。  
这个方法有个返回值。如果你发现没棋可下就返回false，AI会直接投降（

### AIPlayer的方法

`static void addAIType(String name, Function aiFactory)`

不要被Function这个你没见过的东西吓到。
我先举个例子：
``` java
AIPlayer.addAIType("France", (id) -> {
    return new AIPlayer(id, "France"， 200) {
        //200啥意思看构造函数
        @Override
        protected boolean calculateNextMove() {
            return false;
        }
    }
})
```
一个只会投降的AI就做完了。

所以这个函数第一个参数是AI的名字，第二个参数是传入一个id的lambda表达式，用来返回一个继承AIPlayer的匿名内部类，里面具体实现你的calculateNextMove方法。

照example抄，然后改里面的函数就行了。

# 排行榜

这个做的很敷衍，没有登录啥的，只是根据玩家名统计胜场和负场。

如何清空排行榜：删除players.sav。

## PlayerInfo

玩家信息，记录玩家名，胜场和负场

### PlayerInfo的getter & setter

`getName(), getWinCount(). getLoseCount()`  

获取玩家名，胜场和负场

### PlayerInfo不推荐使用的方法

add开头的两个。手动调的话相当于让这个玩家的记录输一场或者赢一场，后果显而易见。

### PlayerManager里有关的

`static PlayerInfo getPlayerInfo(String name);`  

获取玩家信息。

`static Collection<PlayerInfo>` getAllPlayersInfo();

如果要把结果转成List，结果那个变量后面加个`.stream().toList();`就行。

# 玩家管理

## PlayerManager

工具类。里面一堆方法，但大部分你应该不会用到。

一个词的解释：
Out: 玩家出局，就是玩家已经赢了或者输了不能再下了。

### 判断游戏结束的内建函数们
``` java
isAllPlayerOut();
isOnePlayerRemains();
isOnePlayerOut();
isPlayerRemains(int n);
isPlayerWins(int n);
isPlayerLoses(int n);
```

用于判断胜利条件。  
一般两人对弈的话，用isOnePlayerRemains，因为可以处理有人胜利和有人投降的情况。  
注意，对于带out和remains的方法，玩家胜利/失败都是出局。就算按框架里的概念没有写判断玩家输的方法，投降也算是输。

### PlayerManager的getter & setter


### BaseGrid不推荐使用的方法

没说的都不准用，因为玩家系统本身就写的不太美观。。。会出一堆bug的。