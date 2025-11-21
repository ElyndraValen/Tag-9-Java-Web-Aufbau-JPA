package de.javafleet.web.dao;

import de.javafleet.web.model.User;
import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object (DAO)
 * Verwaltet alle Datenbankzugriffe für User-Objekte
 * 
 * Nutzt Dependency Injection für DataSource (moderne Methode)
 */
public class UserDAO {
    
    @Resource(name = "jdbc/MyWebAppDB")
    private DataSource dataSource;
    
    /**
     * Alternative: JNDI-Lookup im Konstruktor (klassische Methode)
     */
    /*
    public UserDAO() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/MyWebAppDB");
        } catch (NamingException e) {
            throw new RuntimeException("Datasource init failed", e);
        }
    }
    */
    
    /**
     * Holt alle User aus der Datenbank
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email FROM users ORDER BY username";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Holt einen User anhand der ID
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, email FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Holt einen User anhand des Usernames
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, email FROM users WHERE username = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Sucht User anhand eines Suchbegriffs (username oder email)
     */
    public List<User> search(String query) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email FROM users " +
                    "WHERE username LIKE ? OR email LIKE ? " +
                    "ORDER BY username";
        
        String searchPattern = "%" + query + "%";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Erstellt einen neuen User in der Datenbank
     * Die generierte ID wird im User-Objekt gesetzt
     */
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            // Generierte ID holen
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Aktualisiert einen existierenden User
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
        }
    }
    
    /**
     * Löscht einen User anhand der ID
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        }
    }
    
    /**
     * Prüft, ob ein Username bereits existiert
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Zählt alle User in der Datenbank
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Helper-Methode: Mappt eine ResultSet-Zeile zu einem User-Objekt
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
