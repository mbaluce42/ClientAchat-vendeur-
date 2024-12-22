package ServeurGeneriqueTCP.requetes;

public class RequeteGetCaddyItems extends RequeteBSPP
{
    int idCaddy;

    public RequeteGetCaddyItems(int idCaddy)
    {
        super("GET_CADDY_ITEMS");
        this.idCaddy = idCaddy;
    }

    public int getIdCaddy()
    {
        return idCaddy;
    }

}
