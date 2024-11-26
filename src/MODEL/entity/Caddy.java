package MODEL.entity;

import java.util.ArrayList;
import java.util.List;

public class Caddy
{
    private int id;
    private Client client;
    private String date;
    private float amount;
    private String payed;     // "Y" ou "N"
    private List<CaddyItem> items;  // relation one-to-many avec CaddyItem


    public Caddy() {
        items = new ArrayList<>();
    }

    public Caddy(Client client, String date, float amount, String payed) {
        this();
        this.client = client;
        this.date = date;
        this.amount = amount;
        this.payed = payed;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public String getPayed() { return payed; }
    public void setPayed(String payed) { this.payed = payed; }
    public List<CaddyItem> getItems() { return items; }
    public void setItems(List<CaddyItem> items) { this.items = items; }
    @Override
    public String toString()
    {
        return "Caddy{" +
                "id=" + id +
                ", client=" + client +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", payed='" + payed + '\'' +
                ", items=" + items +
                '}';
    }


}
