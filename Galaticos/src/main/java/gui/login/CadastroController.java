package gui.login;

import dao.UsuarioDAO;
import factory.ConnectionFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelo.Colaborador;
import modelo.Usuario;
import services.UsuarioService;
import util.SceneManager;
import util.Util;

import java.sql.Connection;
import java.sql.SQLException;

public class CadastroController {

    // @FXML conecta as variáveis do Java com os componentes do FXML pelo fx:id
    @FXML
    private TextField emailUsuario;
    @FXML
    private TextField nomeUsuario;
    @FXML
    private TextField senhaUsuario;
    @FXML
    private ChoiceBox<String> tipoUsuario;
    @FXML
    private Button cadastrarClienteBtn;
    @FXML
    private Button sairBtn;

    @FXML
    public void initialize() {
        tipoUsuario.setItems(FXCollections.observableArrayList("RH", "Gestor de Área", "Gestor Geral", "Colaborador"));
        tipoUsuario.setValue("RH");
    }

    @FXML
    void clickCadastrar(ActionEvent event) {
        String nome = nomeUsuario.getText().trim();
        String email = emailUsuario.getText().trim();
        String senha = senhaUsuario.getText().trim();
        String tipo_usuario = tipoUsuario.getValue();


        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso!");
            alert.setHeaderText(null);
            alert.setContentText("Preencha os campos!");
            alert.showAndWait();
            return;
        }

        try {
            Usuario usuario = new Usuario(nome, email, senha, tipo_usuario, "Ativo");

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.adicionar(usuario);

            Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso!");

            nomeUsuario.clear();
            emailUsuario.clear();
            senhaUsuario.clear();
            SceneManager.mudarCena("LoginGUI", "Login");
        } catch (RuntimeException | SQLException e) {
            // O catch continua o mesmo, pois o serviço vai lançar a exceção em caso de erro.
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro no Cadastro", e.getMessage());
        }
    }


    @FXML
    void clickMudarTelaLogin(ActionEvent event) {
        SceneManager.mudarCena("LoginGUI", "Login");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}