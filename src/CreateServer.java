import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Initialize and create connection using SSL.
 */
public class CreateServer {

    private final SSLServerSocketFactory factory;
    private SSLServerSocket sslServerSocket;
    private final int port;


    /**
     * Constructor.
     * @param port = default or arg port.
     */
    public CreateServer(int port) {
        this.port = port;
        this.factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        this.sslServerSocket = null;
    }


    /**
     * Initialize SSLServerSocket.
     * @throws IOException e.
     */
    public void init() throws IOException {

        try {
            // SSLServerSocket
            sslServerSocket = (SSLServerSocket) factory.createServerSocket(port);

            // The client do not need to be authenticated in the server side.
            sslServerSocket.setNeedClientAuth(false);

            startServer();
        } catch (IOException e) {
            System.err.println("Server connection failed." );
        }
    }



    /**
     * sslServerSocket.accept() returns Socket when connection to client accepted.
     * Server communicates with client with returned Socket.
     * Create ClientHandler to be used in new separate Thread.
     * New Thread contains connection between client and server.
     * Server can handle multiple clients.
     */
    public void startServer() {
        System.out.println("Starting server on port " + sslServerSocket.getLocalPort());

        // Address/name of local host.
        String ip;

        try {
            ip = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try {
            while(!sslServerSocket.isClosed()) {
                // Created connection on socket.
                SSLSocket socket = (SSLSocket) sslServerSocket.accept();

                ClientHandler clientHandler = new ClientHandler(socket, ip);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            terminateServerSocket();
        }
    }


    public void terminateServerSocket() {
        try {
            if(sslServerSocket != null) {
                sslServerSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
