import java.net.DatagramSocket;
import java.net.SocketException;

public class DAS {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java DAS <port> <number>");
            return;
        }

        int port;
        int number;

        try {
            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error: Both arguments must be integers.");
            return;
        }

        System.out.println("Starting UDP server");

        System.out.println("Opening UDP socket");

        if (isMaster(port)) {
            runAsMaster(port, number);
            return;
        }

        runAsSlave(port);
    }

    private static boolean isMaster(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Port " + port + " is available. Running as master.");
            return true;
        } catch (SocketException e) {
            System.out.println("Port " + port + " is already in use. Running as slave.");
            return false;
        }
    }

    private static void runAsMaster(int port, int number) {

        System.out.println("Starting in master mode...");

        MasterMode masterMode = new MasterMode(port, number);
        masterMode.start();
    }

    private static void runAsSlave(int port) {
        System.out.println("Starting in slave mode...");
    }
}
