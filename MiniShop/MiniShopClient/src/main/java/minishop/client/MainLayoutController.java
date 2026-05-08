package minishop.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import minishop.model.Product;
import minishop.model.User;
import minishop.networking.jsonprotocol.MiniShopServerJsonProxy;
import minishop.services.IMiniShopObserver;
import minishop.services.IMiniShopServices;

public class MainLayoutController implements IMiniShopObserver {

    @FXML private HBox navbarRight;
    @FXML private Label navbarBrand;
    @FXML private Button loginNavBtn;
    @FXML private Button logoutNavBtn;
    @FXML private Label userNameLabel;
    @FXML private Label homeLink;
    @FXML private StackPane contentArea;

    private Stage stage;
    private String serverHost;
    private int serverPort;
    private IMiniShopServices services;
    private User loggedInUser;
    private ProductsController productsController;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            try {
                if (loggedInUser != null && services != null) {
                    services.logout(loggedInUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setServerConfig(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    @FXML
    private void initialize() {
        updateNavbar();
        if (homeLink != null) {
            homeLink.setOnMouseClicked(e -> showProducts());
        }
    }

    private void updateNavbar() {
        if (loggedInUser != null) {
            if (loginNavBtn != null) { loginNavBtn.setVisible(false); loginNavBtn.setManaged(false); }
            if (logoutNavBtn != null) { logoutNavBtn.setVisible(true); logoutNavBtn.setManaged(true); }
            if (userNameLabel != null) {
                userNameLabel.setVisible(true);
                userNameLabel.setManaged(true);
                userNameLabel.setText(loggedInUser.getName());
            }
        } else {
            if (loginNavBtn != null) { loginNavBtn.setVisible(true); loginNavBtn.setManaged(true); }
            if (logoutNavBtn != null) { logoutNavBtn.setVisible(false); logoutNavBtn.setManaged(false); }
            if (userNameLabel != null) { userNameLabel.setVisible(false); userNameLabel.setManaged(false); }
        }
    }

    public void showProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/products.fxml"));
            Parent productsView = loader.load();
            productsController = loader.getController();
            productsController.setLoggedInUser(loggedInUser);

            if (services == null) {
                services = createProxy();
            }
            productsController.setServices(services);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(productsView);

            new Thread(() -> {
                try {
                    productsController.loadProducts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginView = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainLayout(this);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(loginView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginSuccess(User user, IMiniShopServices services) {
        this.loggedInUser = user;
        this.services = services;
        updateNavbar();
        showProducts();
    }

    public IMiniShopServices createProxy() {
        return new MiniShopServerJsonProxy(serverHost, serverPort);
    }

    @FXML
    private void handleLogout() {
        try {
            if (loggedInUser != null && services != null) {
                services.logout(loggedInUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loggedInUser = null;
        services = null;
        updateNavbar();
        showProducts();
    }

    @Override
    public void productUpdated(Product product) {
        Platform.runLater(() -> {
            if (productsController != null) {
                new Thread(productsController::loadProducts).start();
            }
        });
    }
}