package ServeurGeneriqueTCP.serveur;

import MODEL.networking.Prot_BSPPnew;
import ServeurGeneriqueTCP.client.ThreadClientPool;
import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Properties;

public class ThreadServeurPool extends ThreadServeur
{

    private FileAttente connexionsEnAttente;
    private ThreadGroup pool;
    private static int taillePool;
    private static int port;

    public ThreadServeurPool(int port, Protocole protocole, int taillePool, Logger logger) throws IOException
    {
        super(port, protocole, logger);

        connexionsEnAttente = new FileAttente();
        pool = new ThreadGroup("POOL");
        this.taillePool = taillePool;
    }

    @Override
    public void run()
    {
        logger.Trace("Démarrage du TH Serveur (Pool)...");

        // Création du pool de threads
        try
        {
            for(int i=0; i<taillePool; i++)
            {
                new ThreadClientPool(protocole, connexionsEnAttente, pool, logger).start();
            }
        }
        catch(IOException ex)
        {
            logger.Trace("Erreur I/O lors de la création du pool de threads");
            return;
        }

        // Attente des connexions
        while(!this.isInterrupted())
        {
            Socket csocket;
            try
            {
                ssocket.setSoTimeout(2000);
                csocket = ssocket.accept();
                logger.Trace("Connexion acceptée, mise en file d'attente.");
                connexionsEnAttente.addConnexion(csocket);
            }
            catch(SocketTimeoutException ex)
            {
                // Pour vérifier l'interruption
            }
            catch(IOException ex)
            {
                logger.Trace("Erreur I/O");
            }
        }

        logger.Trace("TH Serveur (Pool) interrompu.");
        pool.interrupt();
    }

    public static void main(String[] args)
    {
        Logger logger = new Logger() {
            @Override
            public void Trace(String message)
            {
                System.out.println("SERVER: " + message);
            }
        };
        Protocole protocole = new Prot_BSPPnew(logger);
        ThreadServeurPool serveur = null;

        //serveur.readConfig();
        try
        {
            serveur = new ThreadServeurPool(50001, protocole, 2, logger);
        }
        catch(IOException ex)
        {
            logger.Trace("Erreur lors de la création du serveur: " + ex.getMessage());
            throw new RuntimeException("Erreur lors de la création du serveur: " + ex.getMessage());
            //System.exit(1);
        }

        serveur.start();

        while (true)
        {
            System.out.println("Appuyez sur 'q' pour quitter...");
            try
            {
                if (System.in.read() == 'q')
                {
                    serveur.interrupt();
                    break;
                }
            }
            catch(IOException ex)
            {
                logger.Trace("Erreur I/O: " + ex.getMessage());
            }
        }

    }
}
