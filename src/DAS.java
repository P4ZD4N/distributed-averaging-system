import java.net.DatagramSocket;
import java.net.SocketException;

public class DAS {
    public static void main(String[] args) {
        Log.PREFIX = "D";

        if (args.length != 2) {
            Log.log("Usage: java DAS <port> <number>");
            return;
        }

        int port;
        int number;

        try {
            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Log.log("Error: Both arguments must be integers.");
            return;
        }

        if (isMaster(port)) {
            runAsMaster(port, number);
            return;
        }

        runAsSlave(port, number);
    }

    private static boolean isMaster(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            Log.log("Port " + port + " is available. Running as master.");
            return true;
        } catch (SocketException e) {
            Log.log("Port " + port + " is already in use. Running as slave.");
            return false;
        }
    }

    private static void runAsMaster(int port, int number) {
        MasterMode masterMode = new MasterMode(port, number);
        masterMode.start();
    }

    private static void runAsSlave(int port, int number) {
        SlaveMode slaveMode = new SlaveMode(port, number);
        slaveMode.start();
    }
}
