import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.StageManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //System.out.println("Rodando MAIN");
        Parent listaRoot = FXMLLoader.load(getClass().getResource("/gui/loginGUI.fxml"));

        stage.setTitle("Sistema PDI");
        stage.setScene(new Scene(listaRoot, 579, 579));
        stage.setResizable(false);
        stage.show();
        StageManager.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
