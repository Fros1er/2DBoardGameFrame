# A Simple Framework for 2-D Board Games

# For non-Chinese speakers

Sorry for not providing English version of readme and docs. I'll add them once I have time.

# 简介

这是一个用jvav swing写的给2D棋类游戏或者带格子的游戏（比如扫雷）的框架。  
功能包括：基础的gui架构（resize，更换图片，音乐的接口等），撤销，存档/读档和迫真的回放，人机对战接口，简陋的玩家数据保存和排行榜。  
框架不提供的功能（需要自己实现的部分）包括：具体游戏逻辑，具体的棋盘/格子/棋子/部分ui的绘制，AI的具体算法。这些部分并不多，详见example里200多行写的五子棋。

更多细节请查看[文档](docs/README.md)和[example](docs/examples.md)。

如果有文档没有涵盖的问题，或者您对我乱七八糟的代码有宝贵的建议或意见，请提交issue，或者pm也行。

本项目由Apache-2.0协议开源。
