package MODEL.entity;

import java.io.Serializable;

public class Subject implements Serializable
{
    private int id;
    private String name;

    public Subject(String name)
    {
        this.name = name;
    }

    public Subject() {

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Subject{" + "id=" + id + ", name=" + name + '}';
    }

}
