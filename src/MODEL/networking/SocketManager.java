package MODEL.networking;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SocketManager {
    public static final int BUFFER_SIZE = 1500;

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

    //*fait un appel à socket() pour créer la socket
    //o construit l’adresse réseau de la socket par appel à getaddrinfo()
    //o fait appel à bind() pour lier la socket à l’adresse réseau*/
    public static ServerSocket createServerSocket(String port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
        return serverSocket;
    }

    public static Socket acceptConnection(ServerSocket serverSocket, StringBuilder clientIp)
            throws IOException {
        Socket clientSocket = serverSocket.accept();

        if (clientIp != null) {
            String remoteAddress = clientSocket.getInetAddress().getHostAddress();
            clientIp.append(remoteAddress);
        }

        return clientSocket;
    }

    public static int sendData(Socket socket, byte[] data, int length) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(data, 0, length);
        out.flush();
        return length;
    }

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

    public static void sendObject(Socket socket, Serializable object) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(object);
        out.flush();
    }

    public static Object receiveObject(Socket socket) throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return in.readObject();
    }


    public static void main(String[] args)
    {
        //test socket
        //!!!!! mettre la bonne ip du serveurEncodage !!!!!!!
        Socket client = null;
        /*try
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
        }*/

        try {
            client = SocketManager.createClientSocket("192.168.163.128", "50000");

            // Envoi d'un objet
            String testObject = "Hello, Server!";
            SocketManager.sendObject(client, testObject);

            // Réception d'un objet
            Object receivedObject = SocketManager.receiveObject(client);
            if (receivedObject instanceof String)
            {
                System.out.println("Message reçu (Objet) : " + receivedObject);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}