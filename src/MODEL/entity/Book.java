package MODEL.entity;

import java.io.Serializable;

public class Book implements Serializable
{
    private int id;
    private Author author;
    private Subject subject;
    private String title;
    private String isbn;
    private int pageCount;
    private int stockQuantity;
    private float price;
    private int publishYear;

    public Book(Author author, Subject subject, String title, String isbn, int pageCount, int stockQuantity, float price, int publishYear)
    {
        this.author = author;
        this.subject = subject;
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.publishYear = publishYear;
    }

    public Book()
    {
        this.author = new Author();
        this.subject = new Subject();

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(Author author)
    {
        this.author = author;
    }

    public Subject getSubject()
    {
        return subject;
    }

    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public int getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }

    public int getStockQuantity()
    {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity)
    {
        this.stockQuantity = stockQuantity;
    }

    public float getPrice()
    {
        return price;
    }

    public void setPrice(float price)
    {
        this.price = price;
    }

    public int getPublishYear()
    {
        return publishYear;
    }

    public void setPublishYear(int publishYear)
    {
        this.publishYear = publishYear;
    }

    @Override
    public String toString()
    {
        return "Book{" + "id=" + id + ", author=" + author + ", subject=" + subject + ", title=" + title + ", isbn=" + isbn + ", pageCount=" + pageCount + ", stockQuantity=" + stockQuantity + ", price=" + price + ", publishYear=" + publishYear + '}';
    }

}
