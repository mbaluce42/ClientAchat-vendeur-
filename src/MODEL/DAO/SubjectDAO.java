package MODEL.DAO;

import MODEL.entity.Subject;

import java.sql.*;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SubjectDAO
{
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

    public SubjectDAO() {
        connection = ConnectDB.getInstance().getConnection();
    }

    public Subject create(Subject subject)
    {
        //verifier si le sujet existe deja
        Subject subjectExist = findByName(subject.getName());
        if (subjectExist != null)
        {
            LOGGER.log(Level.INFO, "Sujet existe deja,pas besoin de le creer");
            return subjectExist;
        }
        String sql = "INSERT INTO subjects (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, subject.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
            {
                throw new SQLException("Erreur lors de la creation du sujet, aucune ligne affectee.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    subject.setId(generatedKeys.getInt(1));
                    return subject;
                }
                else
                {
                    throw new SQLException("Erreur lors de la creation du sujet, aucun ID recupere.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la creation du sujet", ex);
            return null;
        }
    }

    public Subject findById(int id)
    {
        String sql = "SELECT * FROM subjects WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapResultSetToSubject(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du sujet par id", ex);
        }

        return null;
    }

    public Subject findByName(String name)
    {
        String sql = "SELECT * FROM subjects WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapResultSetToSubject(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du sujet par nom", ex);
        }

        return null;
    }

    public List<Subject> findAll()
    {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";

        try (Statement stmt = connection.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                Subject subject = mapResultSetToSubject(rs);
                subjects.add(subject);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de tous les sujets", ex);
        }

        return subjects;
    }

    public boolean update(Subject subject)
    {
        //verifier si le sujet existe deja
        Subject subjectExist = findById(subject.getId());
        if (subjectExist == null)
        {
            LOGGER.log(Level.INFO, "Sujet n'existe pas, impossible de le mettre a jour");
            return false;
        }
        String sql = "UPDATE subjects SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows == 1;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise a jour du sujet", ex);
            return false;
        }
    }

    public boolean delete(int id)
    {
        //verifier si le sujet existe deja
        Subject subjectExist = findById(id);
        if (subjectExist == null)
        {
            LOGGER.log(Level.INFO, "Sujet n'existe pas, impossible de le supprimer");
            return false;
        }
        String sql = "DELETE FROM subjects WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            return affectedRows == 1;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du sujet", ex);
            return false;
        }
    }

    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setName(rs.getString("name"));
        return subject;
    }



}
