package ServeurGeneriqueTCP.requetes;

public class RequeteDelCaddyItem extends RequeteBSPP
{
    private int idItem;

    public RequeteDelCaddyItem(int idItem)
    {
        super("DEL_CADDY_ITEM");
        this.idItem = idItem;
    }

    public int getIdItem()
    {
        return idItem;
    }

}
