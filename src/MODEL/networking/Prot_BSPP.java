package MODEL.networking;

import MODEL.DAO.*;
import MODEL.entity.*;
import ServeurGeneriqueTCP.exception.FinConnexionException;
import ServeurGeneriqueTCP.protocol.Protocole;
import ServeurGeneriqueTCP.protocol.Reponse;
import ServeurGeneriqueTCP.protocol.Requete;
import ServeurGeneriqueTCP.reponses.*;
import ServeurGeneriqueTCP.requetes.*;
import ServeurGeneriqueTCP.utils.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Prot_BSPP implements Protocole
{
    private static final String PROTOCOL_NAME = "BSPP";
    private Logger logger;
    private Socket clientSocket;
    private Socket clientSocket1;

    public Prot_BSPP(Logger logger)
    {
        this.logger = logger;
    }
    public Prot_BSPP(Socket clientSocket1)
    {
        this.clientSocket1 = clientSocket1;
    }

    @Override
    public String getNom()
    {
        return PROTOCOL_NAME;
    }

    @Override
    public synchronized Reponse TraiteRequete(Requete requete, Socket socket) throws FinConnexionException
    {
        RequeteBSPP req = (RequeteBSPP) requete;
        String type = req.getType();
        logger.Trace("Requête reçue : " + type+ "de l'ip:"+ socket.getInetAddress().getHostAddress());

        switch (type)
        {
            case "ADD_CLIENT":
                return traiterRequeteAddClient((RequeteAddClient) req);
            case "GET_CLIENT":
                return traiterRequeteGetClient((RequeteGetClient) req);
            case "GET_CADDY":
                return traiterRequeteGetCaddy((RequeteGetCaddy) req);
            case "GET_BOOKS":
                return traiterRequeteGetBooks((RequeteGetBooks) req);
            case "GET_AUTHORS":
                return traiterRequeteGetAuthors((RequeteGetAuthors) req);
            case "GET_SUBJECTS":
                return traiterRequeteGetSubjects((RequeteGetSubjects) req);
            case "ADD_CADDY_ITEM":
                return traiterRequeteAddCaddyItem((RequeteAddCaddyItem) req);
            case "GET_CADDY_ITEMS":
                return traiterRequeteGetCaddyItems((RequeteGetCaddyItems) req);
            case "DEL_CADDY_ITEM":
                return traiterRequeteDelCaddyItem((RequeteDelCaddyItem) req);
            case "CANCEL_CADDY":
                return traiterRequeteCancelCaddy((RequeteCancelCaddy) req);
            case "PAY_CADDY":
                return traiterRequetePayCaddy((RequetePayCaddy) req);
            case "UPDATE_BOOK":
                return traiterRequeteUpdateBook((RequeteUpdateBook) req);

            default:
                logger.Trace("Requête inconnue");
                return null;

        }

    }

    private Reponse traiterRequeteAddClient(RequeteAddClient req)
    {

        Client client = new Client(req.getNom(), req.getPrenom(), req.getTelephone(), req.getAdresse(), req.getEmail());
        ClientDAO clientDAO = new ClientDAO();
        Client res = clientDAO.create(client); // dans ma dao create avant de le faire, je verif si le client existe -> si oui alors return client , sinon ajoute dans la bd
        if(res != null)
        {
            logger.Trace("Client ajouté, "+"id= "+res.getId());
            return new ReponseAddClient(true,"Operation reussie", res.getId());
        }
        else
        {
            logger.Trace("Erreur lors de l'ajout du client");
            return new ReponseAddClient(false, "Erreur lors de l'ajout du client", -1);

        }
    }

    private Reponse traiterRequeteGetClient(RequeteGetClient req)
    {
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findByNomPrenom(req.getNom(), req.getPrenom());
        if(client != null)
        {
            logger.Trace("Client trouvé, "+"id= "+client.getId());
            return new ReponseGetClient(true, "Client trouvé", client.getId());
        }
        else
        {
            logger.Trace("Client non trouvé");
            return new ReponseGetClient(false, "Client non trouvé", -1);
        }

    }
    private Reponse traiterRequeteGetCaddy(RequeteGetCaddy req)
    {
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findById(req.getIdClient());
        if(client == null)
        {
            logger.Trace("Panier du client non trouvé, "+"idclient= "+req.getIdClient());
            return new ReponseGetCaddy(false, "Panier du client non trouvé", -1, "", 0.0f, "N");
        }

        CaddyDAO caddyDAO = new CaddyDAO();
        Caddy caddy = caddyDAO.findActiveByClient(client);
        if(caddy != null)
        {
            logger.Trace("Panier("+caddy.getId()+") du client("+client.getId()+") trouvé");
            return new ReponseGetCaddy(true, "Panier trouvé", caddy.getId(), caddy.getDate(), caddy.getAmount(), caddy.getPayed());
        }

        logger.Trace("Panier non trouvé");
        return new ReponseGetCaddy(false, "Panier non trouvé", -1, "", 0.0f, "N");
    }

    private Reponse traiterRequeteGetBooks(RequeteGetBooks req)
    {
        Author author = new Author();
        AuthorDAO authorDAO = new AuthorDAO();
        BookDAO bookDAO = new BookDAO();
        if (req.getAuthorFirstName().equals("NULL") == false && req.getAuthorLastName().equals("NULL") == false)
        {
            List<Book> ListBooks = bookDAO.findByAuthorLastNameFirstName(req.getAuthorLastName(), req.getAuthorFirstName());
            if (!ListBooks.isEmpty())
            {
                logger.Trace("Livres trouvés: \n"+ListBooks);
                return new ReponseGetBooks(true, "Livres trouvés",ListBooks);
            }
            else
            {
                logger.Trace("Livre non trouvé");
                return new ReponseGetBooks(false, "Livre non trouvé", null);
            }
        }
        else if(req.getSubject().equals("NULL") == false)
        {
            List<Book> ListBooks = bookDAO.findBySubjectName(req.getSubject());
            if (!ListBooks.isEmpty())
            {
                logger.Trace("Livres trouvés: \n"+ListBooks);
                return new ReponseGetBooks(true, "Livres trouvés", ListBooks);
            }
            else
            {
                logger.Trace("Livre non trouvé");
                return new ReponseGetBooks(false, "Livre non trouvé", null);
            }
        }
        else if (req.getTitre().equals("NULL") == false)
        {
            List<Book> ListBooks= bookDAO.findByTitle(req.getTitre());
            if(!ListBooks.isEmpty())
            {
                logger.Trace("Livres trouvés: \n"+ListBooks);
                return new ReponseGetBooks(true, "Livres trouvés", ListBooks);
            }
            else
            {
                logger.Trace("Livre non trouvé");
                return new ReponseGetBooks(false, "Livre non trouvé", null);
            }
        }
        else if (req.getMaxPrice().equals("0.0")==false || req.getMaxPrice().equals("0")==false)
        {
            List<Book> ListBooks = bookDAO.findByPrice(req.getMaxPrice());
            if (!ListBooks.isEmpty())
            {
                logger.Trace("Livres trouvés: \n"+ListBooks);
                return new ReponseGetBooks(true, "Livres trouvés", ListBooks);
            }
            else
            {
                logger.Trace("Livre non trouvé");
                return new ReponseGetBooks(false, "Livre non trouvé", null);
            }
        }
        logger.Trace("Aucun livre ne correspond à votre recherche");
        return new ReponseGetBooks(false, "Aucun livre ne correspond à votre recherche", null);
    }

    private Reponse traiterRequeteGetAuthors(RequeteGetAuthors req)
    {
        AuthorDAO authorDAO = new AuthorDAO();
        List<Author> authors = authorDAO.findAll();
        if(authors != null)
        {
            logger.Trace("Auteurs trouvés: \n"+authors);
            return new ReponseGetAuthors(true, "Auteurs trouvés", authors);
        }
        else
        {
            logger.Trace("Auteur non trouvé");
            return new ReponseGetAuthors(false, "Auteur non trouvé", null);
        }
    }

    private Reponse traiterRequeteGetSubjects(RequeteGetSubjects req)
    {
        SubjectDAO subjectDAO = new SubjectDAO();
        List<Subject> subjects = subjectDAO.findAll();
        if(subjects != null)
        {
            logger.Trace("Sujets trouvés: \n"+subjects);
            return new ReponseGetSubjects(true, "Sujets trouvés", subjects);
        }
        else
        {
            logger.Trace("Sujet non trouvé");
            return new ReponseGetSubjects(false, "Sujet non trouvé", null);
        }
    }

    private Reponse traiterRequeteAddCaddyItem(RequeteAddCaddyItem req)
    {
        CaddyDAO caddyDAO = new CaddyDAO();
        BookDAO bookDAO = new BookDAO();
        ClientDAO clientDAO = new ClientDAO();

        // Vérifier si le client existe
        Client client = clientDAO.findById(req.getIdClient());
        if(client == null)
        {
            logger.Trace("Client non trouvé");
            return new ReponseAddCaddyItem(false, "Client non trouvé");
        }

        // Vérifier si le livre existe et le stock
        Book book = bookDAO.findById(req.getIdBook());
        if(book == null)
        {
            logger.Trace("Livre non trouvé");
            return new ReponseAddCaddyItem(false, "Livre non trouvé");
        }
        if(book.getStockQuantity() < req.getQuantity())
        {
            logger.Trace("Stock insuffisant");
            return new ReponseAddCaddyItem(false, "Stock insuffisant");
        }

        // Chercher un panier actif ou en créer un nouveau
        Caddy caddy = caddyDAO.findActiveByClient(client);
        if(caddy == null)
        {
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
        item.setQuantity(req.getQuantity());

        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
        if(caddyItemDAO.create(item) != null)
        {
            // Mettre à jour le montant du panier
            caddy.setAmount(caddy.getAmount() + (book.getPrice() * req.getQuantity()));
            caddyDAO.update(caddy);
            logger.Trace("Item ajouté au panier");
            return new ReponseAddCaddyItem(true, "Item ajouté au panier");
        }

        logger.Trace("Erreur lors de l'ajout au panier");
        return new ReponseAddCaddyItem(false, "Erreur lors de l'ajout au panier");
    }

    private Reponse traiterRequeteGetCaddyItems(RequeteGetCaddyItems req)
    {
        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
        List<CaddyItem> items = caddyItemDAO.findByCaddyId(req.getIdCaddy());
        if(items != null)
        {
            logger.Trace("Items du panier trouvés: \n"+items);
            return new ReponseGetCaddyItems(true, "Items du panier trouvés", items);
        }

        logger.Trace("Erreur lors de la recherche des items du panier");
        return new ReponseGetCaddyItems(false, "Erreur lors de la recherche des items du panier", null);
    }

    private Reponse traiterRequeteDelCaddyItem(RequeteDelCaddyItem req)
    {
        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
        CaddyDAO caddyDAO = new CaddyDAO();

        // Récupérer l'item pour connaître son prix total
        CaddyItem item = caddyItemDAO.findById(req.getIdItem());
        if(item != null)
        {
            Caddy caddy = caddyDAO.findById(item.getCaddy().getId());
            if(caddy != null)
            {
                // Mettre à jour le montant du panier
                float itemTotal = item.getBook().getPrice() * item.getQuantity();
                caddy.setAmount(caddy.getAmount() - itemTotal);

                if(caddyItemDAO.delete(req.getIdItem()))
                {
                    caddyDAO.update(caddy);
                    logger.Trace("Item supprimé du panier");
                    return new ReponseDelCaddyItem(true, "Item supprimé du panier");
                }
            }
        }
        logger.Trace("Erreur lors de la suppression de l'item du panier");
        return new ReponseDelCaddyItem(false, "Erreur lors de la suppression de l'item du panier");
    }

    private Reponse traiterRequeteCancelCaddy(RequeteCancelCaddy req)
    {
        //int clientId = Integer.parseInt(data.trim());

        CaddyDAO caddyDAO = new CaddyDAO();
        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();

        ClientDAO clientDAO = new ClientDAO();
        Client client = new Client();
        client.setId(req.getIdClient());

        Caddy caddy = caddyDAO.findActiveByClient(client);

        if (caddy != null)
        {
            if (caddyItemDAO.deleteByCaddyId(caddy.getId()))
            {
                if (caddyDAO.delete(caddy.getId()))
                {
                    logger.Trace("Panier annulé");
                    return new ReponseCancelCaddy(true, "Panier annulé");

                }
                else
                {
                    logger.Trace("Erreur lors de l'annulation du panier");
                    return new ReponseCancelCaddy(false, "Erreur lors de l'annulation du panier");
                }
            }
        }

        logger.Trace("Panier non trouvé");
        return new ReponseCancelCaddy(false, "Panier non trouvé");
    }

    private Reponse traiterRequetePayCaddy(RequetePayCaddy req)
    {
        CaddyDAO caddyDAO = new CaddyDAO();
        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();

        ClientDAO clientDAO = new ClientDAO();
        Client client = new Client();
        client.setId(req.getIdClient());

        Caddy caddy = caddyDAO.findActiveByClient(client);

        if (caddy != null)
        {
            caddy.setPayed("Y");
            if (caddyDAO.update(caddy))
            {
                logger.Trace("Paiement effectué");
                return new ReponsePayCaddy(true, "Paiement effectué");
                //MAJ stock


            }
        }
        logger.Trace("Erreur lors du paiement");
        return new ReponsePayCaddy(false, "Erreur lors du paiement");

    }

    private Reponse traiterRequeteUpdateBook(RequeteUpdateBook req)
    {
        BookDAO bookDAO = new BookDAO();
        Book book = new Book();
        book.setId(req.getId());
        book.getAuthor().setId(req.getAuthorId());
        book.getSubject().setId(req.getSubjectId());
        book.setTitle(req.getTitle());
        book.setIsbn(req.getIsbn());
        book.setPageCount(req.getPageCount());
        book.setStockQuantity(req.getStockQuantity());
        book.setPrice(req.getPrice());
        book.setPublishYear(req.getPublishYear());
        if (bookDAO.update(book))
        {
            logger.Trace("Livre mis à jour");
            return new ReponseUpdateBook(true, "Livre mis à jour");
        }
        logger.Trace("Erreur lors de la mise à jour du livre");
        return new ReponseUpdateBook(false, "Erreur lors de la mise à jour du livre");
    }

    public Object echangeObject(Serializable object ) throws IOException, ClassNotFoundException {
        try {
            // Envoi de l'objet request
            SocketManager.sendObject(clientSocket1,object);

            // Réception de la réponse sous forme d'objet
            Object reponse = SocketManager.receiveObject(clientSocket1);

            // Retour de l'objet reçu
            return reponse;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors de l'échange d'objets : " + e.getMessage());
            e.printStackTrace();  // Ajout de cette ligne pour voir la trace de l'exception
            throw e;
        }
    }
}