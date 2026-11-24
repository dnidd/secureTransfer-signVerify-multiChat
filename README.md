### Project
A combination of signing/verifying & encryption/decryption  
Schneier (creator of Blowfish) recommends first to sign 
the encryption because it is easier with backup from the law
to claim that someone signed a real document.
Two standards for this are:
- Secure Socket Layer (SSL)
- Secure Multipurpose Internet Mail Extensions (S/MIME)

### SSL
SSL protects communication during transport, for example server-client.
SSL is a standard used with secure transfers over the internet
used by banks, e-commerce and others. SSL is used in the application on
low level connections/sockets. More modern is TLS still using the name SSL
and socket handshake is a TLS handshake:

```
SSLContext context = SSLContext.getInstance("TLS");

SSLSocketFactory factory = context.getSocketFactory();

SSLSocket socket =
    (SSLSocket) factory.createSocket("example.com", 443);

socket.startHandshake();
```


### Chat client

Classes:
- Client (main)
- Chat
- ChatSocketHandler (implements Runnable)

Chat client sending and receiving text messages.
The chat client connects to a chat server via stream socket.
When a chat client sends a text message to the server,
the server sends the text message to all connected chat clients.

The chat clients should be able to:
- Send text messages to chat server.
- Receive text messages from server and show the messages to the user.

### open separate terminals for separate chat clients.

Chat client should be able to be executed in the following three ways:
1. ```java Client (default host 127.0.0.1, default port 2000)```
2. ```java Client <host> (default port 2000)```
3. ```java Client <host> <port>```


TCP is used for transport of data packets.

This means that:
- the connection implements/simulates a continuous "telephone" connection.
- the transport is safe, the packets arrive, and they are put together in the right order.
- the process is somewhat slow.

Server and client makes agreements on safe communications with:
socket.startHandshake();

--------------------------------------------------------------------------------------

### Server
Classes:
- Server (main)
- CreateServer (init())
- ClientHandler (implements Runnable)

Server side stream sockets using SSLServerSocketFactory and SSLServerSocket.
Implements a chat server which can send and receive text messages.
Server can take incoming connection requests from chat clients
and place all chat clients in separate threads to be able to connect to new/more chat clients.
Every chat client thread is listening for a chat client to send a message. When a message is sent the
chat server will send/broadcast the message to all connected clients. The chat server keeps track on
all connected chat clients in a data structure.

The Chat server can be executed in two ways:

1. ```java Server (default port 2000)```
2. ```java Server <port>```


Using synchronized on data structure not to interrupt other actions.
https://docs.oracle.com/javase/tutorial/collections/implementations/wrapper.html


### Key store, trust store
For security a key store is created using key tools. The certificate (public key)
is copied to a trust store used by receiver to verify message/communication.<br>

🟢 <span style="color:lightgreen">Key store password: password</span>
<br>
🟢 <span style="color:lightgreen">Trust store password: password</span>



#### 1 Create key store:

 keytool -genkeypair -alias alias -keyalg RSA -validity 90 -keystore keystore
 <br>
#### 2 Examine the key store.:
keytool -list -v -keystore keystore<br>
   Posttype/Entry type: PrivateKeyEntry

#### 3 Export server certificate, creates file 'alias.cer':
keytool -export -alias alias -keystore keystore -rfc -file alias.cer<br>
<b>Examine certificate:</b><br> cat 'alias.cer'<br>

#### 4 Import certificate into a new trust store. Creates truststore:
keytool -import -alias aliascert -file alias.cer -keystore truststore
   
#### 5 Examine the truststore:
keytool -list -v -keystore truststore

## Run the applications:
- 🟢 Server (uses the keyStore containing private key):<br>
java -Djavax.net.ssl.keyStore=keystore -Djavax.net.ssl.keyStorePassword=password Server
- 🟢 Client (uses the trustStore containing the imported certificate):<br>
java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=password Client



SSLSocketClient, Oracle:<br>
https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm#GUID-AA1C27A1-2CA8-4309-B281-D6199F60E666__SSLSOCKETCLIENT.JAVA-32CFECE1

ClassFileServer, Oracle:<br>
https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm#GUID-3561ED02-174C-4E65-8BB1-5995E9B7282C__CLASSFILESERVER.JAVA-3314B74B

Creating a Keystore to Use with JSSE:<br>
https://docs.oracle.com/javase/10/security/java-secure-socket-extension-jsse-reference-guide.htm#JSSEC-GUID-3D26386B-BC7A-41BB-AC70-80E6CD147D6F