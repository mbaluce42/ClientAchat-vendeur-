package ServeurGeneriqueTCP.serveur;

import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;


public abstract class ThreadServeur extends Thread
{
    protected int taillePool;
    protected int port;
    protected Protocole protocole;
    protected Logger logger;
    private static final String CONFIG_FILE = "serveurPool_config.txt";

    protected ServerSocket ssocket;

    // Constructeur pour le mode "à la demande"
    public ThreadServeur(int port, Protocole protocole, Logger logger) throws IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;
        this.logger = logger;
        ssocket = new ServerSocket(port);
    }

    // Constructeur pour le mode "pool"
    public ThreadServeur(int port, Protocole protocole, int taillePool, Logger logger) throws IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;
        this.logger = logger;
        this.taillePool = taillePool;
        ssocket = new ServerSocket(port);
    }

    public void readConfig()
    {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        try {
            if (!configFile.exists())
            {
                port = 50001;
                taillePool = 2;
                props.setProperty("PORT_PAYMENT", String.valueOf(port));
                props.setProperty("NB_THREADS", String.valueOf(taillePool));
                props.store(new FileOutputStream(configFile), null);
            }
            else
            {
                props.load(new FileInputStream(configFile));
                port = Integer.parseInt(props.getProperty("PORT_PAYMENT", "50001"));
                taillePool = Integer.parseInt(props.getProperty("NB_THREADS", "2"));
            }
        } catch (IOException e)
        {
            logger.Trace("Erreur configuration: " + e.getMessage());
            System.exit(1);
        }
    }
}
