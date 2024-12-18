package ServeurGeneriqueTCP.exception;

import ServeurGeneriqueTCP.protocol.Reponse;

public class FinConnexionException extends Exception
{
    private final Reponse reponse;

    public FinConnexionException(Reponse reponse)
    {
        super("Fin de Connexion décidée par protocole");
        this.reponse = reponse;
    }

    public Reponse getReponse()
    {
        return reponse;
    }
}
