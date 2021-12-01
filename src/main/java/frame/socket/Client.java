package frame.socket;

import frame.Game;
import frame.action.Action;
import frame.player.Player;
import frame.player.PlayerInfo;
import frame.player.PlayerManager;
import frame.player.PlayerType;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

public class Client {
    public static Socket socket;
    private static String clientName;
    private static BufferedReader br;
    private static ObjectInputStream in;

    public static boolean establishConnection(String ip, String name) {
        clientName = name;
        try {
            socket = new Socket(ip, 6930);
//            socket.setSoTimeout(5000);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("ADD\n");
            bw.write(name);
            bw.flush();
            socket.shutdownOutput();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s = br.readLine();
            if (!Objects.equals(s, "SUCCESS")) {
                socket.close();
                return false;
            }
            in = new ObjectInputStream(socket.getInputStream());
            listen();
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static void listen() {
        Thread t = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String s = br.readLine();
                    switch (s) {
                        case "PLAYERS": {
                            Player[] players = (Player[]) in.readObject();
                            Map<String, PlayerInfo> playerInfos = (Map<String, PlayerInfo>) in.readObject();
                            PlayerManager.updatePlayersFromServer(players, playerInfos);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public static void sendAction(Action action) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(action);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendCancel() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write("Cancel");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InetAddress discoverServer() {
        DatagramSocket c;
        InetAddress result = null;
        try {
            c = new DatagramSocket();
            c.setSoTimeout(5000);
            c.setBroadcast(true);
            byte[] sendData = "BoardGame_Frame_Discovery_packet".getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
            } catch (Exception ignored) {
            }
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6930);
                        c.send(sendPacket);
                    } catch (Exception ignored) {
                    }
                }
            }

            byte[] buffer = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            c.receive(receivePacket);

            String message = new String(receivePacket.getData()).trim();
            if (message.equals("BoardGame_Frame_Response_packet")) {
                result = receivePacket.getAddress();
                System.out.println(result);
            }
            c.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}