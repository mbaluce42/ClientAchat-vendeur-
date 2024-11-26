package MODEL.networking;

import MODEL.DAO.*;
import MODEL.entity.*;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Prot_BSPP
{
    private Socket clientSocket;
    private static final int RESPONSE_BUFFER_SIZE = 1500;

    public Prot_BSPP(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    //client vers serveur
    public ResultatBSPP BSPP_Client_Op(String request) throws IOException
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
        String[] parts = response.split("#", 2);
        String typeRequete = parts[0];
        String responseData = parts[1];

        switch (typeRequete) {
            case "ADD_CLIENT":
            case "ADD_CADDY_ITEM":
            case "DEL_CADDY_ITEM":
            case "CANCEL_CADDY":
            case "PAY_CADDY":

                return traiterRequeteAdd(response);

            case "GET_BOOKS":
            case "GET_AUTHORS":
            case "GET_SUBJECTS":
            case "GET_CADDY_ITEMS":
            case "GET_CLIENT":
            case "GET_CADDY":
                return traiterRequeteGet(response);

            default:
                return traiterAutresRequetes(response);
        }
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

    private ResultatBSPP traiterRequeteGet(String response)
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

        System.out.println("(traiterRequeteGet)status : " + status);
        System.out.println("(traiterRequeteGet)message : " + message);

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

    //serveur vers client
    public String BSPP_Server_Parser(String request)
    {
        System.out.println("(BSPP_Server_Parser)Requête brute : " + request);
        String[] parts = request.split("#", 2);
        String typeRequete = parts[0];
        String data = parts[1];
        System.out.println("(BSPP_Server_Parser)typeRequete : " + typeRequete);
        System.out.println("(BSPP_Server_Parser)data : " + data);

        if(typeRequete.equals("ADD_CLIENT")) //ADD_CLIENT#nom#prenom#telephone#adresse#email
        {
            System.out.println("data : " + data);
            parts= data.split("#", 5);
            String nom = parts[0];
            String prenom = parts[1];
            String telephone = parts[2];
            String adresse = parts[3];
            String email = parts[4];
            System.out.println("(BSPP_Server_Parser)nom : " + nom);
            System.out.println("(BSPP_Server_Parser)prenom : " + prenom);
            System.out.println("(BSPP_Server_Parser)telephone : " + telephone);
            System.out.println("(BSPP_Server_Parser)adresse : " + adresse);
            System.out.println("(BSPP_Server_Parser)email : " + email);


            Client client = new Client(nom, prenom, telephone, adresse, email);
            ClientDAO clientDAO = new ClientDAO();
            Client res = clientDAO.create(client);
            if(res != null)
            {
                return "ADD_CLIENT#OK\n"+res.getId();
            }
            else
            {
                return "ADD_CLIENT#KO#Erreur lors de l'ajout du client";
            }
        }
        else if (typeRequete.equals("GET_CLIENT"))
        {
            parts = data.split("#");
            String lastName = parts[0];
            String firstName = parts[1];
            System.out.println("(BSPP_Server_Parser)lastName : " + lastName);
            System.out.println("(BSPP_Server_Parser)firstName : " + firstName);
            ClientDAO clientDAO = new ClientDAO();
            Client client = clientDAO.findByNomPrenom(lastName, firstName);
            if(client != null)
            {
                return "GET_CLIENT#OK\n" + client.getId();
            }
            else
            {
                return "GET_CLIENT#KO#Client non trouvé";
            }
        }
        else if(typeRequete.equals("GET_CADDY")) //GET_CADDY#client_id
        {

            String clientId = data.trim();
            ClientDAO clientDAO = new ClientDAO();
            Client client = clientDAO.findById(Integer.parseInt(clientId));
            if(client == null)
            {
                return "GET_CADDY#KO#Panie du client " + clientId + " non trouvé";
            }

            CaddyDAO caddyDAO = new CaddyDAO();
            Caddy caddy = caddyDAO.findActiveByClient(client);
            if(caddy != null)
            {
                return "GET_CADDY#OK\n" + caddy.getId() + "#" + caddy.getDate() + "#" + caddy.getAmount() + "#" + caddy.getPayed();
            }

            return "GET_CADDY#KO#Panier non trouvé";
        }

        else if(typeRequete.equals("GET_BOOKS"))//GET_BOOKS#author_firstname author_lastname#subject_name#titre#prixMAx
        {
            System.out.println("(BSPP_Server_Parser)GET_BOOKS:data : " + data);
            Author author = new Author();
            AuthorDAO authorDAO = new AuthorDAO();
            BookDAO bookDAO = new BookDAO();
            parts= data.split("#", 4);
            String authorPrenom_Nom = parts[0];
            String subjectName = parts[1];
            String titre = parts[2];
            String prixMax = parts[3];
            System.out.println("(BSPP_Server_Parser)authorPrenom_Nom : " + authorPrenom_Nom);
            System.out.println("(BSPP_Server_Parser)subjectName : " + subjectName);
            System.out.println("(BSPP_Server_Parser)titre : " + titre);
            System.out.println("(BSPP_Server_Parser)prixMax : " + prixMax);

            if( authorPrenom_Nom.equals("NULL")==false)
            {
                System.out.println("(BSPP_Server_Parser)authorPrenom_Nom derfrf: " + authorPrenom_Nom);
                parts = authorPrenom_Nom.split(" ");
                String author_firstname = parts[0];
                String author_lastname = parts[1];
                System.out.println("(BSPP_Server_Parser)author_firstname : " + author_firstname);
                System.out.println("(BSPP_Server_Parser)author_lastname : " + author_lastname);
                List<Book> ListBooks= bookDAO.findByAuthorLastNameFirstName(author_lastname, author_firstname);
                if(!ListBooks.isEmpty())
                {
                    String books = "";
                    for(Book book : ListBooks)
                    {   //id#authorprenom + author nom#subjectName#title#isbn#pages#quantity#price#publicationDate
                        books += book.getId()+"#"+book.getAuthor().getFirstName()+" "+book.getAuthor().getLastName()+"#"+book.getSubject().getName()+"#"+book.getTitle()+"#"+book.getIsbn()+"#"+book.getPageCount()+"#"+book.getStockQuantity()+"#"+book.getPrice()+"#"+book.getPublishYear()+"\n";
                    }
                    System.out.println("(BSPP_Server_Parser)books : " + books);
                    return "GET_BOOKS#OK\n"+books;
                }
                else
                {
                    return "GET_BOOKS#KO#Livre non trouve";
                }
            }
            else if (subjectName.equals("NULL")==false)
            {
                System.out.println("(BSPP_Server_Parser)rfjidsjfisrsubjectName : " + subjectName);
                List<Book> ListBooks= bookDAO.findBySubjectName(subjectName);
                if(ListBooks.isEmpty())
                {
                    String books = "";
                    for(Book book : ListBooks)
                    {   //id#authorprenom + author nom#subjectName#title#isbn#pages#quantity#price#publicationDate
                        books += book.getId()+"#"+book.getAuthor().getFirstName()+" "+book.getAuthor().getLastName()+"#"+book.getSubject().getName()+"#"+book.getTitle()+"#"+book.getIsbn()+"#"+book.getPageCount()+"#"+book.getStockQuantity()+"#"+book.getPrice()+"#"+book.getPublishYear()+"\n";
                    }
                    return "GET_BOOKS#OK\n"+books;
                }
                else
                {
                    return "GET_BOOKS#KO#Livre non trouvé";
                }
            }
            else if (titre.equals("NULL")==false)
            {
                List<Book> ListBooks= bookDAO.findByTitle(titre);
                if(!ListBooks.isEmpty())
                {
                    String books = "";
                    for(Book book : ListBooks)
                    {   //id#authorprenom + author nom#subjectName#title#isbn#pages#quantity#price#publicationDate
                        books += book.getId()+"#"+book.getAuthor().getFirstName()+" "+book.getAuthor().getLastName()+"#"+book.getSubject().getName()+"#"+book.getTitle()+"#"+book.getIsbn()+"#"+book.getPageCount()+"#"+book.getStockQuantity()+"#"+book.getPrice()+"#"+book.getPublishYear()+"\n";
                    }
                    return "GET_BOOKS#OK\n"+books;
                }
                else
                {
                    return "GET_BOOKS#KO#Livre non trouvé";
                }
            }
            else if (prixMax.equals("0.0")==false || !prixMax.equals("0")==false)
            {
                List<Book> ListBooks = bookDAO.findByPrice(Float.parseFloat(prixMax));
                if (!ListBooks.isEmpty())
                {
                    String books = "";
                    for (Book book : ListBooks)
                    {   //id#authorprenom + author nom#subjectName#title#isbn#pages#quantity#price#publicationDate
                        books += book.getId() + "#" + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName() + "#" + book.getSubject().getName() + "#" + book.getTitle() + "#" + book.getIsbn() + "#" + book.getPageCount() + "#" + book.getStockQuantity() + "#" + book.getPrice() + "#" + book.getPublishYear() + "\n";

                    }
                    return "GET_BOOKS#OK\n" + books;
                }
                else
                {
                    return "GET_BOOKS#KO#Livre non trouvé";
                }
            }
            else
            {
                return "GET_BOOKS#KO#Aucun livre ne correspond à votre recherche";
            }

        }
        else if(typeRequete.equals("ADD_CADDY_ITEM")) //ADD_CADDY_ITEM#client_id#book_id#quantity
        {
            parts = data.split("#", 3);
            int clientId = Integer.parseInt(parts[0]);
            int bookId = Integer.parseInt(parts[1]);
            int quantity = Integer.parseInt(parts[2]);

            CaddyDAO caddyDAO = new CaddyDAO();
            BookDAO bookDAO = new BookDAO();
            ClientDAO clientDAO = new ClientDAO();

            // Vérifier si le client existe
            Client client = clientDAO.findById(clientId);
            if(client == null) {
                return "ADD_CADDY_ITEM#KO#Client non trouvé";
            }

            // Vérifier si le livre existe et le stock
            Book book = bookDAO.findById(bookId);
            if(book == null) {
                return "ADD_CADDY_ITEM#KO#Livre non trouvé";
            }
            if(book.getStockQuantity() < quantity) {
                return "ADD_CADDY_ITEM#KO#Stock insuffisant";
            }

            // Chercher un panier actif ou en créer un nouveau
            Caddy caddy = caddyDAO.findActiveByClient(client);
            if(caddy == null) {
                caddy = new Caddy();
                caddy.setClient(client);
                caddy.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                caddy.setPayed("N");
                caddy.setAmount(0.0f);
                caddy = caddyDAO.create(caddy);
            }

            // Ajouter l'item au panier
            CaddyItem item = new CaddyItem();
            item.setCaddy(caddy);
            item.setBook(book);
            item.setQuantity(quantity);

            CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
            if(caddyItemDAO.create(item) != null) {
                // Mettre à jour le montant du panier
                caddy.setAmount(caddy.getAmount() + (book.getPrice() * quantity));
                caddyDAO.update(caddy);
                return "ADD_CADDY_ITEM#OK#Item ajouté au panier";
            }

            return "ADD_CADDY_ITEM#KO#Erreur lors de l'ajout au panier";
        }
        else if(typeRequete.equals("GET_CADDY_ITEMS")) //GET_CADDY_ITEMS#client_id
        {
            int caddyId = Integer.parseInt(data);

            CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
            List<CaddyItem> items = caddyItemDAO.findByCaddyId(caddyId);
            if(items != null) {
                String itemsList = "";
                for(CaddyItem item : items) {
                    itemsList += item.getId() + "#" + item.getBook().getTitle() + "#" + item.getQuantity() + "#" + item.getBook().getPrice() + "\n";
                }
                return "GET_CADDY_ITEMS#OK\n" + itemsList;
            }

            return "GET_CADDY_ITEMS#KO#Erreur lors de la recherche des items du panier";
        }

        else if(typeRequete.equals("GET_AUTHORS")) //GET_AUTHORS
        {

            AuthorDAO authorDAO = new AuthorDAO();
            List<Author> authors = authorDAO.findAll();
            if(authors != null)
            {
                String authorsList = "";
                for(Author author : authors)
                {
                    authorsList += author.getId() + "#" + author.getFirstName() + " " + author.getLastName() + "\n";
                }
                System.out.println("(BSPP_Server_Parser)authorsList : " + authorsList);
                return "GET_AUTHORS#OK\n" + authorsList;
            }

            return "GET_AUTHORS#KO#Erreur lors de la recherche des auteurs";
        }
        else if(typeRequete.equals("GET_SUBJECTS")) //GET_SUBJECTS
        {
            SubjectDAO subjectDAO = new SubjectDAO();
            List<Subject> subjects = subjectDAO.findAll();
            if(subjects != null) {
                String subjectsList = "";
                for(Subject subject : subjects) {
                    subjectsList += subject.getId() + "#" + subject.getName() + "\n";
                }
                return "GET_SUBJECTS#OK\n" + subjectsList;
            }

            return "GET_SUBJECTS#KO#Erreur lors de la recherche des sujets";
        }
        else if(typeRequete.equals("DEL_CADDY_ITEM")) //DEL_CADDY_ITEM#item_id
        {

            int itemId = Integer.parseInt(data.trim());

            CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
            CaddyDAO caddyDAO = new CaddyDAO();

            // Récupérer l'item pour connaître son prix total
            CaddyItem item = caddyItemDAO.findById(itemId);
            if(item != null)
            {
                Caddy caddy = caddyDAO.findById(item.getCaddy().getId());
                if(caddy != null)
                {
                    // Mettre à jour le montant du panier
                    float itemTotal = item.getBook().getPrice() * item.getQuantity();
                    caddy.setAmount(caddy.getAmount() - itemTotal);

                    if(caddyItemDAO.delete(itemId))
                    {
                        caddyDAO.update(caddy);
                        return "DEL_CADDY_ITEM#OK#Item supprimé du panier";
                    }
                }
            }
            return "DEL_CADDY_ITEM#KO#Erreur lors de la suppression de l'item";
        }

        else if (typeRequete.equals("CANCEL_CADDY")) //CANCEL_CADDY#Client_id
        {
            int clientId = Integer.parseInt(data.trim());

            CaddyDAO caddyDAO = new CaddyDAO();
            CaddyItemDAO caddyItemDAO = new CaddyItemDAO();

            ClientDAO clientDAO = new ClientDAO();
            Client client = new Client();
            client.setId(clientId);

            Caddy caddy = caddyDAO.findActiveByClient(client);

            if (caddy != null) {
                if (caddyItemDAO.deleteByCaddyId(caddy.getId())) {
                    if (caddyDAO.delete(caddy.getId())) {
                        return "CANCEL_CADDY#OK#Panier annulé";
                    } else {
                        return "CANCEL_CADDY#KO#Erreur lors de l'annulation du panier";
                    }
                }
            }

            return "CANCEL_CADDY#KO#Erreur lors de l'annulation du panier";
        }

        else if (typeRequete.equals("PAY_CADDY")) //PAY_CADDY#clientId
        {
            int clientId = Integer.parseInt(data.trim());

            CaddyDAO caddyDAO = new CaddyDAO();
            CaddyItemDAO caddyItemDAO = new CaddyItemDAO();

            ClientDAO clientDAO = new ClientDAO();
            Client client = new Client();
            client.setId(clientId);

            Caddy caddy = caddyDAO.findActiveByClient(client);

            if (caddy != null)
            {
                caddy.setPayed("Y");
                if (caddyDAO.update(caddy))
                {
                    return "PAY_CADDY#OK#Paiement effectué";
                }
            }
            return "PAY_CADDY#KO#Erreur lors du paiement";
        }


        return null;
    }

    /*public static void main(String[] args)
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
    }*/
}