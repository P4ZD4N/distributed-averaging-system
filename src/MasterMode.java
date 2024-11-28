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
        Log.PREFIX = "M";

        Log.log("Master mode started. Listening on port " + port + "..." + "\n");

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
                    Log.log("Received invalid data (not a number): " + receivedData + "\n");
                    continue;
                }

                if (receivedValue == -1) {
                    Log.log("Received -1. Shutting down...");
                    broadcastMessage(socket, "Received -1. Shutting down..." + "\n");
                    break;
                }

                handleReceivedValue(socket, receivedValue);
            }
        } catch (IOException e) {
            Log.log("Error in master mode: " + e.getMessage() + "\n");
        }
    }

    private void handleReceivedValue(DatagramSocket socket, double value) throws IOException {

        if (value == 0) {
            double average = calculateAverage();
            Log.log("Calculated average: " + average);
            broadcastMessage(socket, "Calculated average: " + average + "\n");
            return;
        }

        receivedNumbers.add((int) value);
        Log.log("Received value: " + (int) value + "\n");
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
        Log.log("Broadcasted message: " + message);
    }

    private Double extractNumberFromReceivedData(String text) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) return Double.parseDouble(matcher.group());
        return null;
    }
}
