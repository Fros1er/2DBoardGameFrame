# 房间

![](images/room.png)

布局是BoxLayout。

上面两个文本框是用来设置棋盘宽高的。不需要可以直接隐藏掉。

RoomBlock见下文。

## RoomBlock

每一个RoomBlock代表一个玩家框。

![](images/roomblock.png)

布局是GridLayout。接受PlayerChangeEvent，每次收到会更新玩家姓名和胜场。