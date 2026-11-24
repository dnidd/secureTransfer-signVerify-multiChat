import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class responsible for taking incoming messages from clients.
 * Broadcast incoming messages to all connected clients.
 * Class used in separate threads for each connected client.
 * implements Runnable to be used in thread.
 */
public class ClientHandler implements Runnable {

    // thread-safe collections: one thread at a time modify the List.
    // Using synchronized not to interrupt other actions on data structure.
    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    // Socket is the established connection between the client and the server.
    private Socket socket;

    // Read from clients.
    private BufferedReader in;

    // Broadcast to clients.
    private PrintWriter out;

    // serverSocket/host address.
    private String ip;

    // Connected client host.
    private String clientHost;

    // Server port.
    private int port;


    /**
     * Constructor
     * @param socket = returned socket from accepted connection.
     * @param ip = server host address.
     */
    public ClientHandler(Socket socket, String ip) {
        try {
            this.ip = ip;
            this.socket = socket;
            this.port = socket.getLocalPort();
            this.clientHost = socket.getInetAddress().getHostAddress();
            createStreams();

            clients.add(this); // Add client to data structure

            // Server terminal: server info + number of connected client
            System.out.println("Server on host: " + ip
                    + ", port: " + port
                    + ", clients: " + clients.size());

            updateTitle();

            // Broadcast connected clients.
            broadcastMessage(clientHost + " connected.");
        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }


    /**
     * Method runs in separate thread.
     * Reads incoming messages from clients.
     * Broadcasting incoming messages to clients.
     * Listening/waiting for messages is a blocking operation - needs a separate thread.
     */
    @Override
    public void run() {
        String messageComingFromClient;

            try {
                while((messageComingFromClient = in.readLine()) != null) {

                    // If user types "exit" in client -> leave.
                    if (messageComingFromClient.equals("exit")) {
                        break;
                    }

                    // Broadcast clients messages.
                    broadcastMessage(clientHost + ": " + messageComingFromClient);
                }
            } catch (IOException e) {
                System.err.println("User left: " + e.getMessage());
            } finally {
                closeEverything(socket, in, out);
            }
    }


    /**
     * Broadcast messages to all clients.
     * Under Tips on: "2.1.2 Stream sockets på serversidan".
     * Using synchronized not to interrupt other actions on data structure.
     * @param sendMessage = message to send.
     */
    public void broadcastMessage(String sendMessage) {
        // Show message in server terminal.
        System.out.println(sendMessage);

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.out.println(sendMessage);
            }
        }
    }


    /**
     * When user exits, remove from ArrayList clients.
     * Using synchronized not to interrupt other actions on data structure.
     * Inform in server terminal.
     * Broadcast to users about user exit.
     */
    public void removeClient() {
        boolean userWasRemoved;

        synchronized (clients) {
            // true if client was in data structure.
            userWasRemoved = clients.remove(this);
        }

        if (userWasRemoved) {
            return;
        }

        // Print in server terminal.
        System.out.println("Server: " + clientHost + " disconnected.");
        System.out.println("Server: " + ip
                + " port: " + port
                + " clients: " + clients.size());

        updateTitle();

        // Broadcast to other users (chat clients) that client left the chat.
        broadcastMessage("Server: " + clientHost + " left the chat.");
    }

    private void updateTitle() {
        System.out.println("Server: " + ip + ":" + port + " clients: " + clients.size());
        System.out.flush();
    }


    /**
     * Close connections & streams when user exit chat.
     * @param socket = server-client connection.
     * @param in = BufferedReader.
     * @param out = PrintWriter.
     */
    public void closeEverything(Socket socket, BufferedReader in, PrintWriter out) {
        // Remove client from ArrayList
        removeClient();
        // Avoid null pointer exceptions
        try {
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create streams to clients via socket.
     * out: to client.
     * in: from client.
     * @throws IOException propagates to constructor.
     */
    private void createStreams() throws IOException {
        // write stream to client
        out = new PrintWriter(                                            // enables: .println(message)
                new OutputStreamWriter(                                   // text to bytes
                        socket.getOutputStream(), "UTF-8"),   // create output stream
                true);                                                    // flush buffer

        // get text from client
        in = new BufferedReader(                                          // enables: in.readLine()
                new InputStreamReader(                                    // byte to text
                        socket.getInputStream(),  "UTF-8"));  // create input stream

    }


}
