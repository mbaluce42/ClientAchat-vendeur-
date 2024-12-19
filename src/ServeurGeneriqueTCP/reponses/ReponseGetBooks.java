package ServeurGeneriqueTCP.reponses;

import MODEL.entity.Book;

import java.util.List;

public class ReponseGetBooks extends ReponseBSPP
{
    private List<Book> books;

    public ReponseGetBooks(boolean success, String message, List<Book> books)
    {
        super(success, message);
        this.books = books;
    }

    public List<Book> getBooks()
    {
        return books;
    }
}

