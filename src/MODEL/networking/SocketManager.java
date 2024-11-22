package MODEL.networking;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SocketManager {
    public static final int BUFFER_SIZE = 1500;

    /**
     * Creates a client socket and connects to the specified server.
     * @param serverIp The server's IP address
     * @param serverPort The server's port
     * @return The connected socket
     * @throws IOException If an I/O error occurs
     */
    public static Socket createClientSocket(String serverIp, String serverPort) throws IOException
    {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(serverIp, Integer.parseInt(serverPort));
            return clientSocket;
        } catch (IOException e) {
            if (clientSocket != null) {
                clientSocket.close();
            }
            throw e;
        }
    }

    /**
     * Accepts a connection on a server socket and optionally retrieves client IP.
     * @param serverSocket The server socket listening for connections
     * @param clientIp StringBuilder to store client IP (can be null)
     * @return The socket for the accepted connection
     * @throws IOException If an I/O error occurs
     */
    public static Socket acceptConnection(ServerSocket serverSocket, StringBuilder clientIp)
            throws IOException {
        Socket clientSocket = serverSocket.accept();

        if (clientIp != null) {
            String remoteAddress = clientSocket.getInetAddress().getHostAddress();
            clientIp.append(remoteAddress);
        }

        return clientSocket;
    }

    /**
     * Sends data through the socket.
     * @param socket The socket to send data through
     * @param data The data to send
     * @param length The length of data to send
     * @return The number of bytes sent
     * @throws IOException If an I/O error occurs
     */
    public static int sendData(Socket socket, byte[] data, int length) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(data, 0, length);
        out.flush();
        return length;
    }

    /**
     * Receives data from the socket.
     * @param socket The socket to receive data from
     * @return The received data as byte array
     * @throws IOException If an I/O error occurs
     */
    public static byte[] receiveData(Socket socket) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream in = socket.getInputStream();
        int bytesRead = in.read(buffer);

        if (bytesRead == -1) {
            return new byte[0];
        }

        return Arrays.copyOf(buffer, bytesRead);
    }

    public static void main(String[] args)
    {
        //test socket
        //!!!!! mettre la bonne ip du serveurEncodage !!!!!!!
        Socket client = null;
        try
        {
            client = SocketManager.createClientSocket("192.168.163.128", "50000");
            // Envoi/Réception
            byte[] data = "GET_AUTHORS#".getBytes();

            SocketManager.sendData(client, data, data.length);
            byte[] received = SocketManager.receiveData(client);

            String messageUtf8 = new String(received, "UTF-8");
            System.out.println("Message reçu (UTF-8) : " + messageUtf8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

}