package minishop.services;

import minishop.model.User;
import minishop.model.Product;

import java.util.List;
import java.util.Map;

public interface IMiniShopServices {
    User login(String username, String password, IMiniShopObserver observer) throws Exception;
    void logout(User user) throws Exception;
    List<Product> getAllProducts() throws Exception;
    List<Product> searchProducts(String query) throws Exception;
    void placeOrder(String userId, Map<String, Integer> productQuantities) throws Exception;
}