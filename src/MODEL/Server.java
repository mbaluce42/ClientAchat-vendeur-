package MODEL;

import MODEL.networking.Prot_BSPP;
import MODEL.networking.SocketManager;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Server {
    private static final int MAX_THREADS = 100;
    private static final String CONFIG_FILE = "server_config.txt";

    private static ServerSocket serverSocket;
    private static int PORT_PAYMENT;
    private static int NB_THREADS;
    private static Thread[] threads;
    private static boolean isRunning = true;

    private static final Queue<Socket> clientsQueue = new LinkedList<>();
    private static final Object queueLock = new Object();
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static class WorkerThread extends Thread {
        @Override
        public void run()
        {
            while (isRunning) {
                Socket clientSocket = null;
                synchronized (queueLock) {
                    while (isRunning && clientsQueue.isEmpty()) {
                        try {
                            queueLock.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    if (!isRunning) return;
                    clientSocket = clientsQueue.poll();
                }

                if (clientSocket != null) {
                    processClientConnection(clientSocket);
                }
            }
        }

        private void processClientConnection(Socket socket)
        {
            try {
                Prot_BSPP bsppHandler = new Prot_BSPP(socket);  // Passer le socket au constructeur
                while (isRunning) {
                    byte[] requestData = SocketManager.receiveData(socket);
                    if (requestData.length == 0) break;

                    String request = new String(requestData);
                    logger.info(getName() + " - Requête reçue: " + request);

                    String response = bsppHandler.BSPP_Server_Parser(request); // Utiliser BSPP_Parser au lieu de parseRequest
                    System.out.println("Réponse : " + response);
                    if (response != null)
                    {
                        byte[] responseData = response.getBytes();
                        SocketManager.sendData(socket, responseData, responseData.length);
                    }
                }
            } catch (IOException e) {
                logger.warning(getName() + " - Erreur: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warning("Erreur fermeture socket: " + e.getMessage());
                }
            }
        }
    }




    public static void main(String[] args)
    {
        initServer();
        Runtime.getRuntime().addShutdownHook(new Thread(Server::handleShutdown));

        threads = new Thread[NB_THREADS];
        for (int i = 0; i < NB_THREADS; i++)
        {
            threads[i] = new WorkerThread();
            threads[i].setName("Worker-" + i);
            threads[i].start();
        }

        acceptClients();
    }

    private static void initServer()
    {
        readConfig();
        try {
            serverSocket = SocketManager.createServerSocket(String.valueOf(PORT_PAYMENT));
            logger.info("Serveur démarré sur port " + PORT_PAYMENT);
        } catch (IOException e) {
            logger.severe("Erreur création serveur: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void acceptClients()
    {
        while (isRunning) {
            try {
                StringBuilder clientIp = new StringBuilder();
                Socket clientSocket = SocketManager.acceptConnection(serverSocket, clientIp);

                synchronized (queueLock) {
                    if (clientsQueue.size() < MAX_THREADS) {
                        clientsQueue.offer(clientSocket);
                        queueLock.notify();
                        logger.info("Client connecté: " + clientIp);
                    } else {
                        clientSocket.close();
                        logger.warning("File pleine, connexion refusée");
                    }
                }
            } catch (IOException e) {
                if (isRunning) {
                    logger.warning("Erreur acceptation client: " + e.getMessage());
                }
            }
        }
    }

        private static void readConfig()
        {
            Properties props = new Properties();
            File configFile = new File(CONFIG_FILE);

            try {
                if (!configFile.exists()) {
                    PORT_PAYMENT = 50001;  // Port différent pour le serveur BSPP
                    NB_THREADS = 2;
                    props.setProperty("PORT_PAYMENT", String.valueOf(PORT_PAYMENT));
                    props.setProperty("NB_THREADS", String.valueOf(NB_THREADS));
                    props.store(new FileOutputStream(configFile), null);
                } else {
                    props.load(new FileInputStream(configFile));
                    PORT_PAYMENT = Integer.parseInt(props.getProperty("PORT_PAYMENT", "50001"));
                    NB_THREADS = Integer.parseInt(props.getProperty("NB_THREADS", "2"));
                }
            } catch (IOException e) {
                logger.severe("Erreur configuration: " + e.getMessage());
                System.exit(1);
            }
        }

    private static void handleShutdown()
    {
        isRunning = false;
        try {
            serverSocket.close();
            synchronized (queueLock)
            {
                queueLock.notifyAll();
            }
            for (Thread thread : threads)
            {
                thread.join(5000);
            }
            //OBEP.close();
        }
        catch (Exception e) {
            logger.severe("Erreur arrêt: " + e.getMessage());
        }
    }
}