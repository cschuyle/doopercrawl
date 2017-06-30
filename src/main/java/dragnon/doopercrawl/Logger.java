package dragnon.doopercrawl;

public class Logger {
    public static void error(Exception e) {
        System.err.println("UNEXPECTED ERROR WILL ROBINSON!");
        e.printStackTrace(System.err);
    }

    public static void error(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }

    public static void warn(String message) {
        System.err.println(message);
    }
}
