package de.javafleet.web.servlets;

import de.javafleet.web.dao.UserDAO;
import de.javafleet.web.model.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * UserListServlet - Zeigt alle User an oder einen einzelnen User
 * 
 * URLs:
 * - /users → Liste aller User
 * - /users?id=X → Details zu User X
 * - /users?search=query → Suche nach User
 */
@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    
    @EJB
    private UserDAO userDAO;
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try {
            String idParam = request.getParameter("id");
            String searchQuery = request.getParameter("search");
            
            if (idParam != null) {
                // Einzelnen User anzeigen
                showUser(request, response, idParam);
            } else if (searchQuery != null) {
                // User suchen
                searchUsers(request, response, searchQuery);
            } else {
                // Alle User anzeigen
                showAllUsers(request, response);
            }
            
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    /**
     * Zeigt alle User an
     */
    private void showAllUsers(HttpServletRequest request, 
                             HttpServletResponse response) 
                             throws SQLException, ServletException, IOException {
        
        List<User> users = userDAO.findAll();
        int userCount = userDAO.count();
        
        request.setAttribute("users", users);
        request.setAttribute("userCount", userCount);
        
        request.getRequestDispatcher("/WEB-INF/views/user-list.jsp")
               .forward(request, response);
    }
    
    /**
     * Zeigt einen einzelnen User an
     */
    private void showUser(HttpServletRequest request, 
                         HttpServletResponse response, 
                         String idParam) 
                         throws SQLException, ServletException, IOException {
        
        try {
            int id = Integer.parseInt(idParam);
            User user = userDAO.findById(id);
            
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                                  "User not found");
                return;
            }
            
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user-detail.jsp")
                   .forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "Invalid user ID");
        }
    }
    
    /**
     * Sucht User anhand eines Suchbegriffs
     */
    private void searchUsers(HttpServletRequest request, 
                            HttpServletResponse response, 
                            String searchQuery) 
                            throws SQLException, ServletException, IOException {
        
        List<User> users = userDAO.search(searchQuery);
        
        request.setAttribute("users", users);
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("userCount", users.size());
        
        request.getRequestDispatcher("/WEB-INF/views/user-list.jsp")
               .forward(request, response);
    }
}
