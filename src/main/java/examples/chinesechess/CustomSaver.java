package examples.chinesechess;

import frame.Controller.DefaultSaver;
import frame.Controller.Game;
import frame.save.Save;
import frame.save.Saver;
import frame.save.UnmatchedSizeException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.StringTokenizer;

// 自定义Saver
public class CustomSaver extends DefaultSaver {
    //框架的存档是存的每一步的action。。不过如果想对存档做点手脚，可以继承DefaultSaver自己写点东西进去
    @Override
    public void load(String path) throws IOException, ClassNotFoundException, UnmatchedSizeException {
        // 框架自带版本和棋盘大小的检查，不过，自定义的话得把默认实现复制一份。。。
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        try {
            loadedSave = (Save) in.readObject();
            if (!loadedSave.boardClass.equals(Board.class)) throw new ClassNotFoundException();
            if (checkSize && (loadedSave.height != Game.getHeight() || loadedSave.width != Game.getWidth()))
                throw new UnmatchedSizeException(Game.getWidth(), Game.getHeight(), loadedSave.width, loadedSave.height);
        } catch (ClassCastException ignored) {
            throw new ClassNotFoundException();
        }
        // 读出之前存的数据。如果想阻止读档，抛出异常，把loadedSave设为null
//        loadedSave = null;
        Scanner scanner = new Scanner(fileInputStream);
        System.out.println(scanner.nextLine());
        System.out.println(scanner.nextLine());
        System.out.println(scanner.nextLine());
        // 如果要手动改文件的话：
        // 1.用idea打开后会提示文件编码不对。
        // 这时候需要点右边的Reload in another encoding，选择ISO-8859-1，然后reload。不这么操作会导致修改的存档读不了。
        // 2.只能改你最后添加的几行！
    }

    @Override
    public void save(String path) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        // 把Save写进文档
        ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
        out.writeObject(Game.getSaveObject());
        fileOutputStream.write(
                String.format("%d %d %d %s\n",
                        Game.getBoard().getWidth(),
                        Game.getBoard().getHeight(),
                        Game.getCurrentPlayerIndex(),
                        Piece.PieceType.BING.name()
                ).getBytes(StandardCharsets.UTF_8)); // 在序列化后往文件里写点东西
        fileOutputStream.write("Test String b\n".getBytes(StandardCharsets.UTF_8));
        fileOutputStream.write("Test String c\n".getBytes(StandardCharsets.UTF_8));
        out.close();
    }
}
