package MODEL.DAO;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDB {
    private static ConnectDB instance = null;
    private static Connection connection = null;

    private static final String URL = "jdbc:mysql://192.168.163.128/PourStudent";
    private static final String USER = "Student";
    private static final String PASSWORD = "PassStudent1_";
    private static final Logger LOGGER = Logger.getLogger(ConnectDB.class.getName());

    // Constructeur privé pour empêcher l'instanciation directe
    private ConnectDB() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LOGGER.info("Database connection established");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to database", ex);
            throw new RuntimeException("Failed to connect to database", ex);
        }
    }

    // Méthode pour obtenir l'instance unique
    public static synchronized ConnectDB getInstance() {
        if (instance == null) {
            instance = new ConnectDB();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new ConnectDB();
            }
            return connection;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking connection state", ex);
            throw new RuntimeException("Failed to get database connection", ex);
        }
    }

    // Méthode pour fermer la connexion
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                instance = null;
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error closing database connection", ex);
        }
    }

    // Empêcher le clonage
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton ConnectDB is not allowed");
    }

    public static void main(String[] args)
    {
        ConnectDB bd = ConnectDB.getInstance();//on recupere l'instance de la connexion
        Connection conn = bd.getConnection();//on recupere la connexion

        if(conn != null)
        {
            System.out.println("Connexion établie");
            //rucuperation des auteurs
            try
            {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM authors");
                while(rs.next())
                {
                    //formatage de l'affichage
                    System.out.println("ID : "+rs.getInt("id")+" Nom : "+rs.getString("last_name")+" Prenom : "+rs.getString("first_name")+" Date de naissance : "+rs.getString("birth_date"));
                }
                ConnectDB.closeConnection();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Erreur de connexion");
        }





    }
}