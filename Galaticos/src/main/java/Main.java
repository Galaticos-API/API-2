import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.StageManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Busca o arquivo a partir da raiz da pasta "resources"
        System.out.println("Rodando MAIN");
        Parent listaRoot = FXMLLoader.load(getClass().getResource("/gui/ListaUsuarios.fxml"));
        stage.setTitle("Lista de Usuários");

        stage.setTitle("Cadastro Usuario");
        stage.setScene(new Scene(listaRoot, 800, 400));
        stage.show();
        StageManager.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
