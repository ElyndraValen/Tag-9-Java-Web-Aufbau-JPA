package de.javafleet.web.dao;

import de.javafleet.web.model.BlogPost;
import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BlogPost Data Access Object (DAO)
 * Verwaltet alle Datenbankzugriffe für BlogPost-Objekte
 */
public class BlogPostDAO {
    
    @Resource(name = "jdbc/MyWebAppDB")
    private DataSource dataSource;
    
    /**
     * Holt alle Blog-Posts aus der Datenbank
     */
    public List<BlogPost> findAll() throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at, updated_at " +
                    "FROM blog_posts ORDER BY created_at DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                posts.add(mapRowToBlogPost(rs));
            }
        }
        
        return posts;
    }
    
    /**
     * Holt einen Blog-Post anhand der ID
     */
    public BlogPost findById(int id) throws SQLException {
        String sql = "SELECT id, title, content, author, created_at, updated_at " +
                    "FROM blog_posts WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBlogPost(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Holt Posts eines bestimmten Autors
     */
    public List<BlogPost> findByAuthor(String author) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at, updated_at " +
                    "FROM blog_posts WHERE author = ? ORDER BY created_at DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, author);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapRowToBlogPost(rs));
                }
            }
        }
        
        return posts;
    }
    
    /**
     * Sucht Blog-Posts anhand eines Suchbegriffs (Titel oder Content)
     */
    public List<BlogPost> search(String query) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at, updated_at " +
                    "FROM blog_posts WHERE title LIKE ? OR content LIKE ? " +
                    "ORDER BY created_at DESC";
        
        String searchPattern = "%" + query + "%";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapRowToBlogPost(rs));
                }
            }
        }
        
        return posts;
    }
    
    /**
     * Holt eine paginierte Liste von Blog-Posts
     * @param limit Anzahl der Posts pro Seite
     * @param offset Startposition
     */
    public List<BlogPost> findPaginated(int limit, int offset) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        String sql = "SELECT id, title, content, author, created_at, updated_at " +
                    "FROM blog_posts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapRowToBlogPost(rs));
                }
            }
        }
        
        return posts;
    }
    
    /**
     * Erstellt einen neuen Blog-Post
     */
    public void create(BlogPost post) throws SQLException {
        String sql = "INSERT INTO blog_posts (title, content, author) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getAuthor());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating blog post failed, no rows affected.");
            }
            
            // Generierte ID holen
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating blog post failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Aktualisiert einen existierenden Blog-Post
     */
    public void update(BlogPost post) throws SQLException {
        String sql = "UPDATE blog_posts SET title = ?, content = ?, author = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getAuthor());
            stmt.setInt(4, post.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating blog post failed, no rows affected.");
            }
        }
    }
    
    /**
     * Löscht einen Blog-Post anhand der ID
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM blog_posts WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting blog post failed, no rows affected.");
            }
        }
    }
    
    /**
     * Zählt alle Blog-Posts
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM blog_posts";
        
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
     * Helper-Methode: Mappt eine ResultSet-Zeile zu einem BlogPost-Objekt
     */
    private BlogPost mapRowToBlogPost(ResultSet rs) throws SQLException {
        BlogPost post = new BlogPost();
        post.setId(rs.getInt("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setAuthor(rs.getString("author"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            post.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            post.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        return post;
    }
}
