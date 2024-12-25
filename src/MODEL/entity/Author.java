package MODEL.entity;

import java.io.Serializable;

public class Author implements Serializable
{
    private int id;
    private String lastName;
    private String firstName;
    private String birthDate;

    public Author(String lastName, String firstName, String birthDate)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
    }

    public Author()
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

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    @Override
    public String toString()
    {
        return "Author{" + "id=" + id + ", lastName=" + lastName + ", firstName=" + firstName + ", birthDate=" + birthDate + '}';
    }
}
