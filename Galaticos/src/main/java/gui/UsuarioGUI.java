package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UsuarioGUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Busca o arquivo a partir da raiz da pasta "resources"
        Parent root = FXMLLoader.load(getClass().getResource("/gui/UsuarioGUI.fxml"));
        stage.setTitle("Cadastro Usuario");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
