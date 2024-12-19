package ServeurGeneriqueTCP.requetes;

public class RequeteGetClient extends RequeteBSPP
{
    String nom;
    String prenom;

    public RequeteGetClient(String nom, String prenom)
    {
        super("GET_CLIENT");
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom()
    {
        return nom;
    }

    public String getPrenom()
    {
        return prenom;
    }

}
