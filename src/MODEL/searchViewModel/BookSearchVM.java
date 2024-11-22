package MODEL.searchViewModel;

public class BookSearchVM
{
    private Integer id;
    private String title;
    private String authorLastName;
    private String authorFirstName;
    private String subjectName;
    private String isbn;
    private Float price;

    public BookSearchVM()
    {}

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthorLastName()
    {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName)
    {
        this.authorLastName = authorLastName;
    }

    public String getAuthorFirstName()
    {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName)
    {
        this.authorFirstName = authorFirstName;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public Float getPrice()
    {
        return price;
    }

    public void setPrice(Float price)
    {
        this.price = price;
    }

}

