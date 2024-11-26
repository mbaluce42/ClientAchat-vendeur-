package MODEL.DAO;

import MODEL.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO
{
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(AuthorDAO.class.getName());

    public ClientDAO()
    {
        connection = ConnectDB.getInstance().getConnection();
    }

    public Client create(Client client)
    {
        //verifier si le client existe deja
        Client clientExist = findByNomPrenom(client.getNom(), client.getPrenom());
        if (clientExist != null)
        {
            return clientExist;
        }

        String sql = "INSERT INTO clients (nom, prenom, telephone, adresse, email) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getAddress());
            stmt.setString(5, client.getEmail());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Erreur lors de la creation du client, aucune ligne affectee.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    client.setId(generatedKeys.getInt(1));
                    return client;
                }
                else
                {
                    throw new SQLException("Erreur lors de la creation du client, aucun ID recupere.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la creation du client", ex);
            return null;
        }
    }

    public Client findByNomPrenom(String nom, String prenom)
    {
        String sql = "SELECT * FROM clients WHERE nom = ? AND prenom = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapResultSetToClient(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du client par nom et prenom", ex);
        }
        return null;
    }

    public Client findById(int id)
    {
        String sql = "SELECT * FROM clients WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapResultSetToClient(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du client par id", ex);
        }

        return null;
    }

    public List<Client> findAll()
    {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";

        try (Statement stmt = connection.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                Client client = mapResultSetToClient(rs);
                clients.add(client);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de tous les clients", ex);
        }

        return clients;
    }

    public boolean update(Client client)
    {
        //verifier si le client existe deja
        Client clientExist = findById(client.getId());
        if (clientExist == null)
        {
            return false;
        }

        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, adresse = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getAddress());
            stmt.setString(5, client.getEmail());
            stmt.setInt(6, client.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise a jour du client", ex);
            return false;
        }
    }

    public boolean delete(int id)
    {
        //verifier si le client existe deja
        Client clientExist = findById(id);
        if (clientExist == null)
        {
            return false;
        }

        String sql = "DELETE FROM clients WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du client", ex);
            return false;
        }
    }



    private Client mapResultSetToClient(ResultSet rs) throws SQLException
    {
        Client client = new Client();
        client.setId(rs.getInt("id"));
        client.setNom(rs.getString("nom"));
        client.setPrenom(rs.getString("prenom"));
        client.setTelephone(rs.getString("telephone"));
        client.setAddress(rs.getString("adresse"));
        client.setEmail(rs.getString("email"));
        return client;
    }
}
