package minishop.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import minishop.model.User;
import minishop.services.IMiniShopServices;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private MainLayoutController mainLayout;

    public void setMainLayout(MainLayoutController mainLayout) {
        this.mainLayout = mainLayout;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Completati toate campurile!");
            return;
        }

        new Thread(() -> {
            try {
                IMiniShopServices services = mainLayout.createProxy();
                User user = services.login(username, password, mainLayout);
                Platform.runLater(() -> mainLayout.onLoginSuccess(user, services));
            } catch (Exception e) {
                Platform.runLater(() -> errorLabel.setText(e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleSignUp() {
        System.out.println("Sign up clicked");
    }
}