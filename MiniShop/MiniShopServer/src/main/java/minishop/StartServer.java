package minishop;

import minishop.networking.MiniShopConcurrentServer;
import minishop.repos.ProductRepository;
import minishop.repos.UserRepository;
import minishop.services.IMiniShopServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartServer {

    private static final int DEFAULT_PORT = 55555;

    public static void main(String[] args) {

        int port = DEFAULT_PORT;
        try (InputStream input = StartServer.class.getClassLoader()
                .getResourceAsStream("server.config")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                port = Integer.parseInt(props.getProperty("server.port", String.valueOf(DEFAULT_PORT)));
            }
        } catch (IOException e) {
            System.out.println("Nu am gasit server.config, folosesc portul default: " + DEFAULT_PORT);
        }

        // repositories
        UserRepository userRepository = new UserRepository();
        ProductRepository productRepository = new ProductRepository();

        // services
        MiniShopServicesImpl services = new MiniShopServicesImpl(userRepository, productRepository);

        // seed database daca e goala
        services.seedIfEmpty();

        // server
        MiniShopConcurrentServer server = new MiniShopConcurrentServer(port, services);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Eroare la pornirea serverului: " + e.getMessage());
            e.printStackTrace();
        }
    }
}