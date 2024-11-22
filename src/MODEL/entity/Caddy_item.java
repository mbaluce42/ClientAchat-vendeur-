package MODEL.entity;

public class Caddy_item
{
    private int id;
    private Caddy caddy;
    private Book book;
    private int quantity;
    private float UnitPrice;
    private float totalPrice;

    public Caddy_item(Caddy caddy, Book book, int quantity, float UnitPrice, float totalPrice)
    {
        this.caddy = caddy;
        this.book = book;
        this.quantity = quantity;
        this.UnitPrice = UnitPrice;
        this.totalPrice = totalPrice;
    }

    public Caddy_item()
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

    public Caddy getCaddy()
    {
        return caddy;
    }

    public void setCaddy(Caddy caddy)
    {
        this.caddy = caddy;
    }

    public Book getBook()
    {
        return book;
    }

    public void setBook(Book book)
    {
        this.book = book;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public float getUnitPrice()
    {
        return UnitPrice;
    }

    public void setUnitPrice(float UnitPrice)
    {
        this.UnitPrice = UnitPrice;
    }

    public float getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString()
    {
        return "Caddy_item{" + "id=" + id + ", caddy=" + caddy + ", book=" + book + ", quantity=" + quantity + ", UnitPrice=" + UnitPrice + ", totalPrice=" + totalPrice + '}';
    }

}
