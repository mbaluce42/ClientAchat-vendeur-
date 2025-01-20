
import MODEL.networking.Prot_BSPP;
import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.serveur.ThreadServeur;
import ServeurGeneriqueTCP.serveur.ThreadServeurPool;
import ServeurGeneriqueTCP.serveur.ThreadServeurDemande;
import ServeurGeneriqueTCP.utils.Logger;
import ServeurGeneriqueTCP.utils.ConfigServeurManager;

public class MainServeur
{
    /*private static final int PORT = 50000;
    private static final int TAILLE_POOL = 3;*/
    private static final boolean SECURE = true;

    // Logger simple pour la console
    static class ConsoleLogger implements Logger
    {
        @Override
        public void Trace(String message)
        {
            System.out.println("[" + Thread.currentThread().getName() + "] " + message);
        }
    }

    public static void main(String[] args)
    {
        // Paramètres du serveur
        Logger logger = new ConsoleLogger();
        ThreadServeur serveur = null;

        ConfigServeurManager configServeurManager = new ConfigServeurManager();
        int port = configServeurManager.getPort();
        int taillePool = configServeurManager.getTaillePool();
        boolean mode = configServeurManager.getModePool();

        try
        {
            // Création du protocole
            Protocole protocole = new Prot_BSPP(logger);


            // Création du serveur -> mode pool ou mode à la demande
            //si mode = true, on utilise le mode pool sinon on utilise le mode à la demande
            if (mode)
            {
                serveur = new ThreadServeurPool(port, protocole,taillePool, logger,SECURE);
                logger.Trace("Démarrage du serveur en mode pool sur le port " + port +
                        " avec " + taillePool + " threads");
            }
            else
            {
                serveur = new ThreadServeurDemande(port, protocole, logger,SECURE);
                logger.Trace("Démarrage du serveur en mode à la demande sur le port " + port);
            }

            // Démarrage du serveur
            serveur.start();

            // Attente de l'arret du serveur grace à la touche entrée
            logger.Trace("Serveur démarré. Appuyez sur Entrée pour arrêter...");
            System.in.read();// Bloquant jusqu'à la saisie de la touche Entrée

            // Arrêt propre du serveur
            serveur.interrupt();
            serveur.join(5000);  // Attente max de 5 secondes

            logger.Trace("Serveur arrêté");

        }
        catch (Exception ex) {
            System.err.println("Erreur: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}