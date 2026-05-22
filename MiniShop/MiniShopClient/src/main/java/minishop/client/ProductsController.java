package minishop.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import minishop.model.CartItem;
import minishop.model.Product;
import minishop.model.User;
import minishop.services.IMiniShopServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private List<CartItem> cart = new ArrayList<>();
    private Runnable onCartChanged;

    public void setServices(IMiniShopServices services) {
        this.services = services;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public List<CartItem> getCart() { return cart; }

    public void setOnCartChanged(Runnable callback) {
        this.onCartChanged = callback;
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
        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxPriceField.setText(String.valueOf(newVal.intValue()));
            applyFilters();
        });

        minPriceField.setOnAction(e -> applyFilters());
        maxPriceField.setOnAction(e -> {
            priceSlider.setValue(Double.parseDouble(maxPriceField.getText()));
            applyFilters();
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            new Thread(() -> {
                try {
                    List<Product> results;
                    if (newVal == null || newVal.trim().isEmpty()) {
                        results = services.getAllProducts();
                    } else {
                        results = services.searchProducts(newVal.trim());
                    }
                    Platform.runLater(() -> {
                        allProducts = results;
                        applyFilters();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    public void loadProducts() {
        try {
            List<Product> loaded = (services != null) ? services.getAllProducts() : new ArrayList<>();
            Platform.runLater(() -> {
                allProducts = loaded;
                applyFilters();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        List<Product> filtered = allProducts.stream()
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
            String minText = minPriceField.getText().trim();
            String maxText = maxPriceField.getText().trim();
            if (minText.isEmpty() || maxText.isEmpty()) return true;
            double min = Double.parseDouble(minText);
            double max = Double.parseDouble(maxText);
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

    // ========== ADD TO CART ==========

    private void addToCart(Product product) {
        Optional<CartItem> existing = cart.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            if (item.getQuantity() < product.getNoItems()) {
                item.setQuantity(item.getQuantity() + 1);
            } else {
                showAlert("This product is out of stock");
                return;
            }
        } else {
            cart.add(new CartItem(product, 1));
        }

        if (onCartChanged != null) onCartChanged.run();
        showAlert("The product was added to cart");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MiniShop");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void showCartDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Shopping Cart");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");

        buildCartContent(scrollPane, dialog);

        Scene scene = new Scene(scrollPane, 650, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void buildCartContent(ScrollPane scrollPane, Stage dialog) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Shopping Cart");
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        VBox itemsBox = new VBox(10);

        if (cart.isEmpty()) {
            itemsBox.getChildren().add(new Label("Your cart is empty."));
        } else {
            HBox header = new HBox(15);
            header.setAlignment(Pos.CENTER_LEFT);
            header.getChildren().addAll(
                    createHeaderLabel("Product Name", 200),
                    createHeaderLabel("Unit Price", 100),
                    createHeaderLabel("Quantity", 150),
                    createHeaderLabel("Subtotal", 100)
            );
            header.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;");
            itemsBox.getChildren().add(header);

            for (CartItem item : new ArrayList<>(cart)) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(5, 0, 5, 0));

                Label nameLabel = new Label(item.getProduct().getName());
                nameLabel.setPrefWidth(200);

                Label priceLabel = new Label(String.format("%.0f RON", item.getProduct().getPrice()));
                priceLabel.setPrefWidth(100);

                Button minusBtn = new Button("-");
                minusBtn.setPrefSize(30, 30);
                minusBtn.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-cursor: hand;");

                Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
                qtyLabel.setPrefWidth(30);
                qtyLabel.setAlignment(Pos.CENTER);
                qtyLabel.setStyle("-fx-font-size: 14;");

                Button plusBtn = new Button("+");
                plusBtn.setPrefSize(30, 30);
                plusBtn.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-cursor: hand;");

                minusBtn.setOnAction(e -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                    } else {
                        cart.remove(item);
                    }
                    if (onCartChanged != null) onCartChanged.run();
                    buildCartContent(scrollPane, dialog); // refresh in-place
                });

                plusBtn.setOnAction(e -> {
                    if (item.getQuantity() < item.getProduct().getNoItems()) {
                        item.setQuantity(item.getQuantity() + 1);
                        if (onCartChanged != null) onCartChanged.run();
                        buildCartContent(scrollPane, dialog); // refresh in-place
                    }
                });

                HBox qtyBox = new HBox(5, minusBtn, qtyLabel, plusBtn);
                qtyBox.setAlignment(Pos.CENTER);
                qtyBox.setPrefWidth(150);

                Label subLabel = new Label(String.format("%.0f RON", item.getSubtotal()));
                subLabel.setPrefWidth(100);
                subLabel.setStyle("-fx-font-weight: bold;");

                row.getChildren().addAll(nameLabel, priceLabel, qtyBox, subLabel);
                itemsBox.getChildren().add(row);
            }
        }

        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        Label subtotalLabel = new Label(String.format("Subtotal: %.0f RON", total));
        subtotalLabel.setStyle("-fx-font-size: 14;");
        Label totalLabel = new Label(String.format("Total Payment: %.0f RON", total));
        totalLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        VBox totalsBox = new VBox(5, subtotalLabel, totalLabel);
        totalsBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");

        Button completeBtn = new Button("Complete Order");
        completeBtn.setStyle("-fx-background-color: #5a8f5a; -fx-text-fill: white; -fx-font-size: 14; " +
                "-fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        completeBtn.setOnAction(e -> {
            if (cart.isEmpty()) return;
            completeBtn.setDisable(true);
            completeBtn.setText("Processing...");

            new Thread(() -> {
                try {
                    Map<String, Integer> quantities = new java.util.HashMap<>();
                    for (CartItem item : cart) {
                        quantities.put(item.getProduct().getId(), item.getQuantity());
                    }
                    services.placeOrder(loggedInUser.getId(), quantities);
                    Platform.runLater(() -> {
                        cart.clear();
                        if (onCartChanged != null) onCartChanged.run();
                        dialog.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("MiniShop");
                        alert.setHeaderText(null);
                        alert.setContentText("Order placed successfully! Thank you!");
                        alert.show();
                        new Thread(this::loadProducts).start();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        completeBtn.setDisable(false);
                        completeBtn.setText("Complete Order");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error: " + ex.getMessage());
                        alert.show();
                    });
                }
            }).start();
        });

        HBox btnBox = new HBox(completeBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(title, itemsBox, totalsBox, btnBox);
        scrollPane.setContent(root);
    }

    private HBox createCartRow(CartItem item, VBox itemsBox, Stage dialog) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));

        Label nameLabel = new Label(item.getProduct().getName());
        nameLabel.setPrefWidth(200);

        Label priceLabel = new Label(String.format("%.0f RON", item.getProduct().getPrice()));
        priceLabel.setPrefWidth(100);

        // QUANTITY CONTROLS
        Button minusBtn = new Button("-");
        minusBtn.setPrefSize(30, 30);
        minusBtn.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");

        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setPrefWidth(30);
        qtyLabel.setAlignment(Pos.CENTER);
        qtyLabel.setStyle("-fx-font-size: 14;");

        Button plusBtn = new Button("+");
        plusBtn.setPrefSize(30, 30);
        plusBtn.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");

        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cart.remove(item);
            }
            if (onCartChanged != null) onCartChanged.run();
            dialog.close();
            showCartDialog(); // refresh
        });

        plusBtn.setOnAction(e -> {
            if (item.getQuantity() < item.getProduct().getNoItems()) {
                item.setQuantity(item.getQuantity() + 1);
            } else {
                showAlert("This product is out of stock");
            }
            if (onCartChanged != null) onCartChanged.run();
            dialog.close();
            showCartDialog(); // refresh
        });

        HBox qtyBox = new HBox(5, minusBtn, qtyLabel, plusBtn);
        qtyBox.setAlignment(Pos.CENTER);
        qtyBox.setPrefWidth(150);

        Label subtotalLabel = new Label(String.format("%.0f RON", item.getSubtotal()));
        subtotalLabel.setPrefWidth(100);
        subtotalLabel.setStyle("-fx-font-weight: bold;");

        row.getChildren().addAll(nameLabel, priceLabel, qtyBox, subtotalLabel);
        return row;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        return label;
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
            // ignore
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
            actionBtn.setOnAction(e -> addToCart(product));
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