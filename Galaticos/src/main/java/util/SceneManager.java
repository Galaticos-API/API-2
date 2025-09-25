package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    public static void mudarCena(String novaCena, String titulo) {

        try {
            String resourcePath = "/gui/" + novaCena + ".fxml";

            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(resourcePath)));
            Stage stage = StageManager.getStage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o arquivo FXML: " + novaCena);
        }
    }

}
