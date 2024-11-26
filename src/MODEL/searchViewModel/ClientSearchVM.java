package MODEL.searchViewModel;

public class ClientSearchVM
{
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String address;
    private String email;

    public ClientSearchVM(String nom, String prenom, String telephone, String address, String email)
    {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.address = address;
        this.email = email;
    }

    public ClientSearchVM()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getPrenom()
    {
        return prenom;
    }

    public void setPrenom(String prenom)
    {
        this.prenom = prenom;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
