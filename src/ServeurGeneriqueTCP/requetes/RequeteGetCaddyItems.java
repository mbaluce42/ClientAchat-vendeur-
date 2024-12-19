package ServeurGeneriqueTCP.requetes;

public class RequeteGetCaddyItems extends RequeteBSPP
{
    int idClient;

    public RequeteGetCaddyItems(int idClient)
    {
        super("GET_CADDY_ITEMS");
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }

}
