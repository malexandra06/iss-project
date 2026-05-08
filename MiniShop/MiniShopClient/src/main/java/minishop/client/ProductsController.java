package minishop.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import minishop.model.Product;
import minishop.model.User;
import minishop.services.IMiniShopServices;

import java.util.ArrayList;
import java.util.List;

public class ProductsController {

    @FXML private TextField searchField;
    @FXML private Label productCountLabel;
    @FXML private FlowPane productsPane;
    @FXML private ComboBox<String> sortCombo;

    @FXML private CheckBox catPhones;
    @FXML private CheckBox catLaptops;
    @FXML private CheckBox catTablets;
    @FXML private CheckBox catAccessories;
    @FXML private CheckBox brandSamsung;
    @FXML private CheckBox brandApple;
    @FXML private CheckBox brandLenovo;
    @FXML private CheckBox brandOthers;
    @FXML private Slider priceSlider;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;

    private IMiniShopServices services;
    private User loggedInUser;
    private List<Product> allProducts = new ArrayList<>();

    public void setServices(IMiniShopServices services) {
        this.services = services;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    @FXML
    private void initialize() {
        sortCombo.getItems().addAll("Price: Low to High", "Price: High to Low", "Name: A-Z", "Name: Z-A");
        sortCombo.setValue("Price: Low to High");
        sortCombo.setOnAction(e -> applyFilters());

        catPhones.setOnAction(e -> applyFilters());
        catLaptops.setOnAction(e -> applyFilters());
        catTablets.setOnAction(e -> applyFilters());
        catAccessories.setOnAction(e -> applyFilters());
        brandSamsung.setOnAction(e -> applyFilters());
        brandApple.setOnAction(e -> applyFilters());
        brandLenovo.setOnAction(e -> applyFilters());
        brandOthers.setOnAction(e -> applyFilters());
        priceSlider.setOnMouseReleased(e -> applyFilters());
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    public void loadProducts() {
        try {
            System.out.println("loadProducts: services=" + services);
            if (services == null) {
                System.out.println("loadProducts: services is null!");
                return;
            }
            System.out.println("loadProducts: calling getAllProducts...");
            List<Product> loaded = services.getAllProducts();
            System.out.println("loadProducts: got " + loaded.size() + " products");
            Platform.runLater(() -> {
                allProducts = loaded;
                applyFilters();
                System.out.println("loadProducts: UI updated");
            });
        } catch (Exception e) {
            System.err.println("loadProducts ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        List<Product> filtered = allProducts.stream()
                .filter(this::matchesSearch)
                .filter(this::matchesCategory)
                .filter(this::matchesBrand)
                .filter(this::matchesPrice)
                .sorted((a, b) -> {
                    String sort = sortCombo.getValue();
                    if (sort == null) return 0;
                    return switch (sort) {
                        case "Price: Low to High" -> Double.compare(a.getPrice(), b.getPrice());
                        case "Price: High to Low" -> Double.compare(b.getPrice(), a.getPrice());
                        case "Name: A-Z" -> a.getName().compareToIgnoreCase(b.getName());
                        case "Name: Z-A" -> b.getName().compareToIgnoreCase(a.getName());
                        default -> 0;
                    };
                })
                .toList();

        productCountLabel.setText("All Products (" + filtered.size() + ")");
        displayProducts(filtered);
    }

    private boolean matchesSearch(Product p) {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) return true;
        String q = query.toLowerCase();
        return p.getName().toLowerCase().contains(q)
                || p.getCategory().toLowerCase().contains(q)
                || p.getDescription().toLowerCase().contains(q);
    }

    private boolean matchesCategory(Product p) {
        boolean any = catPhones.isSelected() || catLaptops.isSelected()
                || catTablets.isSelected() || catAccessories.isSelected();
        if (!any) return true;
        String cat = p.getCategory().toLowerCase();
        if (catPhones.isSelected() && cat.contains("phone")) return true;
        if (catLaptops.isSelected() && cat.contains("laptop")) return true;
        if (catTablets.isSelected() && cat.contains("tablet")) return true;
        if (catAccessories.isSelected() && cat.contains("accessor")) return true;
        return false;
    }

    private boolean matchesBrand(Product p) {
        boolean any = brandSamsung.isSelected() || brandApple.isSelected()
                || brandLenovo.isSelected() || brandOthers.isSelected();
        if (!any) return true;
        String desc = p.getDescription().toLowerCase();
        String name = p.getName().toLowerCase();
        if (brandSamsung.isSelected() && (desc.contains("samsung") || name.contains("samsung"))) return true;
        if (brandApple.isSelected() && (desc.contains("apple") || name.contains("iphone") || name.contains("apple"))) return true;
        if (brandLenovo.isSelected() && (desc.contains("lenovo") || name.contains("lenovo"))) return true;
        if (brandOthers.isSelected()) {
            boolean known = desc.contains("samsung") || name.contains("samsung")
                    || desc.contains("apple") || name.contains("iphone") || name.contains("apple")
                    || desc.contains("lenovo") || name.contains("lenovo");
            if (!known) return true;
        }
        return false;
    }

    private boolean matchesPrice(Product p) {
        try {
            double min = Double.parseDouble(minPriceField.getText());
            double max = Double.parseDouble(maxPriceField.getText());
            return p.getPrice() >= min && p.getPrice() <= max;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private void displayProducts(List<Product> products) {
        productsPane.getChildren().clear();
        for (Product product : products) {
            productsPane.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(250);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(180, 180);
        imageContainer.setMinSize(180, 180);
        imageContainer.setMaxSize(180, 180);
        imageContainer.getStyleClass().add("image-container");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(170);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            String path = "file:D:/iss-project/MiniShop/MiniShopClient/src/main/resources/images/" + product.getPhotoName();
            Image img = new Image(path);
            if (!img.isError()) {
                imageView.setImage(img);
            }
        } catch (Exception e) {

        }

        imageContainer.getChildren().add(imageView);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMinHeight(40);

        Label brandLabel = new Label("Brand: " + product.getCategory());
        brandLabel.getStyleClass().add("product-brand");

        Label availLabel;
        if (product.getNoItems() > 0) {
            availLabel = new Label("Availability: In Stock (" + product.getNoItems() + " units)");
            availLabel.getStyleClass().add("in-stock");
        } else {
            availLabel = new Label("Availability: Out of Stock");
            availLabel.getStyleClass().add("out-of-stock");
        }

        Label priceLabel = new Label(String.format("Price: %.0f RON", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        Button actionBtn;
        if (loggedInUser == null) {
            actionBtn = new Button("Login to Buy");
            actionBtn.getStyleClass().add("btn-login-to-buy");
        } else if (product.getNoItems() > 0) {
            actionBtn = new Button("Add to Cart");
            actionBtn.getStyleClass().add("btn-add-cart");
        } else {
            actionBtn = new Button("Out of Stock");
            actionBtn.getStyleClass().add("btn-out-of-stock");
            actionBtn.setDisable(true);
        }
        actionBtn.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(imageContainer, nameLabel, brandLabel, availLabel, priceLabel, actionBtn);
        return card;
    }
}