package gui.login;

import dao.UsuarioDAO;
import gui.MainController;
// Importe os outros controllers que você usa (ColaboradorController, etc.)
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField; // Importe o PasswordField
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Usuario;
import util.Session;
import util.StageManager;

public class LoginController {

    @FXML
    private TextField emailUsuario;
    @FXML
    private PasswordField senhaUsuario;
    @FXML
    private Button sairBtn;

    @FXML
    void clickLogin(ActionEvent event) throws Exception {
        String email = emailUsuario.getText().trim();
        String senha = senhaUsuario.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Campos Vazios", "Por favor, preencha o e-mail e a senha.");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioAutenticado = usuarioDAO.autenticar(email, senha);

        if (usuarioAutenticado != null) {
            Session.setUsuarioAtual(usuarioAutenticado);

            String fxmlFile = getTelaPorTipo(usuarioAutenticado.getTipo_usuario());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + fxmlFile));
            Parent proximoRoot = loader.load();

            passarUsuarioParaController(loader.getController(), usuarioAutenticado);

            Stage stage = StageManager.getStage();
            stage.getScene().setRoot(proximoRoot);

        } else {
            mostrarAlerta("Falha no Login", "Usuário ou senha incorretos.");
        }
    }

    private String getTelaPorTipo(String tipoUsuario) {
        switch (tipoUsuario) {
            case "RH":
                return "MainGUI.fxml";
            case "Gestor Geral":
                return "MainGUI.fxml";
            case "Gestor de Area":
                return "MainGUI.fxml";
            case "Colaborador":
                return "ColaboradorGUI.fxml";
            default:
                throw new IllegalArgumentException("Tipo de usuário desconhecido: " + tipoUsuario);
        }
    }

    private void passarUsuarioParaController(Object controller, Usuario usuario) {
        if (controller instanceof MainController) {
            ((MainController) controller).setUsuario(usuario);
        }
    }

    @FXML
    void clickMudarTelaCadastro(ActionEvent event) {
        mostrarAlerta("Info", "Funcionalidade de cadastro não implementada.");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}