package ServeurGeneriqueTCP.requetes;

public class RequeteGetBooks extends RequeteBSPP
{
    private String titre;
    private String authorLastName;
    private String authorFirstName;
    private String subject;
    private Float maxPrice;

    public RequeteGetBooks(String titre, String authorLastName, String authorFirstName, String subject, float maxPrice)
    {
        super("GET_BOOKS");
        this.titre = titre;
        this.authorLastName = authorLastName;
        this.authorFirstName = authorFirstName;
        this.subject = subject;
        this.maxPrice = maxPrice;
    }

    public String getTitre()
    {
        return titre;
    }

    public String getAuthorLastName()
    {
        return authorLastName;
    }

    public String getAuthorFirstName()
    {
        return authorFirstName;
    }

    public String getSubject()
    {
        return subject;
    }

    public Float getMaxPrice()
    {
        return maxPrice;
    }


}
