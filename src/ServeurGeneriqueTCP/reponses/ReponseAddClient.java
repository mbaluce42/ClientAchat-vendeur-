package ServeurGeneriqueTCP.reponses;

public class ReponseAddClient extends ReponseBSPP
{
    private int idClient;

    public ReponseAddClient(boolean success, String message, int idClient)
    {
        super(success, message);
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }
}
