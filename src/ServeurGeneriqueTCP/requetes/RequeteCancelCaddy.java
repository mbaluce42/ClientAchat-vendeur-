package ServeurGeneriqueTCP.requetes;

public class RequeteCancelCaddy extends RequeteBSPP
{
    private int idClient;

    public RequeteCancelCaddy(int idClient)
    {
        super("CANCEL_CADDY");
        this.idClient = idClient;
    }

    public int getIdClient()
    {
        return idClient;
    }

}
