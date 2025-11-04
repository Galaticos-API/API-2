package gui.login;

import dao.UsuarioDAO;
import gui.MainController;
import gui.modal.CadastroUsuarioModalController;
import gui.modal.LoginErrorModalController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Usuario;
import util.CriptografiaUtil;
import util.Session;
import util.StageManager;
import util.Util;

import java.io.IOException;

public class LoginController {

    public StackPane rootPane;
    @FXML
    private TextField emailUsuario;
    @FXML
    private PasswordField senhaUsuario;
    @FXML
    private Button sairBtn;

    @FXML
    public void initialize() {
        rootPane.setFocusTraversable(true);
        rootPane.requestFocus();

        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.HOME) {
                handleAbrirModalCadastro();
                event.consume();
            }
        });
    }

    @FXML
    void clickLogin(ActionEvent event) throws Exception {
        String email = emailUsuario.getText().trim();
        String senhaPlana = senhaUsuario.getText().trim();

        if (email.isEmpty() || senhaPlana.isEmpty()) {
            mostrarModalErro("Por favor, preencha o e-mail e a senha.");
            return;
        }

        try {
            String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuarioAutenticado = usuarioDAO.autenticar(email, senhaCriptografada);

            if (usuarioAutenticado != null) {
                // Verifica se está ativo o usuário
                if ("Ativo".equals(usuarioAutenticado.getStatus())) {

                    // Se ativo, efetua o login
                    Session.setUsuarioAtual(usuarioAutenticado);

                    String fxmlFile = "MainGUI.fxml";
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + fxmlFile));
                    Parent proximoRoot = loader.load();

                    passarUsuarioParaController(loader.getController(), usuarioAutenticado);

                    Stage stage = StageManager.getStage();
                    stage.getScene().setRoot(proximoRoot);

                } else {
                    // Se estiver inativo impede o login
                    mostrarAlerta("Falha no Login", "Este usuário está inativo. Contate o administrador.");
                }
            } else {
                mostrarModalErro("Usuário ou senha incorretos.");
            }
        } catch (Exception e) {
            Util.mostrarAlerta(javafx.scene.control.Alert.AlertType.ERROR,
                    "Erro no Login", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void passarUsuarioParaController(Object controller, Usuario usuario) {
        if (controller instanceof MainController) {
            ((MainController) controller).setUsuario(usuario);
        }
    }

    @FXML
    void clickMudarTelaCadastro(ActionEvent event) {
        mostrarModalErro("Funcionalidade de cadastro não implementada.");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Substitui o Alert padrão por um modal estilizado
     */
    private void mostrarModalErro(String mensagem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/LoginErrorModal.fxml"));
            Parent page = loader.load();

            LoginErrorModalController controller = loader.getController();
            controller.setMensagem(mensagem);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Aviso");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(rootPane.getScene().getWindow());
            dialogStage.setResizable(false);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbrirModalCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroUsuarioModal.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar Novo Usuário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootPane.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CadastroUsuarioModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                System.out.println("Modal fechado com sucesso");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
