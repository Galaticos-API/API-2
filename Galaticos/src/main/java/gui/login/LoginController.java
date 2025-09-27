package gui.login;

import dao.UsuarioDAO;
import factory.ConnectionFactory;
import gui.MainController;
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
import util.SceneManager;
import util.Session;
import util.StageManager;

import java.sql.Connection;
import java.util.List;

public class LoginController {

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
        Connection conn = ConnectionFactory.getConnection();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> listaUsuarios = usuarioDAO.lerTodos();

        for (Usuario usuario : listaUsuarios) {
            if (emailUsuario.getText().trim().equals(usuario.getEmail()) && senhaUsuario.getText().trim().equals(usuario.getSenha())) {
                //String proximaTela = pegarTela(usuario.getTipo_usuario());
                Session.setUsuarioAtual(usuario);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + "MainGUI" + ".fxml"));
                Parent root = loader.load();
                //passarUsuario(usuario.getTipo_usuario(), loader);
                MainController mainController = loader.getController();
                mainController.setUsuario(Session.getUsuarioAtual());

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

    // Utilidade
    String pegarTela(String funcao) {
        switch (funcao) {
            case "RH":
                return "MainGUI";
            case "Colaborador":
                return "ColaboradorGUI";
            case "Gestor de Area":
                return "GestorAreaGUI";
            case "Gestor Geral":
                return "GestorGeralGUI";
        }
        return "telaColaboradorController";
    }

    void passarUsuario(String funcao, FXMLLoader loader) throws Exception {
        switch (funcao) {
            case "RH":
                MainController mainController = loader.getController();
                mainController.setUsuario(Session.getUsuarioAtual());
                break;
            case "Colaborador":
                MainController mainController2 = loader.getController();
                mainController2.setUsuario(Session.getUsuarioAtual());
                break;
            case "Gestor de Area":

            case "Gestor Geral":

        }
    }
}