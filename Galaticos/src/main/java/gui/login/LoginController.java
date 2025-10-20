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
import util.CriptografiaUtil; // <-- IMPORT ADICIONADO
import util.Session;
import util.StageManager;
import util.Util; // <-- IMPORT ADICIONADO

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
        String senhaPlana = senhaUsuario.getText().trim(); // <-- Variável renomeada

        if (email.isEmpty() || senhaPlana.isEmpty()) { // <-- Variável renomeada
            mostrarAlerta("Campos Vazios", "Por favor, preencha o e-mail e a senha.");
            return;
        }

        // --- ALTERAÇÃO AQUI ---
        try {
            // Criptografa a senha digitada para comparar com a do banco
            String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            // Autentica usando a senha criptografada
            Usuario usuarioAutenticado = usuarioDAO.autenticar(email, senhaCriptografada);

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
        } catch (Exception e) {
            // Captura erros (ex: falha na criptografia ou no banco)
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro no Login", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
        // --- FIM DA ALTERAÇÃO ---
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