package MODEL.DAO;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import MODEL.entity.Caddy;
import MODEL.entity.Client;
import MODEL.entity.CaddyItem;

public class CaddyDAO
{
    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());
    private Connection connection;

    public CaddyDAO()
    {
        this.connection = ConnectDB.getInstance().getConnection();
    }

    public Caddy create(Caddy caddy) {
        String sql = "INSERT INTO caddies (client_id, date, amount, payed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, caddy.getClient().getId()); // On utilise l'ID du client
            stmt.setString(2, caddy.getDate());
            stmt.setFloat(3, caddy.getAmount());
            stmt.setString(4, caddy.getPayed());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating caddy failed");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    caddy.setId(generatedKeys.getInt(1));
                    return caddy;
                } else {
                    throw new SQLException("Creating caddy failed, no ID obtained");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Caddy findById(int id) {
        String sql = "SELECT * FROM caddies WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Créer et remplir le caddy
                Caddy caddy = new Caddy();
                caddy.setId(rs.getInt("id"));
                caddy.setDate(rs.getString("date"));
                caddy.setAmount(rs.getFloat("amount"));
                caddy.setPayed(rs.getString("payed"));

                // Charger le client associé
                ClientDAO clientDAO = new ClientDAO();
                Client client = clientDAO.findById(rs.getInt("client_id"));
                caddy.setClient(client);

                // Charger les items du panier
                CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
                List<CaddyItem> items = caddyItemDAO.findByCaddyId(caddy.getId());
                caddy.setItems(items);

                return caddy;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Caddy findActiveByClient(Client client) {
        String sql = "SELECT * FROM caddies WHERE client_id = ? AND payed = 'N'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, client.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Caddy caddy = new Caddy();
                caddy.setId(rs.getInt("id"));
                caddy.setClient(client);
                caddy.setDate(rs.getString("date"));
                caddy.setAmount(rs.getFloat("amount"));
                caddy.setPayed(rs.getString("payed"));

                // Charger les items du panier
                CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
                List<CaddyItem> items = caddyItemDAO.findByCaddyId(caddy.getId());
                caddy.setItems(items);

                return caddy;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean update(Caddy caddy) {
        String sql = "UPDATE caddies SET amount = ?, payed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setFloat(1, caddy.getAmount());
            stmt.setString(2, caddy.getPayed());
            stmt.setInt(3, caddy.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        // D'abord supprimer tous les items associés
        CaddyItemDAO caddyItemDAO = new CaddyItemDAO();
        caddyItemDAO.deleteByCaddyId(id);

        // Ensuite supprimer le caddy
        String sql = "DELETE FROM caddies WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
