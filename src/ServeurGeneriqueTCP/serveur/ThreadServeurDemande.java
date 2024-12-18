package ServeurGeneriqueTCP.serveur;

import ServeurGeneriqueTCP.client.ThreadClientDemande;
import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadServeurDemande extends ThreadServeur
{

    public ThreadServeurDemande(int port, Protocole protocole, Logger logger) throws IOException
    {
        super(port, protocole, logger);
    }

    @Override
    public void run()
    {
        logger.Trace("Démarrage du TH Serveur (Demande)...");

        while(!this.isInterrupted())
        {
            try
            {
                // Timeout de 2 secondes pour vérifier périodiquement l'interruption
                ssocket.setSoTimeout(2000);

                // Attente d'une connexion client
                Socket csocket = ssocket.accept();
                logger.Trace("Connexion acceptée, création TH Client");

                // Création et démarrage d'un nouveau thread client
                ThreadClientDemande threadClient = new ThreadClientDemande(protocole, csocket, logger);
                threadClient.start();

            }
            catch (SocketTimeoutException e)
            {
                // Timeout normal, permet de vérifier l'interruption
            }
            catch (IOException e)
            {
                if (!this.isInterrupted())
                {
                    logger.Trace("Erreur E/S : " + e.getMessage());
                }
            }
        }

        logger.Trace("TH Serveur (Demande) interrompu.");

        try
        {
            if (ssocket != null)
            {
                ssocket.close();
            }
        }
        catch (IOException e)
        {
            logger.Trace("Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
    }
}
