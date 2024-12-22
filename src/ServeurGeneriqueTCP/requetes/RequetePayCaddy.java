package ServeurGeneriqueTCP.requetes;

public class RequetePayCaddy extends RequeteBSPP
{
    private int idClient;

    public RequetePayCaddy(int idClient)
    {
        super("PAY_CADDY");
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }

}
