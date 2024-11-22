package MODEL.DAO;

import MODEL.entity.Book;

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

    public Book create(Book book) {
        String sql = "INSERT INTO books (author_id, subject_id, title, isbn, page_count, stock_quantity, price, publish_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, book.getAuthor().getId());
            stmt.setInt(2, book.getSubject().getId());
            stmt.setString(3, book.getTitle());
            stmt.setString(4, book.getIsbn());
            stmt.setInt(5, book.getPageCount());
            stmt.setInt(6, book.getStockQuantity());
            stmt.setFloat(7, book.getPrice());
            stmt.setInt(8, book.getPublishYear());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                    return book;
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creating book", ex);
            return null;
        }
    }

    public List<Book> findByAuthorId(int authorId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding books by author", ex);
        }

        return books;
    }

    public List<Book> findBySubject(int subjectId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE subject_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding books by subject", ex);
        }

        return books;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
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
}
