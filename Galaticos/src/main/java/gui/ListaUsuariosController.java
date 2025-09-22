package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Usuario;

public class ListaUsuariosController {

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNome;

    @FXML
    private TableColumn<Usuario, String> colEmail;

    @FXML
    private TableColumn<Usuario, String> colSenha;

    @FXML
    private TableColumn<Usuario, String> colTipo;

    @FXML
    private TableColumn<Usuario, String> colStatus;

    @FXML
    public void initialize() {
        // Configura as colunas com os atributos da classe Usuario
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colSenha.setCellValueFactory(new PropertyValueFactory<>("senha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cria uma lista de usuários fictícia
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList(
                new Usuario("Ana Silva", "ana@email.com", "1234", "ADMIN", "ATIVO"),
                new Usuario("João Souza", "joao@email.com", "abcd", "USER", "INATIVO"),
                new Usuario("Maria Oliveira", "maria@email.com", "senha", "USER", "ATIVO")
        );

        // Define os usuários na tabela
        tabelaUsuarios.setItems(usuarios);
    }
}
