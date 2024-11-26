package MODEL.test;

import MODEL.DAO.AuthorDAO;
import MODEL.DAO.BookDAO;
import MODEL.DAO.SubjectDAO;
import MODEL.entity.Author;
import MODEL.entity.Book;
import MODEL.entity.Subject;

import java.util.List;

public class TestBook
{
    public static void main(String[] args)
    {
        List<Book> Listbooks ;//va permet d'afficher les modif de la BD
        AuthorDAO authorDAO = new AuthorDAO();
        Author author = authorDAO.findById(1);

        SubjectDAO subjectDAO = new SubjectDAO();
        Subject subject = subjectDAO.findById(1);

        BookDAO bookDAO = new BookDAO();

        //cree un livre
        Book book = new Book(author, subject, "Le livre de la jungle", "111-1111111111", 200, 10, 20, 2000);
        Book res =bookDAO.create(book);
        if (res != null)
        {
            System.out.println("Livre cree avec succes: ");
            System.out.println("Book: " +res);
            book=res;
        }
        else
        {
            System.out.println("Erreur lors de la creation du livre");
            return;
        }

        //liste des livres
        Listbooks = bookDAO.findAll();
        System.out.println("Liste des livres: \n");
        for (Book livre : Listbooks)
        {
            System.out.println(livre);
        }


    }

}
