package de.javafleet.web.servlets;

import de.javafleet.web.dao.BlogPostDAO;
import de.javafleet.web.model.BlogPost;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * BlogServlet - Verwaltet Blog-Posts
 * 
 * URLs:
 * - GET  /blog → Liste aller Posts
 * - GET  /blog?id=X → Details zu Post X
 * - GET  /blog?action=new → Formular für neuen Post
 * - POST /blog → Neuen Post erstellen
 * - GET  /blog?action=edit&id=X → Formular zum Bearbeiten
 * - POST /blog?action=update&id=X → Post aktualisieren
 * - POST /blog?action=delete&id=X → Post löschen
 */
@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    
    @Resource(name = "jdbc/MyWebAppDB")
    private DataSource dataSource;
    
    private BlogPostDAO blogDAO;
    
    @Override
    public void init() throws ServletException {
        blogDAO = new BlogPostDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
                        throws ServletException, IOException {
        
        try {
            String action = request.getParameter("action");
            String idParam = request.getParameter("id");
            String searchQuery = request.getParameter("search");
            String pageParam = request.getParameter("page");
            
            if ("new".equals(action)) {
                // Formular für neuen Post anzeigen
                showNewPostForm(request, response);
            } else if ("edit".equals(action) && idParam != null) {
                // Formular zum Bearbeiten anzeigen
                showEditPostForm(request, response, idParam);
            } else if (idParam != null) {
                // Einzelnen Post anzeigen
                showPost(request, response, idParam);
            } else if (searchQuery != null) {
                // Posts suchen
                searchPosts(request, response, searchQuery);
            } else if (pageParam != null) {
                // Paginierte Liste anzeigen
                showPaginatedPosts(request, response, pageParam);
            } else {
                // Alle Posts anzeigen
                showAllPosts(request, response);
            }
            
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, 
                         HttpServletResponse response) 
                         throws ServletException, IOException {
        
        try {
            String action = request.getParameter("action");
            
            if ("update".equals(action)) {
                // Post aktualisieren
                updatePost(request, response);
            } else if ("delete".equals(action)) {
                // Post löschen
                deletePost(request, response);
            } else {
                // Neuen Post erstellen
                createPost(request, response);
            }
            
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    /**
     * Zeigt alle Blog-Posts an
     */
    private void showAllPosts(HttpServletRequest request, 
                             HttpServletResponse response) 
                             throws SQLException, ServletException, IOException {
        
        List<BlogPost> posts = blogDAO.findAll();
        int postCount = blogDAO.count();
        
        request.setAttribute("posts", posts);
        request.setAttribute("postCount", postCount);
        
        request.getRequestDispatcher("/WEB-INF/views/blog-list.jsp")
               .forward(request, response);
    }
    
    /**
     * Zeigt paginierte Blog-Posts an
     */
    private void showPaginatedPosts(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   String pageParam) 
                                   throws SQLException, ServletException, IOException {
        
        int page = 1;
        try {
            page = Integer.parseInt(pageParam);
            if (page < 1) page = 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        int postsPerPage = 10;
        int offset = (page - 1) * postsPerPage;
        
        List<BlogPost> posts = blogDAO.findPaginated(postsPerPage, offset);
        int totalPosts = blogDAO.count();
        int totalPages = (int) Math.ceil((double) totalPosts / postsPerPage);
        
        request.setAttribute("posts", posts);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("postsPerPage", postsPerPage);
        
        request.getRequestDispatcher("/WEB-INF/views/blog-list.jsp")
               .forward(request, response);
    }
    
    /**
     * Zeigt einen einzelnen Blog-Post an
     */
    private void showPost(HttpServletRequest request, 
                         HttpServletResponse response, 
                         String idParam) 
                         throws SQLException, ServletException, IOException {
        
        try {
            int id = Integer.parseInt(idParam);
            BlogPost post = blogDAO.findById(id);
            
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                                  "Blog post not found");
                return;
            }
            
            request.setAttribute("post", post);
            request.getRequestDispatcher("/WEB-INF/views/blog-post.jsp")
                   .forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "Invalid post ID");
        }
    }
    
    /**
     * Sucht Blog-Posts anhand eines Suchbegriffs
     */
    private void searchPosts(HttpServletRequest request, 
                            HttpServletResponse response, 
                            String searchQuery) 
                            throws SQLException, ServletException, IOException {
        
        List<BlogPost> posts = blogDAO.search(searchQuery);
        
        request.setAttribute("posts", posts);
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("postCount", posts.size());
        
        request.getRequestDispatcher("/WEB-INF/views/blog-list.jsp")
               .forward(request, response);
    }
    
    /**
     * Zeigt Formular für neuen Post an
     */
    private void showNewPostForm(HttpServletRequest request, 
                                HttpServletResponse response) 
                                throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/blog-form.jsp")
               .forward(request, response);
    }
    
    /**
     * Zeigt Formular zum Bearbeiten eines Posts an
     */
    private void showEditPostForm(HttpServletRequest request, 
                                 HttpServletResponse response, 
                                 String idParam) 
                                 throws SQLException, ServletException, IOException {
        
        try {
            int id = Integer.parseInt(idParam);
            BlogPost post = blogDAO.findById(id);
            
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                                  "Blog post not found");
                return;
            }
            
            request.setAttribute("post", post);
            request.getRequestDispatcher("/WEB-INF/views/blog-form.jsp")
                   .forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "Invalid post ID");
        }
    }
    
    /**
     * Erstellt einen neuen Blog-Post
     */
    private void createPost(HttpServletRequest request, 
                           HttpServletResponse response) 
                           throws SQLException, IOException {
        
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String author = request.getParameter("author");
        
        // Validierung
        if (title == null || title.trim().isEmpty() ||
            content == null || content.trim().isEmpty() ||
            author == null || author.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "All fields are required");
            return;
        }
        
        BlogPost post = new BlogPost(title, content, author);
        blogDAO.create(post);
        
        // Redirect nach Post-Redirect-Get Pattern
        response.sendRedirect("blog?id=" + post.getId());
    }
    
    /**
     * Aktualisiert einen existierenden Blog-Post
     */
    private void updatePost(HttpServletRequest request, 
                           HttpServletResponse response) 
                           throws SQLException, IOException {
        
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String author = request.getParameter("author");
        
        try {
            int id = Integer.parseInt(idParam);
            
            // Validierung
            if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty() ||
                author == null || author.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                                  "All fields are required");
                return;
            }
            
            BlogPost post = new BlogPost();
            post.setId(id);
            post.setTitle(title);
            post.setContent(content);
            post.setAuthor(author);
            
            blogDAO.update(post);
            
            // Redirect nach Post-Redirect-Get Pattern
            response.sendRedirect("blog?id=" + id);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "Invalid post ID");
        }
    }
    
    /**
     * Löscht einen Blog-Post
     */
    private void deletePost(HttpServletRequest request, 
                           HttpServletResponse response) 
                           throws SQLException, IOException {
        
        String idParam = request.getParameter("id");
        
        try {
            int id = Integer.parseInt(idParam);
            blogDAO.delete(id);
            
            // Redirect zur Liste
            response.sendRedirect("blog");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                              "Invalid post ID");
        }
    }
}
