package ServeurGeneriqueTCP.reponses;

public class ReponseGetCaddy extends ReponseBSPP
{
    private  int idCaddy;
    private String date;
    private float amount;
    private String payed;

    public ReponseGetCaddy(boolean success, String message,int idCaddy, String date, float amount, String payed)
    {
        super(success, message);

        this.idCaddy = idCaddy;
        this.date = date;
        this.amount = amount;
        this.payed = payed;
    }

    public int getIdCaddy()
    {
        return idCaddy;
    }

    public String getDate()
    {
        return date;
    }

    public float getAmount()
    {
        return amount;
    }

    public String getPayed()
    {
        return payed;
    }
}
