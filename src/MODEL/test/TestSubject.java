package MODEL.test;

import MODEL.DAO.SubjectDAO;
import MODEL.entity.Subject;

import java.util.List;

public class TestSubject
{
    public static void main(String[] args)
    {
        List<Subject> Listsubjects ;//va permet d'afficher les modif de la BD

        //creer un new sujet
        Subject subject = new Subject("Mathematiques");
        System.out.println("Subject: " +subject);

        SubjectDAO subjectDAO = new SubjectDAO();

        Subject resultat =subjectDAO.create(subject);

        if (resultat != null)
        {
            System.out.println("Sujet cree avec succes: ");
            System.out.println("Subject: " +resultat);
            subject=resultat;

        }
        else
        {
            System.out.println("Erreur lors de la creation du sujet");
            return;
        }

        Listsubjects = subjectDAO.findAll();
        System.out.println("Liste des sujets: ");
        for (Subject sujet : Listsubjects)
        {
            System.out.println(sujet);
        }
        System.out.println("");


        //update sujet
        subject.setName("Manga");

        boolean res = subjectDAO.update(subject);
        if (res)
        {
            System.out.println("Sujet mis a jour avec succes: ");
            System.out.println("Subject: " +subject);
        }
        else
        {
            System.out.println("Erreur lors de la mise a jour du sujet");
            return;
        }

        Listsubjects = subjectDAO.findAll();
        System.out.println("Liste des sujets: ");
        for (Subject sujet : Listsubjects)
        {
            System.out.println(sujet);
        }

        System.out.println("");


        //delete sujet

        res = subjectDAO.delete(subject.getId());
        if (res)
        {
            System.out.println("Sujet supprime avec succes: ");
            System.out.println("Subject: " +subject);
        }
        else
        {
            System.out.println("Erreur lors de la suppression du sujet");
            return;
        }

        Listsubjects = subjectDAO.findAll();
        System.out.println("Liste des sujets: ");
        for (Subject sujet : Listsubjects)
        {
            System.out.println(sujet);
        }

    }
}
