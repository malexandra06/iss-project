package minishop.repos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HibernateUtil {
    private static final EntityManagerFactory entityManagerFactory;

    static {
        try {
            Properties dbProps = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader()
                    .getResourceAsStream("bd.config")) {
                if (input == null) {
                    throw new RuntimeException("Nu gasesc bd.config in resources!");
                }
                dbProps.load(input);
            }

            Map<String, String> overrides = new HashMap<>();

            overrides.put("jakarta.persistence.jdbc.url", dbProps.getProperty("db.url"));
            overrides.put("jakarta.persistence.jdbc.user", dbProps.getProperty("db.username"));
            overrides.put("jakarta.persistence.jdbc.password", dbProps.getProperty("db.password"));
            overrides.put("jakarta.persistence.jdbc.driver", dbProps.getProperty("db.driver"));

            if (dbProps.getProperty("hikari.maximumPoolSize") != null) {
                overrides.put("hibernate.hikari.maximumPoolSize", dbProps.getProperty("hikari.maximumPoolSize"));
            }
            if (dbProps.getProperty("hikari.minimumIdle") != null) {
                overrides.put("hibernate.hikari.minimumIdle", dbProps.getProperty("hikari.minimumIdle"));
            }
            if (dbProps.getProperty("hikari.connectionTimeout") != null) {
                overrides.put("hibernate.hikari.connectionTimeout", dbProps.getProperty("hikari.connectionTimeout"));
            }

            entityManagerFactory = Persistence.createEntityManagerFactory("MiniShopPU", overrides);
        } catch (IOException e) {
            throw new RuntimeException("Eroare la citirea db.config: " + e.getMessage(), e);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}