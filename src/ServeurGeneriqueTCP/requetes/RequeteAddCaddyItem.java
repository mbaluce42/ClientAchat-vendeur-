package ServeurGeneriqueTCP.requetes;
//client_id#book_id#quantity
public class RequeteAddCaddyItem extends RequeteBSPP
{
    private int idClient;
    private int idBook;
    private int quantity;

    public RequeteAddCaddyItem(int idClient, int idBook, int quantity)
    {
        super("ADD_CADDY_ITEM");
        this.idClient = idClient;
        this.idBook = idBook;
        this.quantity = quantity;
    }

    public int getIdClient()
    {
        return idClient;
    }

    public int getIdBook()
    {
        return idBook;
    }

    public int getQuantity()
    {
        return quantity;
    }

}
