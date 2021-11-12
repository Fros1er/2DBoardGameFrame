package frame.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private static final int PORT = 6789;
    private ServerSocket server;
    private ExecutorService threadPool;
    public SocketServer() throws IOException {
        server = new ServerSocket(PORT);
        System.out.println("Server started.");
        threadPool = Executors.newFixedThreadPool(10);
        while (true) {
            Socket socket = server.accept();

            Runnable runnable=()->{
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    StringBuilder sb = new StringBuilder();
                    while ((len = inputStream.read(bytes)) != -1) {
                        sb.append(new String(bytes, 0, len, "UTF-8"));
                    }
                    System.out.println("get message from client: " + sb);
                    inputStream.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            threadPool.submit(runnable);
        }
    }
    public void close() {
        threadPool.shutdown();
    }
}