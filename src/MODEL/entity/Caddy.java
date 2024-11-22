package MODEL.entity;

public class Caddy
{
    private int id;
    private Client client;
    private String date;
    private float total;
    private boolean paid;


    public Caddy(Client client, String date, float total, boolean paid)
    {
        this.client = client;
        this.date = date;
        this.total = total;
        this.paid = paid;
    }

    public Caddy()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public float getTotal()
    {
        return total;
    }

    public void setTotal(float total)
    {
        this.total = total;
    }

    public boolean isPaid()
    {
        return paid;
    }

    public void setPaid(boolean paid)
    {
        this.paid = paid;
    }

    @Override
    public String toString()
    {
        return "Caddy{" +
                "id=" + id +
                ", client=" + client +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", paid=" + paid +
                '}';
    }


}
