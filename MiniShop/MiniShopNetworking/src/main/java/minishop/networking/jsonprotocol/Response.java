package minishop.networking;

import minishop.networking.dto.ProductDTO;
import minishop.networking.dto.UserDTO;
import minishop.networking.jsonprotocol.ResponseType;

import java.util.List;

public class Response {
    private ResponseType type;
    private String errorMessage;

    private UserDTO user;

    private List<ProductDTO> products;

    private ProductDTO product;

    public Response() {}

    public ResponseType getType() { return type; }
    public void setType(ResponseType type) { this.type = type; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }
}