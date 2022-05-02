# 文档

## 注意事项

不建议使用文档和样例里没有提到的构造函数或者方法。如果你发现某些情况下一定要用，建议先提问，以便于我补充文档里漏掉的东西或者添加样例。

**文档的很大一部分是说明书一样的东西，所以不用一次性看完，有需要的时候查阅对应的部分即可。善用浏览器的页面内搜索功能（CTRL+F）。**

## 如何使用 

1. 本文档中所有能点的地方都可以跳转。
2. 看完[一些基础知识](engine/Basic-Requirements.md)。
3. 看完[一些概念](Concepts.md)
4. 依照[如何导入](import.md)提供的方法创建一个项目，导入框架。
5. 搞个main函数出来。
6. 打开example作为参考。
7. 依据样例里的注释和样例代码本身写你自己的游戏。
8. 你会发现里面有很多不知道用来干什么的方法。根据目录去找对应的方法，然后看一下（没必要把其他的一次看完）。
9. 如果你需要干一些example里没有做过的事，看下面的目录，然后翻翻对应的文档。
10. 如果你需要改ui，请参考[Stage](display/Stage.md)中“修改Stage里的界面“，和[概念](Concepts.md)中“框架里前端部分的一些概念”一节。

## 目录

这里是所有文档的目录。

### 开始

[一些概念](Concepts.md)  
[如何导入](import.md)

### 后端

[一些基础知识](engine/Basic-Requirements.md)  
[控制器](engine/Game.md)  
[Action和Event](engine/Action-and-Event.md)  
[棋盘相关](engine/Board-Grid-and-Piece.md)  
[玩家，玩家管理器，以及排行榜相关](engine/Player.md)  
[存档](engine/Save-and-Saver.md)  
[工具](engine/Util.md)

### 前端

[java swing](display/swing.md)  
[Stage](display/Stage.md)  
[前端的控制器](display/View.md)  
[棋盘和格子的前端](display/Board.md)  
[音乐播放器](display/Music.md)  
[杂项](display/Misc.md)

#### 各种Stage：
- [MenuStage](display/MenuStage.md)
- [RoomStage](display/RoomStage.md)
- [LoadStage](display/LoadStage.md)
- [GameStage](display/GameStage.md)
- [RankingStage](display/RankingStage.md)

#### ~~一个神奇的文档~~

[~~分数列表~~](score.md)