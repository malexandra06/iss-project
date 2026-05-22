package minishop.repos;

import minishop.model.Order;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class OrderRepository {

    public Order save(Order order) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
            return order;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Order> findByUserId(String userId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o WHERE o.userId = :userId", Order.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}