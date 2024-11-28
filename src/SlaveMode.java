import java.net.*;

public class SlaveMode {

    private final int port;
    private final int number;

    public SlaveMode(int port, int number) {
        this.port = port;
        this.number = number;
    }

    public void start() {
        Log.PREFIX = "S";

        Log.log("Slave mode started..." + "\n");

        DatagramSocket socket = null;
        int randomPort;
        boolean portFound = false;

        try {
            while (!portFound) {
                randomPort = (int) (Math.random() * 65536) + 1;
                try {
                    socket = new DatagramSocket(randomPort);
                    portFound = true;
                    Log.log("Found free port: " + randomPort);
                    Log.log("Opening UDP socket..." + "\n");
                } catch (SocketException e) {
                    Log.log("Port " + randomPort + " is already in use, trying again...");
                }
            }

            byte[] buffer = String.valueOf(number).getBytes();
            InetAddress receiverAddress = InetAddress.getByName("127.0.0.1");
            DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, receiverAddress, port);

            socket.send(packetToSend);
            Log.log("Packet sent!");

        } catch (Exception e) {
            Log.log("Error in slave mode: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                Log.log("Socket closed.");
            }
        }
    }
}
