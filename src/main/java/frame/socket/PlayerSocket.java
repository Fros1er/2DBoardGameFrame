package frame.socket;

import java.net.Socket;

public class PlayerSocket {
    private Socket socket;
    public PlayerSocket(Socket socket) {
        this.socket = socket;
    }
}
