package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Usuario;
import org.w3c.dom.Text;
import util.SceneManager;
import util.Session;
import util.StageManager;

import java.util.List;

public class LoginGUIController {

    @FXML
    private TextField emailUsuario;
    @FXML
    private TextField senhaUsuario;
    @FXML
    private ChoiceBox<String> tipoUsuario;
    @FXML
    private Button loginBtn;
    @FXML
    private Button sairBtn;

    @FXML
    public void initialize() {
        tipoUsuario.setItems(FXCollections.observableArrayList("RH", "Gestor de Área", "Gestor Geral", "Colaborador"));
        tipoUsuario.setValue("RH");
    }

    @FXML
    void clickLogin(ActionEvent event) throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> listaUsuarios = usuarioDAO.lerTodos();

        for (Usuario usuario : listaUsuarios) {
            if (emailUsuario.getText().trim().equals(usuario.getEmail()) && senhaUsuario.getText().trim().equals(usuario.getSenha())) {
                Session.setUsuarioAtual(usuario);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainGUI.fxml"));
                Parent root = loader.load();
                MainGUIController controller = loader.getController();
                controller.setUsuario(Session.getUsuarioAtual());

                Stage stage = StageManager.getStage();
                stage.setScene(new Scene(root));
                stage.show();

                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso!");
        alert.setHeaderText(null);
        alert.setContentText("Usuário ou senha incorretos.");
        alert.showAndWait();
    }

    @FXML
    void clickMudarTelaCadastro(ActionEvent event) {
        SceneManager.mudarCena("CadastroGUI", "Cadastro");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}