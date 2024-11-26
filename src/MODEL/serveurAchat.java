package MODEL;

import MODEL.networking.Prot_BSPP;
import MODEL.networking.SocketManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Queue;


public class serveurAchat implements Runnable
{
    private static final int MAX_THREADS = 10;//nombre de threads maximum(client)

    private static final String CONFIG_FILE = "config.properties";//fichier de configuration

    private static ServerSocket serverSocket;
    private static String PORT;
    private static int NB_THREADS;
    private static Thread[] threads;

    private static Queue<Socket> clientsQueue;

    private static void readConfig()
    {
        Properties prop = new Properties();
        File configFile = new File(CONFIG_FILE);

        try
        {
            if (!configFile.exists())
            {
                System.out.println("Fichier de configuration introuvable, on va cree avec valeur par defaut");
                PORT= "5001";
                NB_THREADS= 5;
                prop.setProperty("PORT", PORT);
                prop.setProperty("NB_THREADS", String.valueOf(NB_THREADS));
                prop.store(new FileOutputStream(CONFIG_FILE), null);

            }
            else
            {
                prop.load(new FileInputStream(CONFIG_FILE));
                PORT = prop.getProperty("PORT");
                NB_THREADS = Integer.parseInt(prop.getProperty("NB_THREADS"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void run()
    {
        while (true)
        {
            Socket clientSocket=null;
            synchronized (clientsQueue)
            {
                while (clientsQueue.isEmpty())
                {
                    try
                    {
                        clientsQueue.wait();
                    } catch (InterruptedException e) {
                        System.err.println("Erreur lors de l'attente de connexion : " + e.getMessage());
                    }
                }
                clientSocket = clientsQueue.poll();//recuperation de la socket du client
            }

            //traitement de la connexion
            if (clientSocket != null)
            {
                //recevoir la requete
                /*SocketManager.receiveData(clientSocket);

                //traiter la requete
                Prot_BSPP protocole = new Prot_BSPP(clientSocket);

*/

            }

        }

    }

    public static void main(String[] args)
    {
        readConfig();
        //creation socket d'ecoute
        try {
            serverSocket= SocketManager.createServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //creation du pool de threads
        threads = new Thread[NB_THREADS];
        for (int i = 0; i < NB_THREADS; i++)
        {
            threads[i] = new Thread("Thread " + (i+1));
            threads[i].start();
        }

        System.out.println("Demarrage du serveur d'achat sur le port " + PORT + " avec " + NB_THREADS + " threads\n");

        //attente des connexions
        while (true)
        {
            try
            {
                StringBuilder clientIp = new StringBuilder();
                Socket clientSocket = SocketManager.acceptConnection(serverSocket, clientIp);
                System.out.println("Connexion acceptee de " + clientIp.toString());

                //ajout du client dans la file d'attente
                synchronized (clientsQueue)
                {
                    if (clientsQueue.size() == MAX_THREADS)
                    {
                        System.out.println("File d'attente pleine, connexion refusee");
                        clientSocket.close();
                    } else {
                        clientsQueue.add(clientSocket);
                        clientsQueue.notify();
                    }
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
            }
        }



    }
}
