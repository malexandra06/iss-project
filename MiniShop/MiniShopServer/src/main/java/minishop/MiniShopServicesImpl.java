package minishop;

import minishop.model.Product;
import minishop.model.User;
import minishop.repos.ProductRepository;
import minishop.repos.UserRepository;
import minishop.services.IMiniShopObserver;
import minishop.services.IMiniShopServices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiniShopServicesImpl implements IMiniShopServices {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final Map<User, IMiniShopObserver> loggedInUsers;

    public MiniShopServicesImpl(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.loggedInUsers = new ConcurrentHashMap<>();
    }

    public void seedIfEmpty() {
        try {
            if (userRepository.findAll().isEmpty()) {
                System.out.println("Seeding users from CSV...");
                seedUsers();
            } else {
                System.out.println("Users already exist, skipping seed.");
            }

            if (productRepository.findAll().isEmpty()) {
                System.out.println("Seeding products from CSV...");
                seedProducts();
            } else {
                System.out.println("Products already exist, skipping seed.");
            }
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedUsers() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("users.csv")) {
            if (is == null) {
                System.err.println("users.csv not found in resources!");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String header = reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                User user = new User(
                        parts[0].trim(), // username
                        parts[1].trim(), // password
                        parts[2].trim(), // name
                        parts[3].trim(), // phone
                        parts[4].trim()  // address
                );
                userRepository.save(user);
                System.out.println("  Added user: " + user.getUsername());
            }
        }
    }

    private void seedProducts() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("products.csv")) {
            if (is == null) {
                System.err.println("products.csv not found in resources!");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String header = reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                Product product = new Product(
                        parts[0].trim(), // name
                        parts[1].trim(), // category
                        Double.parseDouble(parts[2].trim()), // price
                        parts[3].trim(), // description
                        LocalDate.parse(parts[4].trim()), // fabricationDate
                        Integer.parseInt(parts[5].trim()), // noItems
                        parts[6].trim()  // photoName
                );
                productRepository.save(product);
                System.out.println("  Added product: " + product.getName());
            }
        }
    }

    @Override
    public synchronized User login(String username, String password, IMiniShopObserver observer) throws Exception {
        if (username == null || username.isEmpty() || password == null || password.isEmpty())
            throw new Exception("Username si parola nu pot fi goale!");

        User found = null;
        for (User user : userRepository.findAll()) {
            if (user.getUsername().equals(username)) {
                found = user;
                break;
            }
        }

        if (found == null)
            throw new Exception("Username nu a fost gasit!");

        for (User user : loggedInUsers.keySet()) {
            if (user.getUsername().equals(username)) {
                throw new Exception("Userul este deja logat!");
            }
        }

        if (!found.getPassword().equals(password))
            throw new Exception("Parola gresita!");

        loggedInUsers.put(found, observer);
        return found;
    }

    @Override
    public synchronized void logout(User user) throws Exception {
        if (!loggedInUsers.containsKey(user))
            throw new Exception("Userul nu este logat!");
        loggedInUsers.remove(user);
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        return productRepository.findAll();
    }

    private void notifyAll(ObserverAction action) {
        for (IMiniShopObserver obs : loggedInUsers.values()) {
            try {
                action.execute(obs);
            } catch (Exception e) {
                System.err.println("Error notifying: " + e.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface ObserverAction {
        void execute(IMiniShopObserver observer);
    }
}