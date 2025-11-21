package de.javafleet.web.dao;

import de.javafleet.web.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object (DAO)
 * Verwaltet alle Datenbankzugriffe für User-Objekte
 */
@Stateless
public class UserDAO {

    @PersistenceContext(unitName = "mywebapp_jpa")
    private EntityManager em;
    
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
        
        Query q=em.createQuery("SELECT u FROM User u ");
        List<User> us=q.getResultList();
        return us;
    }
    
    /**
     * Holt einen User anhand der ID
     */
    public User findById(int id) throws SQLException {
       
        
        return em.find(User.class, id);
    }
    
    /**
     * Holt einen User anhand des Usernames
     */
    public User findByUsername(String username) throws SQLException {
        Query q=em.createQuery("SELECT u FROM User u WHERE u.username=:username");
        q.setParameter("username", username);
        return (User) q.getSingleResult();
    }
    
    /**
     * Sucht User anhand eines Suchbegriffs (username oder email)
     */
    public List<User> search(String query) throws SQLException {
        List<User> users = new ArrayList<>();
       
        Query q=em.createQuery("SELECT u FROM User u WHERE u.username LIKE :=para1");
        q.setParameter("para1", query);
        users=q.getResultList();
        return users;
    }
    
    /**
     * Erstellt einen neuen User in der Datenbank
     * Die generierte ID wird im User-Objekt gesetzt
     */
    public void create(User user) throws SQLException {
       
        em.persist(user);
        
      
    }
    
    /**
     * Aktualisiert einen existierenden User
     */
    public void update(User user) throws SQLException {
       em.merge(user);
    }
    
    /**
     * Löscht einen User anhand der ID
     */
    public void delete(int id) throws SQLException {
        em.remove(em.find(User.class, id));
    }
    
    /**
     * Prüft, ob ein Username bereits existiert
     */
    public boolean usernameExists(String username) throws SQLException {
         
        Query q=em.createQuery("SELECT u FROM User u WHERE u.username =:_username ");
        q.setParameter("username", username);
        if(q.getSingleResult()!=null){
            return true;
        }
        return false;
    }
    
    /**
     * Zählt alle User in der Datenbank
     */
    public int count() throws SQLException {
        Query q =em.createQuery("SELECT u FROM User u");
        
        
        return q.getResultList().size();
    }
    
    /**
     * Helper-Methode: Mappt eine ResultSet-Zeile zu einem User-Objekt
     */
//    private User mapRowToUser(ResultSet rs) throws SQLException {
//        User user = new User();
//        user.setId(rs.getInt("id"));
//        user.setUsername(rs.getString("username"));
//        user.setEmail(rs.getString("email"));
//        return user;
//    }
}
