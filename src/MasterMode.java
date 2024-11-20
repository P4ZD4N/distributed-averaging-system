import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterMode {

    private final int port;
    private final List<Integer> receivedNumbers;

    public MasterMode(int port, int initialNumber) {
        this.port = port;
        this.receivedNumbers = new ArrayList<>();
        receivedNumbers.add(initialNumber);
    }

    public void start() {
        System.out.println("Master mode started. Listening on port " + port + "...");

        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024];
            InetAddress masterAddress = InetAddress.getLocalHost();

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                if (packet.getAddress().equals(masterAddress)) continue;

                String receivedData = new String(packet.getData(), 0, packet.getLength()).trim();
                Double receivedValue = extractNumberFromReceivedData(receivedData);

                if (receivedValue == null) {
                    System.out.println("Received invalid data (not a number): " + receivedData);
                    continue;
                }

                if (receivedValue == -1) {
                    System.out.println("Received -1. Shutting down...");
                    broadcastMessage(socket, "Received -1. Shutting down...");
                    break;
                }

                handleReceivedValue(socket, receivedValue);
            }
        } catch (IOException e) {
            System.err.println("Error in master mode: " + e.getMessage());
        }
    }

    private void handleReceivedValue(DatagramSocket socket, double value) throws IOException {

        if (value == 0) {
            double average = calculateAverage();
            System.out.println("Calculated average: " + average);
            broadcastMessage(socket, "Calculated average: " + average);
            return;
        }

        receivedNumbers.add((int) value);
        System.out.println("Received value: " + (int) value);
    }

    private double calculateAverage() {
        return receivedNumbers.stream()
                .filter(num -> num != 0)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private void broadcastMessage(DatagramSocket socket, String message) throws IOException {

        byte[] messageBytes = message.getBytes();
        DatagramPacket broadcastPacket = new DatagramPacket(
                messageBytes,
                messageBytes.length,
                InetAddress.getByName("255.255.255.255"),
                port
        );

        socket.setBroadcast(true);
        socket.send(broadcastPacket);
        System.out.println("Broadcasted message: " + message);
    }

    private Double extractNumberFromReceivedData(String text) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) return Double.parseDouble(matcher.group());
        return null;
    }
}