package ServeurGeneriqueTCP.reponses;

import MODEL.entity.CaddyItem;

import java.util.List;

public class ReponseGetCaddyItems extends ReponseBSPP
{
    private List<CaddyItem> items;

    public ReponseGetCaddyItems(boolean success, String message, List<CaddyItem> items)
    {
        super(success, message);
        this.items = items;
    }

    public List<CaddyItem> getItems()
    {
        return items;
    }
}
