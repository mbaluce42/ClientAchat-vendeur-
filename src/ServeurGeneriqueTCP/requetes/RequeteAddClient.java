package ServeurGeneriqueTCP.requetes;


import java.io.Serializable;

public class RequeteAddClient extends RequeteBSPP
{
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;

    public RequeteAddClient(String nom, String prenom, String telephone, String adresse, String email)
    {
        super("ADD_CLIENT");
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.email = email;
    }

    public String getNom()
    {
        return nom;
    }

    public String getPrenom()
    {
        return prenom;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public String getAdresse()
    {
        return adresse;
    }

    public String getEmail()
    {
        return email;
    }

}