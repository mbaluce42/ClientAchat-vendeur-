package MODEL.entity;

import java.io.Serializable;

public class CaddyItem implements Serializable
{
    private int id;
    private Caddy caddy;    // au lieu de caddy_id
    private Book book;      // au lieu de book_id
    private int quantity;

    public CaddyItem() {}

    public CaddyItem(Caddy caddy, Book book, int quantity) {
        this.caddy = caddy;
        this.book = book;
        this.quantity = quantity;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Caddy getCaddy() { return caddy; }
    public void setCaddy(Caddy caddy) { this.caddy = caddy; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Méthode utilitaire pour calculer le sous-total de cet item
    public float getSubtotal() {
        return book.getPrice() * quantity;
    }

    @Override
    public String toString()
    {
        return "CaddyItem{" +
                "id=" + id +
                ", caddy=" + caddy +
                ", book=" + book +
                ", quantity=" + quantity +
                '}';
    }
}
