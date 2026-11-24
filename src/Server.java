import java.io.IOException;

/**
 * Entry point for chat server.
 * If args are present - use args, else use default port.
 * When client connects, SSLServerSocket returns Socket:
 * inside: createServer.init() -> startServer()
 */
public class Server {

    private static final int DEFAULT_PORT = 2000;

    public static void main(String[] args) throws IOException {

        int port = DEFAULT_PORT;

        // Check if default port or args port will be used.
        if(args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port must be an integer. Usage: java Server <port>");
                return;
            }

        } else if (args.length > 1) {
                System.err.println("Usage: java Server <port>");
            return;
        }

        CreateServer createServer = new CreateServer(port);
        createServer.init();
    }

}













