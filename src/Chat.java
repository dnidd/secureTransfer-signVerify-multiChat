import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.Scanner;

/**
 * Connects to server via SSL Socket.
 * Create communication Thread.
 * Take user input - send to Handler.
 * Takes messages coming from server.
 * Chat can read from the keyboard while ChatSocketHandler is listening to the server.
 */
public class Chat {

    // SSL used for secure sockets.
    private final SSLSocketFactory factory;
    private final SSLSocket socket;
    private ChatSocketHandler chatSocketHandler;

    /**
     * Constructor
     * @param host = server address/host
     * @param port = port
     */
    public Chat(String host, int port) throws IOException {
        // Connect SSL socket to server.
        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(host, port);
        // Server and client makes agreements on safe communications.
        socket.startHandshake();
    }

    /**
     * Create chatSocketHandler
     * Create and start Thread with chatSocketHandler injected.
     * Take incoming messages from keyboard/user.
     * Forward messages to chatSocketHandler for server transfer.
     * @throws IOException propagates to main in Client
     */
    public void startChat() throws IOException {

        chatSocketHandler = new ChatSocketHandler(this, socket);
        // Chat can read from the keyboard while
        // ChatSocketHandler is listening to the server.
        Thread thread = new Thread(chatSocketHandler);
        thread.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message = scanner.nextLine();

            if (message.equals("exit")) {
                chatSocketHandler.sendMessage("exit");
                socket.close();
                break;
            }

            chatSocketHandler.sendMessage(message);
        }
    }


    /**
     * Prints message to chat clients.
     * Messages fetched from server (in chatSocketHandler).
     * @param message = incoming message
     */
    public void getMessageFromChatHandler(String message) {
        System.out.println(message);
    }

}



