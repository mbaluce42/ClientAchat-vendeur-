package MODEL.test;

import MODEL.DAO.AuthorDAO;
import MODEL.entity.Author;

import java.util.List;

public class TestAuthor
{
    public static void main(String[] args)
    {
        List<Author> Listauthors ;//va permet d'afficher les modif de la BD
        //creer un new auteur
        Author author = new Author("Mbaya", "Luce", "2002-09-23");
        System.out.println("Author: " +author);

        AuthorDAO authorDAO = new AuthorDAO();

        Author resultat =authorDAO.create(author);

        if (resultat != null)
        {
            System.out.println("Auteur cree avec succes: ");
            System.out.println("Author: " +resultat);
            author=resultat;

        }
        else
        {
            System.out.println("Erreur lors de la creation de l'auteur");
            return;
        }

        Listauthors = authorDAO.findAll();
        System.out.println("Liste des auteurs: ");
        for (Author auteur : Listauthors)
        {
            System.out.println(auteur);
        }
        System.out.println("");


        //update auteur
        author.setLastName("Guardiola");

        boolean res = authorDAO.update(author);
        if (res)
        {
            System.out.println("Auteur mis a jour avec succes: ");
            System.out.println("Author: " +author);
        }
        else
        {
            System.out.println("Erreur lors de la mise a jour de l'auteur");
            return;
        }

        Listauthors = authorDAO.findAll();
        System.out.println("Liste des auteurs: ");
        for (Author auteur : Listauthors)
        {
            System.out.println(auteur);
        }

        System.out.println("");

        //delete auteur

        res = authorDAO.delete(author.getId());
        if (res)
        {
            System.out.println("Auteur supprime avec succes: ");
            System.out.println("Author: " +author);
        }
        else
        {
            System.out.println("Erreur lors de la suppression de l'auteur");
            return;
        }

        Listauthors = authorDAO.findAll();
        System.out.println("Liste des auteurs: ");
        for (Author auteur : Listauthors)
        {
            System.out.println(auteur);
        }

    }
}
