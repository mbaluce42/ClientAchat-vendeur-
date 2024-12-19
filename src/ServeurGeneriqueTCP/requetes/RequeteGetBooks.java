package ServeurGeneriqueTCP.requetes;

public class RequeteGetBooks extends RequeteBSPP
{
    private String titre;
    private String authorName;
    private String authorFirstName;
    private String subject;
    private Float maxPrice;

    public RequeteGetBooks(String titre, String authorName, String authorFirstName, String subject, Float maxPrice)
    {
        super("GET_BOOKS");
        this.titre = titre;
        this.authorName = authorName;
        this.authorFirstName = authorFirstName;
        this.subject = subject;
        this.maxPrice = maxPrice;
    }

    public String getTitre()
    {
        return titre;
    }

    public String getAuthorName()
    {
        return authorName;
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
