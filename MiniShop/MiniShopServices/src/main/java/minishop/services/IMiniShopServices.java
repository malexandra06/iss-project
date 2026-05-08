package minishop.services;

import minishop.model.User;
import minishop.model.Product;

import java.util.List;

public interface IMiniShopServices {
    User login(String username, String password, IMiniShopObserver observer) throws Exception;
    void logout(User user) throws Exception;

    List<Product> getAllProducts() throws Exception;
}