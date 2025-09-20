package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Usuario;
import org.w3c.dom.Text;
import util.SceneManager;

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
    void clickLogin(ActionEvent event) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> listaUsuarios = usuarioDAO.lerTodos();

        for (Usuario usuario : listaUsuarios) {
            if (emailUsuario.getText().trim().equals(usuario.getEmail()) && senhaUsuario.getText().trim().equals(usuario.getSenha())) {
                SceneManager.mudarCena("MainGUI", "Tela Principal");
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso!");
        alert.setHeaderText(null);
        alert.setContentText("Usuário não encontrado no sistema.");
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