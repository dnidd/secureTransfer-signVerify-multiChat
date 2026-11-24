import java.io.IOException;

/**
 * Entry point.
 * Takes command line args host and port or defaults.
 * Creates Chat depending on args.
 */
public class Client {

    private static final String DEFAULT_HOST =  "127.0.0.1";
    private static final int DEFAULT_PORT = 2000;

    public static void main(String[] args) throws IOException {

        // 0 args -> default host + port
        if (args.length == 0) {
            try {
                Chat chat = new Chat(DEFAULT_HOST, DEFAULT_PORT);
                chat.startChat();
            } catch (IOException e) {
                System.err.println("Server connection failed.");
            }
        }

        // 1 args -> set host, default port
        else if(args.length == 1) {
            try {
                Chat chat = new Chat(args[0], DEFAULT_PORT);
                chat.startChat();
            } catch (IOException e) {
                System.err.println("Server connection failed.");
            }
        }

        // 2 args -> set host + port
        else if(args.length == 2 ) {
            try {
                int port = Integer.parseInt(args[1]);
                Chat chat = new Chat(args[0], port);
                chat.startChat();
            } catch (NumberFormatException e) {
                System.err.println("Port must be a number.");
            } catch (IOException e) {
                System.err.println("Server connection failed.");
            }
        }

        else {
            System.err.println("Usage: java Client <host> <port>");
        }

    }

}