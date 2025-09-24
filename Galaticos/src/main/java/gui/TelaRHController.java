package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Usuario;
import util.SceneManager;
import util.Session;

import java.util.List;

public class TelaRHController {

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNome;

    @FXML
    private TableColumn<Usuario, String> colEmail;

    @FXML
    private TableColumn<Usuario, String> colTipo;

    @FXML
    private TableColumn<Usuario, String> colStatus;

    @FXML
    private Label funcaoUsuario;
    @FXML
    private Label logadoComo;

    @FXML
    public void initialize() {
        // Configura as colunas com os atributos da classe Usuario
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Define os usuários na tabela
        atualizarUsuarios();
    }

    @FXML
    public void atualizarUsuarios() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> listaUsuarios = usuarioDAO.lerTodos();
        tabelaUsuarios.setItems(FXCollections.observableArrayList(listaUsuarios));
    }

    @FXML
    void clickDeslogar(ActionEvent event) {
        Session.setUsuarioAtual(null);
        SceneManager.mudarCena("LoginGUI", "Login");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void setUsuario(Usuario usuario) {
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
    }
}
