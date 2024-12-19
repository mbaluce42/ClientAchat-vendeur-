package ServeurGeneriqueTCP.reponses;

import MODEL.entity.Author;

import java.util.List;

public class ReponseGetAuthors extends ReponseBSPP
{
    private List<Author> authors;

    public ReponseGetAuthors(Boolean succes, String message, List<Author> authors)
    {
        super(succes, message);
        this.authors = authors;
    }

    public List<Author> getAuthors()
    {
        return authors;
    }
}
