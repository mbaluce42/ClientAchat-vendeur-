package ServeurGeneriqueTCP.client;

import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.IOException;
import java.net.Socket;

public class ThreadClientDemande extends ThreadClient
{
    public ThreadClientDemande(Protocole protocole, Socket csocket, Logger logger) throws IOException
    {
        super(protocole, csocket, logger);
    }

    @Override
    public void run()
    {
        logger.Trace("TH Client (Demande) démarre...");
        super.run();  // Utilise le traitement standard de ThreadClient
        logger.Trace("TH Client (Demande) se termine.");
    }
}
