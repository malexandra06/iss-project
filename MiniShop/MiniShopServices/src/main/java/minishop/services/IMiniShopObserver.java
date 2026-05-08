package minishop.services;

import minishop.model.Product;

public interface IMiniShopObserver {
    void productUpdated(Product product);
}