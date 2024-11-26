package MODEL.DAO;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;


import MODEL.entity.Caddy;
import MODEL.entity.CaddyItem;
import MODEL.entity.Book;


public class CaddyItemDAO {
    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());
    private Connection connection;

    public CaddyItemDAO() {
        this.connection = ConnectDB.getInstance().getConnection();
    }

    public CaddyItem create(CaddyItem item)
    {
        String sql = "INSERT INTO caddies_items (caddy_id, book_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getCaddy().getId()); // On utilise l'ID du caddy
            stmt.setInt(2, item.getBook().getId());   // On utilise l'ID du livre
            stmt.setInt(3, item.getQuantity());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating caddy item failed");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                    return item;
                } else {
                    throw new SQLException("Creating caddy item failed, no ID obtained");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<CaddyItem> findByCaddyId(int caddyId) {
        List<CaddyItem> items = new ArrayList<>();
        String sql = "SELECT * FROM caddies_items WHERE caddy_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, caddyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CaddyItem item = new CaddyItem();
                item.setId(rs.getInt("id"));
                item.setQuantity(rs.getInt("quantity"));

                // Charger le livre associé
                BookDAO bookDAO = new BookDAO();
                Book book = bookDAO.findById(rs.getInt("book_id"));
                item.setBook(book);

                items.add(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM caddies_items WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteByCaddyId(int caddyId) {
        String sql = "DELETE FROM caddies_items WHERE caddy_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, caddyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // Dans CaddyItemDAO
    public CaddyItem findById(int id) {
        String sql = "SELECT * FROM caddies_items WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CaddyItem item = new CaddyItem();
                item.setId(rs.getInt("id"));
                item.setQuantity(rs.getInt("quantity"));

                // Charger le livre associé
                BookDAO bookDAO = new BookDAO();
                Book book = bookDAO.findById(rs.getInt("book_id"));
                item.setBook(book);

                // Charger le caddy associé
                CaddyDAO caddyDAO = new CaddyDAO();
                Caddy caddy = caddyDAO.findById(rs.getInt("caddy_id"));
                item.setCaddy(caddy);

                return item;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
