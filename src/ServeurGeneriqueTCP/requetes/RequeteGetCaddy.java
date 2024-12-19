package ServeurGeneriqueTCP.requetes;

public class RequeteGetCaddy extends RequeteBSPP
{
    private int idClient;

    public RequeteGetCaddy(int idClient)
    {
        super("GET_CADDY");
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }
}
