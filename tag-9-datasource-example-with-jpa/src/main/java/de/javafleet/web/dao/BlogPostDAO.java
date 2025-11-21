package de.javafleet.web.dao;

import de.javafleet.web.model.BlogPost;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BlogPost Data Access Object (DAO)
 * Verwaltet alle Datenbankzugriffe für BlogPost-Objekte
 */
//@ApplicationScoped
//@Transactional
@Stateless
public class BlogPostDAO {
    
    @PersistenceContext
    private EntityManager em;
    
    
    /**
     * Holt alle Blog-Posts aus der Datenbank
     */
    public List<BlogPost> findAll() throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        
        
        return posts;
    }
    
    /**
     * Holt einen Blog-Post anhand der ID
     * @param id
     * @return 
     */
    public BlogPost findById(int id)  {
        BlogPost bp=em.find(BlogPost.class, id);
        
        
        return bp;
    }
    
    /**
     * Holt Posts eines bestimmten Autors
     */
    public List<BlogPost> findByAuthor(String author) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        Query q=em.createQuery("SELECT b FROM BlogPost b WHERE b.author LIKE :=author");
        q.setParameter("author", author);
        posts=q.getResultList();
        
        return posts;
    }
    
    /**
     * Sucht Blog-Posts anhand eines Suchbegriffs (Titel oder Content)
     */
    public List<BlogPost> search(String query) throws SQLException {
        List<BlogPost> posts = new ArrayList<>();
        Query q=em.createQuery("SELECT b FROM BlogPost b WHERE b.title LIKE :=para1 OR b.content LIKE :=para1");
        q.setParameter("para1", query);
        posts=q.getResultList();
        
        return posts;
    }
    
    /**
     * Holt eine paginierte Liste von Blog-Posts
     * @param limit Anzahl der Posts pro Seite
     * @param offset Startposition
     */
//    public List<BlogPost> findPaginated(int limit, int offset) throws SQLException {
//        List<BlogPost> posts = new ArrayList<>();
//        String sql = "SELECT id, title, content, author, created_at, updated_at " +
//                    "FROM blog_posts ORDER BY created_at DESC LIMIT ? OFFSET ?";
//        
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            
//            stmt.setInt(1, limit);
//            stmt.setInt(2, offset);
//            
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    posts.add(mapRowToBlogPost(rs));
//                }
//            }
//        }
//        
//        return posts;
//    }
    
    /**
     * Erstellt einen neuen Blog-Post
     */
    public void create(BlogPost post) throws SQLException {
       
        em.persist(post);
    }
    
    /**
     * Aktualisiert einen existierenden Blog-Post
     */
    public void update(BlogPost post) throws SQLException {
      em.merge(post);
    }
    
    /**
     * Löscht einen Blog-Post anhand der ID
     */
    public void delete(int id) throws SQLException {
       em.remove(em.find(BlogPost.class, id));
    }
    
    /**
     * Zählt alle Blog-Posts
     */
    public int count() throws SQLException {
        Query q =em.createQuery("Select b From BlogPost as b");
        
        return q.getResultList().size();
    }
    
  
}
