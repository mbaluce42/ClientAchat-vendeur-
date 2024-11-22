package MODEL.DAO;


import MODEL.entity.Subject;

import java.sql.*;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AuthorDAO {
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(AuthorDAO.class.getName());

    public AuthorDAO() {
        connection = ConnectDB.getInstance().getConnection();
    }

    public Subject create(Subject subject)
    {
        String sql = "INSERT INTO subjects (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating subject failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subject.setId(generatedKeys.getInt(1));
                    return subject;
                } else {
                    throw new SQLException("Creating subject failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creating subject", ex);
            return null;
        }
    }

    public Subject findById(int id)
    {
        String sql = "SELECT * FROM subjects WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSubject(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding subject by id", ex);
        }

        return null;
    }

    public List<Subject> findAll()
    {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Subject subject = mapResultSetToSubject(rs);
                subjects.add(subject);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding all subjects", ex);
        }

        return subjects;
    }

    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException
    {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setName(rs.getString("name"));
        return subject;
    }



}
