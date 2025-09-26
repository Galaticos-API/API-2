package gui;

import dao.ColaboradorDAO;
import dao.UsuarioDAO;
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
import util.SceneManager;

public class CadastroGUIController {

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

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = new Usuario(nome, email, senha, tipo_usuario, "Ativo");
        usuarioDAO.adicionar(usuario);
        nomeUsuario.clear();
        emailUsuario.clear();
        senhaUsuario.clear();

        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
        Colaborador colaborador = new Colaborador(nome, "", null, tipo_usuario, "", "", usuario);
        colaboradorDAO.adicionar(colaborador);

        SceneManager.mudarCena("LoginGUI", "Login");
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