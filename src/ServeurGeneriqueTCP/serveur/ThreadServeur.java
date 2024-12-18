package ServeurGeneriqueTCP.serveur;

import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;


public abstract class ThreadServeur extends Thread
{
    protected int taillePool;
    protected int port;
    protected Protocole protocole;
    protected Logger logger;

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
}
