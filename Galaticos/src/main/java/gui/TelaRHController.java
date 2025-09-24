package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        // Adiciona o ContextMenu a cada linha da tabela
    }

    @FXML
    public void atualizarUsuarios() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> listaUsuarios = usuarioDAO.lerTodos();
        tabelaUsuarios.setItems(FXCollections.observableArrayList(listaUsuarios));



        tabelaUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem apagarUsuario = new MenuItem("Apagar Usuário");
            MenuItem editarUsuario = new MenuItem("Editar Usuário");
            contextMenu.getItems().addAll(apagarUsuario, editarUsuario);

            apagarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                System.out.println(selecionado);
                if (selecionado != null) {
                    System.out.println("Apagar usuário: " + selecionado.getNome());
                    usuarioDAO.deletar(selecionado.getId());
                    atualizarUsuarios();
                }
            });

            editarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                if (selecionado != null) {
                    System.out.println("Editar usuário: " + selecionado.getNome());
                }
            });

            // Só mostra o menu se a linha não estiver vazia
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });

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
