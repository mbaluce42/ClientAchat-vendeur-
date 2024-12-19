package ServeurGeneriqueTCP.reponses;

public class ReponseGetClient extends ReponseBSPP
{
    private int idClient;

    public ReponseGetClient(boolean success, String message, int idClient)
    {
        super(success, message);
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }

}
