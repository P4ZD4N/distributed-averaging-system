public class Log {
    public static String PREFIX = "";

    public static void log(String message) {
        System.out.println("[" + PREFIX + "] " + message);
        System.out.flush();
    }
}