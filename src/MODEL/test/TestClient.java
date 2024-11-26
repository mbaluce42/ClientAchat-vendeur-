package MODEL.test;

import MODEL.DAO.ClientDAO;
import MODEL.entity.Client;

import java.util.List;

public class TestClient
{

    public static void main(String[] args)
    {
        List<Client> ListClients ;//va permet d'afficher les modif de la BD
        //creer client
        ClientDAO clientDAO = new ClientDAO();
        Client client1 = new Client("Mourhino", "Jose", "123456789", "London", "josemourhino@gmail.com");
        Client res= clientDAO.create(client1);
        if (res != null)
        {
            System.out.println("Client cree avec succes: ");
            System.out.println("Client: " +res);
            client1=res;
        }
        else
        {
            System.out.println("Erreur lors de la creation du client");
            return;
        }


        //afficher les clients
        System.out.println("Liste des clients: ");
        ListClients = clientDAO.findAll();
        for (Client client : ListClients)
        {
            System.out.println(client);
        }
        System.out.println("");

        //update client

        client1.setNom("Guardiola");
        client1.setPrenom("Pep");

        boolean res1 = clientDAO.update(client1);
        if (res1)
        {
            System.out.println("Client mis a jour avec succes: ");
            System.out.println("Client: " +client1);
        }
        else
        {
            System.out.println("Erreur lors de la mise a jour du client");
            return;
        }

        ListClients = clientDAO.findAll();
        System.out.println("Liste des clients: ");
        for (Client client : ListClients)
        {
            System.out.println(client);
        }

        System.out.println("");


        //delete client

        boolean res2 = clientDAO.delete(client1.getId());
        if (res2)
        {
            System.out.println("Client supprime avec succes: ");
            System.out.println("Client: " +client1);
        }
        else
        {
            System.out.println("Erreur lors de la suppression du client");
            return;
        }

        ListClients = clientDAO.findAll();
        System.out.println("Liste des clients: ");
        for (Client client : ListClients)
        {
            System.out.println(client);
        }

    }

}
