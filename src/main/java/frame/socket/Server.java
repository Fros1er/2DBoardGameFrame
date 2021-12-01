package frame.socket;

import frame.player.PlayerManager;
import frame.player.PlayerType;

import java.io.*;
import java.net.*;

public class Server {
    private DatagramSocket datagramSocket;
    private boolean discoveryServiceEnables = false;
    private static ServerSocket serverSocket;

    private static void handleInput(String command, Socket socket) throws IOException {
        if (command == null) return;
        switch (command) {
            case "ADD": {
                int pos = PlayerManager.getNextRemotePlayerIndex();
                if (pos != -1) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PlayerManager.setPlayer(pos, PlayerType.REMOTE, br.readLine());
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write("SUCCESS\n");
                    bw.flush();
                    bw.write("PLAYERS\n");
                    bw.flush();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    Object[] playerInfos = PlayerManager.getPlayersForServer();
                    out.writeObject(playerInfos[0]);
                    out.writeObject(playerInfos[1]);
                    out.flush();
                }
            }
        }
    }

    private static void listenConnectionRequests() {
        try {
            serverSocket = new ServerSocket(6930);

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端:" + socket.getInetAddress().getHostAddress() + "已连接到服务器");
                Thread t = new Thread(() -> {
                    while (socket.isConnected()) {
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            handleInput(br.readLine(), socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDiscoveryService() {
        discoveryServiceEnables = true;
        Thread t = new Thread(() -> {
            try {
                //6930 = 11 * 45 * 14
                datagramSocket = new DatagramSocket(6930, InetAddress.getByName("0.0.0.0"));
                datagramSocket.setBroadcast(true);
                while (discoveryServiceEnables) {
                    System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                    byte[] buffer = new byte[15000];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                    System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                    String message = new String(packet.getData()).trim();
                    if (message.equals("BoardGame_Frame_Discovery_packet")) {
                        byte[] sendData = ("BoardGame_Frame_Response_packet").getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        datagramSocket.send(sendPacket);

                        System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        t.start();
    }

    public static void startService() {
        Thread t = new Thread(Server::listenConnectionRequests);
        t.start();
    }

    public static void stopAllService() {
//        discoveryServiceEnables = false;
//        datagramSocket.close();
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
