package ServeurGeneriqueTCP.requetes;


public class RequeteUpdateBook extends RequeteBSPP
{
    private int id;
    private int authorId;
    private int subjectId;
    private String title;
    private String isbn;
    private int pageCount;
    private int stockQuantity;
    private float price;
    private int publishYear;

    public RequeteUpdateBook(int id, int authorId, int subjectId, String title, String isbn, int pageCount, int stockQuantity, float price, int publishYear) {
        super("UPDATE_BOOK");
        this.id = id;
        this.authorId = authorId;
        this.subjectId = subjectId;
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.publishYear = publishYear;
    }

    public int getId() {
        return id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public float getPrice() {
        return price;
    }

    public int getPublishYear() {
        return publishYear;
    }

}
