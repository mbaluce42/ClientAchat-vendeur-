package ServeurGeneriqueTCP.serveur;

import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.Properties;


public abstract class ThreadServeur extends Thread
{
    protected int taillePool;
    protected int port;
    protected Protocole protocole;
    protected Logger logger;
    private static final String CONFIG_FILE = "serveurPool_config.txt";
    private static final String KEYSTORE_SERVER_FILE = "src/server_keystore.jks";
    private static final String KEYSTORE_SERVER_PASSWORD = "serverpass";


    protected ServerSocket ssocket;

    // Constructeur pour le mode "à la demande"
    public ThreadServeur(int port, Protocole protocole, Logger logger,boolean secure) throws IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;
        this.logger = logger;
        //ssocket = new ServerSocket(port);
        //utilisation ssl/tls
        if(secure)
        {
            try
            {
                KeyStore serverKs = KeyStore.getInstance("JKS");
                try (FileInputStream fileInputStream = new FileInputStream(KEYSTORE_SERVER_FILE))
                {
                    serverKs.load(fileInputStream,KEYSTORE_SERVER_PASSWORD.toCharArray());
                }

                //config gestionnaire de clés
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(serverKs, KEYSTORE_SERVER_PASSWORD.toCharArray());

                //config gestionnaire de confiance
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                trustManagerFactory.init(serverKs);

                //config SSLContext
                SSLContext SsIC = SSLContext.getInstance("TLSv1.3");
                SsIC.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                //création du serveur
                ssocket = SsIC.getServerSocketFactory().createServerSocket(port);
            }
            catch (Exception e)
            {
                logger.Trace("Erreur lors de la création du serveur sécurisé: " + e.getMessage());
                System.exit(1);
            }
        }
        else
        {
            ssocket = new ServerSocket(port);
        }
    }

    // Constructeur pour le mode "pool"
    public ThreadServeur(int port, Protocole protocole, int taillePool, Logger logger, boolean secure) throws IOException
    {
        super("TH Serveur (port=" + port + ",protocole=" + protocole.getNom() + ")");
        this.port = port;
        this.protocole = protocole;
        this.logger = logger;
        this.taillePool = taillePool;
        //ssocket = new ServerSocket(port);

        //utilisation ssl/tls

        if(secure)
        {
            try
            {
                KeyStore serverKs = KeyStore.getInstance("JKS");
                try (FileInputStream fileInputStream = new FileInputStream(KEYSTORE_SERVER_FILE))
                {
                    serverKs.load(fileInputStream,KEYSTORE_SERVER_PASSWORD.toCharArray());
                }

                //config gestionnaire de clés
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(serverKs, KEYSTORE_SERVER_PASSWORD.toCharArray());

                //config gestionnaire de confiance
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                trustManagerFactory.init(serverKs);

                //config SSLContext
                SSLContext SsIC = SSLContext.getInstance("TLSv1.3");
                SsIC.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                //création du serveur
                ssocket = SsIC.getServerSocketFactory().createServerSocket(port);
            }
            catch (Exception e)
            {
                logger.Trace("Erreur lors de la création du serveur sécurisé: " + e.getMessage());
                System.exit(1);
            }
        }
        else
        {
            ssocket = new ServerSocket(port);
        }
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
