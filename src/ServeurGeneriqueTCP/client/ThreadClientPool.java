package ServeurGeneriqueTCP.client;

import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.serveur.FileAttente;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.IOException;

public class ThreadClientPool extends ThreadClient
{
    private FileAttente connexionsEnAttente;

    public ThreadClientPool(Protocole protocole, FileAttente file, ThreadGroup groupe, Logger logger) throws IOException
    {
        super(protocole, groupe, logger);
        this.connexionsEnAttente = file;
    }

    @Override
    public void run()
    {
        logger.Trace("TH Client (Pool) démarre...");
        boolean interrupted = false;

        while(!interrupted)
        {
            try
            {
                // Attente d'une connexion dans la file
                logger.Trace("Attente d'une connexion...");
                csocket = connexionsEnAttente.getConnexion();
                logger.Trace("Connexion prise en charge.");

                // Traitement de la connexion avec la méthode de la classe mère
                super.run();

                // Fermeture de la socket après traitement
                if(csocket != null)
                {
                    try
                    {
                        csocket.close();
                    }
                    catch (IOException e)
                    {
                        logger.Trace("Erreur fermeture socket: " + e.getMessage());
                    }
                }

            }
            catch (InterruptedException e)
            {
                logger.Trace("Demande d'interruption...");
                interrupted = true;
            }
        }

        logger.Trace("TH Client (Pool) se termine.");
    }
}
