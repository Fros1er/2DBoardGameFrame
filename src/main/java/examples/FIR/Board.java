package examples.FIR;

import frame.board.BaseBoard;
import frame.board.BaseGrid;

public class Board extends BaseBoard {
    // 棋盘要继承BaseBoard类
    // 如果看到没见过的变量，跳到BaseBoard类或者看文档
    public Board(int width, int height) {
        super(width, height);
    }

    // 2.1 初始化
    // init里的部分每次开始游戏执行一次，用来给棋盘数组赋值
    // 五子棋就简单的把grids里面填满BaseGrid就好了
    // 如果游戏开始的时候要摆放棋子也一起写在这里面
    @Override
    public void init() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                grids[i][j] = new Grid(i, j);
            }
        }
    }
}
