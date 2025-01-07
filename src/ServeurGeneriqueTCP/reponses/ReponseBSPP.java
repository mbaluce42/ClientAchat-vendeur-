package ServeurGeneriqueTCP.reponses;

import ServeurGeneriqueTCP.protocol.Reponse;

import java.io.Serializable;

/*
        case "ADD_CLIENT": //reponse sera un boolean(status -> true ou false) + message + idClient
        case "GET_CLIENT": //reponse sera un boolean(status -> true ou false) + message + idClient

        case "GET_BOOKS": //reponse sera un boolean(status -> true ou false) + message + liste des livres
        case "GET_AUTHORS": //reponse sera un boolean(status -> true ou false) + message + liste des auteurs
        case "GET_SUBJECTS": //reponse sera un boolean(status -> true ou false) + message + liste des sujets

        case "ADD_CADDY_ITEM"://reponse sera un boolean(status -> true ou false) + message
        case "GET_CADDY_ITEMS"://reponse sera un boolean(status -> true ou false) + message + liste des items du caddy
        case "DEL_CADDY_ITEM": //reponse sera un boolean(status -> true ou false) + message

        case "CANCEL_CADDY"://reponse sera un boolean(status -> true ou false) + message
        case "PAY_CADDY": //reponse sera un boolean(status -> true ou false) + message
        case "GET_CADDY": //reponse sera un boolean(status -> true ou false) + message + id + Date + Amount + Payed
        case "UPDATE_BOOK": //reponse sera un boolean(status -> true ou false) + message

     */
public abstract class ReponseBSPP implements Reponse, Serializable
{
    private boolean success;
    private String message;

    public ReponseBSPP(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }
}
