package ServeurGeneriqueTCP.requetes;

import ServeurGeneriqueTCP.protocol.Requete;
/*
        case "ADD_CLIENT": //permet d'ajouter un client
        case "GET_CLIENT": //recup un client specifique -> par nom, par prenom, par id

        case "GET_BOOKS": //recup la liste des livres specifique -> par auteur, par sujet, par titre et par prix
        case "GET_AUTHORS": //recup la liste des auteurs
        case "GET_SUBJECTS": //recup la liste des sujets

        case "ADD_CADDY_ITEM"://ajoute un item(articles) au caddy d'un client
        case "GET_CADDY_ITEMS"://recup les items(articles) du caddy d'un client -> par idclient
        case "DEL_CADDY_ITEM": //permet de supprimer un item du caddy d'un client -> par idItem

        case "CANCEL_CADDY"://annule le caddy d'un client -> par idclient
        case "PAY_CADDY": //paie le caddy d'un client -> par idclient
        case "GET_CADDY": //recup le caddy d'un client -> par idclient

     */
public abstract class RequeteBSPP implements Requete
{
    private String type;//type de la requete (ADD_CLIENT, ADD_CADDY_ITEM, DEL_CADDY_ITEM, CANCEL_CADDY, PAY_CADDY, GET_BOOKS, GET_AUTHORS, GET_SUBJECTS, GET_CADDY_ITEMS, GET_CLIENT, GET_CADDY)

    public RequeteBSPP(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
}
