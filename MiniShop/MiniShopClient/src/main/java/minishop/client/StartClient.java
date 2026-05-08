package minishop.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Properties;

public class StartClient extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            String host = "localhost";
            int port = 55555;
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("client.config")) {
                if (input != null) {
                    Properties props = new Properties();
                    props.load(input);
                    host = props.getProperty("server.host", "localhost");
                    port = Integer.parseInt(props.getProperty("server.port", "55555"));
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_layout.fxml"));
            Parent root = loader.load();

            MainLayoutController controller = loader.getController();
            controller.setServerConfig(host, port);
            controller.setStage(primaryStage);

            Scene scene = new Scene(root, 1200, 750);
            var cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("MiniShop");
            primaryStage.setScene(scene);
            primaryStage.show();

            Platform.runLater(controller::showProducts);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}