package minishop.networking.dto;

import minishop.model.User;
import minishop.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DTOUtils {

    public static UserDTO getDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(),
                user.getName(), user.getPhone(), user.getAddress());
    }

    public static User getFromDTO(UserDTO dto) {
        User user = new User(dto.getUsername(), dto.getPassword(), dto.getName(),
                dto.getPhone(), dto.getAddress());
        user.setId(dto.getId());
        return user;
    }

    public static ProductDTO getDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setFabricationDate(product.getFabricationDate().toString());
        dto.setNoItems(product.getNoItems());
        dto.setPhotoName(product.getPhotoName());
        return dto;
    }

    public static Product getFromDTO(ProductDTO dto) {
        Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(),
                dto.getDescription(), LocalDate.parse(dto.getFabricationDate()),
                dto.getNoItems(), dto.getPhotoName());
        product.setId(dto.getId());
        return product;
    }

    public static List<ProductDTO> getDTO(List<Product> products) {
        return products.stream().map(DTOUtils::getDTO).collect(Collectors.toList());
    }

    public static List<Product> getFromDTOList(List<ProductDTO> dtos) {
        return dtos.stream().map(DTOUtils::getFromDTO).collect(Collectors.toList());
    }
}