package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    public static void mudarCena(String novaCena, String titulo) {

        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(("/gui/" + novaCena + ".fxml")));
            Stage stage = StageManager.getStage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
