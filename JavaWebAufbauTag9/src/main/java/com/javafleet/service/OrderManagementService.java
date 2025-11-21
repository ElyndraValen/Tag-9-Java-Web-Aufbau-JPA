package com.javafleet.service;

import com.javafleet.model.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class OrderManagementService {
    
    @PersistenceContext
    private EntityManager em;
    
    // User mit Profile erstellen
    public User createUserWithProfile(String username, String email, 
                                     String firstName, String lastName, 
                                     LocalDate dateOfBirth) {
        UserProfile profile = new UserProfile(firstName, lastName);
        profile.setDateOfBirth(dateOfBirth);
        
        User user = new User(username, email);
        user.setProfile(profile);
        
        em.persist(user);
        return user;
    }
    
    // Order erstellen und User zuordnen
    public Order createOrder(Long userId, String orderNumber, BigDecimal amount) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        
        Order order = new Order(orderNumber, amount, user);
        user.addOrder(order); // Bidirektionale Relation pflegen!
        
        em.persist(order);
        return order;
    }
    
    // User mit allen Orders laden (JOIN FETCH)
    public User findUserWithOrders(Long userId) {
        return em.createQuery(
            "SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id", 
            User.class
        )
        .setParameter("id", userId)
        .getSingleResult();
    }
    
    // Orders mit User-Info laden (JOIN FETCH)
    public List<Order> findRecentOrders(int limit) {
        return em.createQuery(
            "SELECT o FROM Order o JOIN FETCH o.user " +
            "ORDER BY o.orderDate DESC", 
            Order.class
        )
        .setMaxResults(limit)
        .getResultList();
    }
    
    // Order Status aktualisieren
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }
    
    // User Profile aktualisieren
    public void updateUserProfile(Long userId, String bio, String phone) {
        User user = em.find(User.class, userId);
        if (user != null && user.getProfile() != null) {
            user.getProfile().setBio(bio);
            user.getProfile().setPhoneNumber(phone);
        }
    }
    
    // User mit allen abhängigen Entities löschen
    public void deleteUser(Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            em.remove(user);
            // Profile: gelöscht (orphanRemoval = true)
            // Orders: gelöscht (cascade = CascadeType.REMOVE)
        }
    }
    
    // Einzelne Order löschen
    public void deleteOrder(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            User user = order.getUser();
            user.removeOrder(order); // Bidirektionale Relation pflegen!
            em.remove(order);
        }
    }
    
    // Statistics - Orders pro User
    public Long countOrdersByUser(Long userId) {
        return em.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId", 
            Long.class
        )
        .setParameter("userId", userId)
        .getSingleResult();
    }
    
    // Statistics - Total Amount pro User
    public BigDecimal getTotalAmountByUser(Long userId) {
        BigDecimal result = em.createQuery(
            "SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId", 
            BigDecimal.class
        )
        .setParameter("userId", userId)
        .getSingleResult();
        
        return result != null ? result : BigDecimal.ZERO;
    }
}
