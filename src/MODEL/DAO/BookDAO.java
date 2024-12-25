package MODEL.DAO;

import MODEL.entity.Author;
import MODEL.entity.Book;
import MODEL.entity.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO
{
    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());
    private Connection connection;

    public BookDAO() {
        connection = ConnectDB.getInstance().getConnection();
    }

    public Book create(Book book)
    {
        //verifier si le livre existe deja
        Book bookExist = findIsbn(book.getIsbn());
        if (bookExist != null)
        {
            LOGGER.log(Level.INFO, "Livre existe deja,pas besoin de le creer");
            return bookExist;
        }
        String sql = "INSERT INTO books (author_id, subject_id, title, isbn, page_count, stock_quantity, price, publish_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setInt(1, book.getAuthor().getId());
            stmt.setInt(2, book.getSubject().getId());
            stmt.setString(3, book.getTitle());
            stmt.setString(4, book.getIsbn());
            stmt.setInt(5, book.getPageCount());
            stmt.setInt(6, book.getStockQuantity());
            stmt.setFloat(7, book.getPrice());
            stmt.setInt(8, book.getPublishYear());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
            {
                throw new SQLException("Erreur lors de la creation du livre, aucune ligne affectee.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    book.setId(generatedKeys.getInt(1));
                    return book;
                }
                else
                {
                    throw new SQLException("Erreur lors de la creation du livre, aucun ID recupere.");
                }
            }
        } catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Erreur lors de la creation du livre", ex);
            return null;
        }
    }

    public Book findById(int bookId)
    {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                return book;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du livre par id", ex);
        }

        return null;
    }

    public List<Book> findByAuthorId(int authorId)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(authorId);
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par auteur", ex);
        }

        return books;
    }

    public  List<Book> findByAuthorLastNameFirstName(String lastName, String firstName)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author_id = (SELECT id FROM authors WHERE last_name = ? AND first_name = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par auteur", ex);
        }

        return books;
    }

    public List<Book> findBySubjectId(int subjectId)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE subject_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(subjectId);
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par sujet", ex);
        }

        return books;
    }

    public List<Book> findBySubjectName(String name)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE subject_id = (SELECT id FROM subjects WHERE name = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par sujet", ex);
        }

        return books;
    }

    public List<Book> findByTitle(String title)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par titre", ex);
        }

        return books;
    }

    public List<Book> findByIsbn(String isbn)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par ISBN", ex);
        }

        return books;
    }

    public Book findIsbn(String isbn)
    {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                return book;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du livre par ISBN", ex);
        }

        return null;
    }

    public List<Book> findByPrice(float price)
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE price <= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setFloat(1, price);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des livres par prix", ex);
        }

        return books;
    }


    public List<Book> findAll()
    {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Statement stmt = connection.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                Book book = mapResultSetToBook(rs);

                AuthorDAO authorDAO = new AuthorDAO();
                Author author = authorDAO.findById(rs.getInt("author_id"));
                if (author != null)
                {
                    book.setAuthor(author);
                }

                SubjectDAO subjectDAO = new SubjectDAO();
                Subject subject = subjectDAO.findById(rs.getInt("subject_id"));
                if (subject != null)
                {
                    book.setSubject(subject);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de tous les livres", ex);
        }

        return books;
    }


    /* Update et delete pas besoin pour le moment*/




    private Book mapResultSetToBook(ResultSet rs) throws SQLException
    {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.getAuthor().setId(rs.getInt("author_id"));
        book.getSubject().setId(rs.getInt("subject_id"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getString("isbn"));
        book.setPageCount(rs.getInt("page_count"));
        book.setStockQuantity(rs.getInt("stock_quantity"));
        book.setPrice(rs.getFloat("price"));
        book.setPublishYear(rs.getInt("publish_year"));
        return book;
    }

    public boolean update(Book book)
    {
        //verifier si le livre existe deja
        Book bookExist = findById(book.getId());
        if (bookExist == null)
        {
            LOGGER.log(Level.INFO, "Livre n'existe pas, pas besoin de le mettre a jour");
            return false;
        }
        String sql = "UPDATE books SET author_id = ?, subject_id = ?, title = ?, isbn = ?, page_count = ?, stock_quantity = ?, price = ?, publish_year = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, book.getAuthor().getId());
            stmt.setInt(2, book.getSubject().getId());
            stmt.setString(3, book.getTitle());
            stmt.setString(4, book.getIsbn());
            stmt.setInt(5, book.getPageCount());
            stmt.setInt(6, book.getStockQuantity());
            stmt.setFloat(7, book.getPrice());
            stmt.setInt(8, book.getPublishYear());
            stmt.setInt(9, book.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows == 1;
        } catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise a jour du livre", ex);
            return false;
        }
    }
}
