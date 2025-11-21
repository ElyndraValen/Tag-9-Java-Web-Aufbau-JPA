package com.javafleet.web;

import com.javafleet.model.*;
import com.javafleet.service.OrderManagementService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    
    @Inject
    private OrderManagementService orderService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        
        printHeader(out, "Java Web Aufbau Tag 9 - Order Management Demo");
        
        try {
            if ("create".equals(action)) {
                handleCreate(out);
            } else if ("list".equals(action)) {
                handleList(out);
            } else if ("stats".equals(action)) {
                handleStats(out);
            } else {
                printMenu(out);
            }
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<h2>❌ Fehler</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
        }
        
        printFooter(out);
    }
    
    private void handleCreate(PrintWriter out) {
        // Test: User mit Profile erstellen
        User user = orderService.createUserWithProfile(
            "alice_" + System.currentTimeMillis(),
            "alice@example.com",
            "Alice",
            "Johnson",
            LocalDate.of(1990, 5, 15)
        );
        
        out.println("<div class='success'>");
        out.println("<h2>✅ User erstellt</h2>");
        out.println("<p><strong>ID:</strong> " + user.getId() + "</p>");
        out.println("<p><strong>Username:</strong> " + user.getUsername() + "</p>");
        out.println("<p><strong>Email:</strong> " + user.getEmail() + "</p>");
        out.println("<p><strong>Name:</strong> " 
            + user.getProfile().getFirstName() + " " 
            + user.getProfile().getLastName() + "</p>");
        out.println("<p><strong>Geburtsdatum:</strong> " 
            + user.getProfile().getDateOfBirth() + "</p>");
        out.println("</div>");
        
        // Orders erstellen
        Order order1 = orderService.createOrder(
            user.getId(),
            "ORD-" + System.currentTimeMillis() + "-001",
            new BigDecimal("99.99")
        );
        
        Order order2 = orderService.createOrder(
            user.getId(),
            "ORD-" + System.currentTimeMillis() + "-002",
            new BigDecimal("149.50")
        );
        
        out.println("<div class='success'>");
        out.println("<h2>✅ Orders erstellt</h2>");
        out.println("<p><strong>Order 1:</strong> " + order1.getOrderNumber() 
            + " - €" + order1.getTotalAmount() + "</p>");
        out.println("<p><strong>Order 2:</strong> " + order2.getOrderNumber() 
            + " - €" + order2.getTotalAmount() + "</p>");
        out.println("</div>");
        
        out.println("<p><a href='orders?action=list' class='button'>📋 Alle Orders anzeigen</a></p>");
    }
    
    private void handleList(PrintWriter out) {
        List<Order> orders = orderService.findRecentOrders(20);
        
        out.println("<h2>📋 Recent Orders</h2>");
        
        if (orders.isEmpty()) {
            out.println("<div class='info'>");
            out.println("<p>Keine Orders vorhanden. " +
                "<a href='orders?action=create'>Test-Daten erstellen</a></p>");
            out.println("</div>");
        } else {
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Order#</th>");
            out.println("<th>User</th>");
            out.println("<th>Amount</th>");
            out.println("<th>Status</th>");
            out.println("<th>Date</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            
            for (Order order : orders) {
                out.println("<tr>");
                out.println("<td>" + order.getOrderNumber() + "</td>");
                out.println("<td>" + order.getUser().getUsername() + "</td>");
                out.println("<td>€" + order.getTotalAmount() + "</td>");
                out.println("<td><span class='status-" + order.getStatus().name().toLowerCase() 
                    + "'>" + order.getStatus() + "</span></td>");
                out.println("<td>" + order.getOrderDate().format(formatter) + "</td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
        }
        
        out.println("<p><a href='orders' class='button'>🏠 Zurück zum Menü</a></p>");
    }
    
    private void handleStats(PrintWriter out) {
        List<Order> orders = orderService.findRecentOrders(1);
        
        if (orders.isEmpty()) {
            out.println("<div class='info'>");
            out.println("<p>Keine Statistiken verfügbar. " +
                "<a href='orders?action=create'>Test-Daten erstellen</a></p>");
            out.println("</div>");
            return;
        }
        
        Long userId = orders.get(0).getUser().getId();
        User user = orderService.findUserWithOrders(userId);
        
        Long orderCount = orderService.countOrdersByUser(userId);
        BigDecimal totalAmount = orderService.getTotalAmountByUser(userId);
        
        out.println("<h2>📊 Statistiken für User: " + user.getUsername() + "</h2>");
        
        out.println("<div class='stats'>");
        out.println("<div class='stat-box'>");
        out.println("<h3>Anzahl Orders</h3>");
        out.println("<p class='stat-value'>" + orderCount + "</p>");
        out.println("</div>");
        
        out.println("<div class='stat-box'>");
        out.println("<h3>Gesamtsumme</h3>");
        out.println("<p class='stat-value'>€" + totalAmount + "</p>");
        out.println("</div>");
        
        out.println("<div class='stat-box'>");
        out.println("<h3>Durchschnitt</h3>");
        BigDecimal avg = orderCount > 0 ? 
            totalAmount.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        out.println("<p class='stat-value'>€" + avg + "</p>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<p><a href='orders' class='button'>🏠 Zurück zum Menü</a></p>");
    }
    
    private void printMenu(PrintWriter out) {
        out.println("<h2>🚀 Order Management System</h2>");
        out.println("<p>Willkommen zur Demo von JPA Relationen (@OneToOne & @ManyToOne)!</p>");
        
        out.println("<div class='menu'>");
        out.println("<a href='orders?action=create' class='button'>➕ Test-Daten erstellen</a>");
        out.println("<a href='orders?action=list' class='button'>📋 Orders anzeigen</a>");
        out.println("<a href='orders?action=stats' class='button'>📊 Statistiken</a>");
        out.println("</div>");
        
        out.println("<div class='info'>");
        out.println("<h3>ℹ️ Was wird demonstriert?</h3>");
        out.println("<ul>");
        out.println("<li><strong>@OneToOne:</strong> User ↔ UserProfile</li>");
        out.println("<li><strong>@ManyToOne:</strong> Orders → User</li>");
        out.println("<li><strong>CascadeType:</strong> Automatische Propagierung</li>");
        out.println("<li><strong>JOIN FETCH:</strong> Effizientes Laden</li>");
        out.println("<li><strong>Bidirektionale Relationen:</strong> User ↔ Orders</li>");
        out.println("</ul>");
        out.println("</div>");
    }
    
    private void printHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='de'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>" + title + "</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<header>");
        out.println("<h1>☕ " + title + "</h1>");
        out.println("<p class='subtitle'>Von Elyndra Valen | Java Fleet Systems Consulting</p>");
        out.println("</header>");
        out.println("<main>");
    }
    
    private void printFooter(PrintWriter out) {
        out.println("</main>");
        out.println("<footer>");
        out.println("<p>© 2025 Java Fleet Systems Consulting | " +
            "<a href='https://www.java-developer.online'>java-developer.online</a></p>");
        out.println("</footer>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
