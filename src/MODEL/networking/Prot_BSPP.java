package MODEL.networking;

import java.io.*;
import java.net.Socket;

public class Prot_BSPP
{
    private Socket clientSocket;
    private static final int RESPONSE_BUFFER_SIZE = 1500;

    public Prot_BSPP(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }


    public ResultatBSPP BSPP_Op(String request) throws IOException
    {
        ResultatBSPP resultat = new ResultatBSPP();

        // Échange avec le serveur
        String response = echange(request);
        if (response == null) {
            resultat.setSuccess(false);
            return resultat;
        }

        System.out.println("Réponse brute : " + response);

        // Analyse de la réponse
        String typeRequete;
        String[] parts = response.split("_", 2);
        if(parts.length != 2)
        {
            //c'est une requete de type LOGIN ou LOGOUT

            parts= response.split("#",2);
            typeRequete = parts[0];//login, logout
            response= parts[1];//reponse sera _statut#message

        }
        else
        {
            typeRequete = parts[0];//add, get, getid
            response= parts[1];//reponse sera _entity#statut#message

        }
        System.out.println("Type de requête : " + typeRequete);
        System.out.println("Réponse : " + response);


        switch (typeRequete)
        {
            case "ADD":
                return traiterRequeteAdd(response);
            case "GET":
                return traiterRequeteGet(response);
            case "GETID":
                return traiterRequeteGetId(response);

            case "LOGIN":
            case "LOGOUT":
                return traiterRequeteLoginLogout(response);
        }
        return resultat;
    }

    private ResultatBSPP traiterRequeteAdd(String response)
    {
        ResultatBSPP resultat = new ResultatBSPP();
        String temp = response;
        String[] parts = temp.split("#", 2);
        temp= parts[1];
        parts = temp.split("#", 2);
        String status = parts[0];
        String message = parts[1];

        resultat.setMessage(message);
        resultat.setSuccess("OK".equals(status));

        return resultat;
    }

    private ResultatBSPP traiterRequeteGet(String response) {
        ResultatBSPP resultat = new ResultatBSPP();
        String temp = response;
        System.out.println("temp=reponse : " + temp);
        String[] parts = temp.split("#", 2);
        temp= parts[1]; //status#message-> quand status=KO ou status\nmessage -> quand status=OK
        System.out.println("temp=parts[1] : " + temp);
        if(temp.contains("\n"))
        {
            parts = temp.split("\n", 2);
        }
        else
        {
            parts = temp.split("#", 2);
        }

        String status = parts[0];
        String message = parts[1];

        resultat.setMessage(message);
        resultat.setSuccess("OK".equals(status));

        return resultat;
    }

    private ResultatBSPP traiterRequeteGetId(String response)
    {
        ResultatBSPP resultat = new ResultatBSPP();
        String temp = response;
        System.out.println("temp=reponse : " + temp);
        String[] parts = temp.split("#", 2);
        temp= parts[1]; //status#message-> quand status=KO ou status\nmessage -> quand status=OK
        System.out.println("temp=parts[1] : " + temp);
        if(temp.contains("\n"))
        {
            parts = temp.split("\n", 2);
        }
        else
        {
            parts = temp.split("#", 2);
        }

        String status = parts[0];
        String message = parts[1];

        resultat.setMessage(message);
        resultat.setSuccess("OK".equals(status));

        return resultat;
    }

    private ResultatBSPP traiterRequeteLoginLogout(String response)
    {
        ResultatBSPP resultat = new ResultatBSPP();
        String temp = response;
        String[] parts = temp.split("#", 2);

        String status = parts[0];
        String message = parts[1];

        resultat.setMessage(message);
        resultat.setSuccess("OK".equals(status));

        return resultat;
    }

    private ResultatBSPP traiterAutresRequetes(String response)
    {

        ResultatBSPP resultat = new ResultatBSPP();
        String temp = response;
        String[] parts = temp.split("#", 2);
        String status = parts[0];
        String message = parts[1];

        resultat.setMessage(message);
        resultat.setSuccess("OK".equals(status));

        return resultat;
    }


    private String echange(String request) throws IOException
    {
        try {
            // Envoi de la requête
            byte[] requestBytes = request.getBytes();
            int bytesWritten = SocketManager.sendData(clientSocket, requestBytes, requestBytes.length);
            if (bytesWritten == -1)
            {
                System.err.println("Erreur lors de l'envoi des données");
                return null;
            }

            // Réception de la réponse
            byte[] responseBytes = SocketManager.receiveData(clientSocket);
            if (responseBytes.length == 0)
            {
                System.out.println("Serveur arrêté, pas de réponse reçue...");
                return null;
            }

            return new String(responseBytes);
        } catch (IOException e) {
            System.err.println("Erreur d'E/S lors de l'échange : " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args)
    {
        // Exemple d'utilisation
        try {
            Socket clientSocket = SocketManager.createClientSocket("192.168.163.128", "50000");
            Prot_BSPP protocol = new Prot_BSPP(clientSocket);

            //ResultatOBEP resultat = protocol.OBEP_Op("GET_ENCODEDBOOKSBYEMPLOYEE#2");
            //ResultatOBEP resultat = protocol.OBEP_Op("ADD_ENCODEDBOOK#2#1");
            //ResultatOBEP resultat = protocol.OBEP_Op("GET_BOOKBYID#1");
            //GETID
            ResultatBSPP resultat = protocol.BSPP_Op("GETID_BOOK#Les");


            //string employee_id,string book_id,string date)
            if (resultat.isSuccess()) {
                System.out.println("Opération réussie : " + resultat.getMessage());
            } else {
                System.out.println("Erreur : " + resultat.getMessage());
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}