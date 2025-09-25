package gui.menu;

import dao.UsuarioDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Usuario;

import java.beans.EventHandler;
import java.util.EventListener;
import java.util.List;
import java.util.Optional;

public class UsuariosGUIController {

    private Usuario usuarioLogado;

    @FXML
    private TableView<Usuario> tabelaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colTipo;
    @FXML
    private TableColumn<Usuario, Integer> colStatus;

    @FXML
    private Button btnAddUsuario;


    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        atualizarUsuarios();
    }

    @FXML
    public void initialize() {
        if (usuarioLogado != null) {
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            atualizarUsuarios();
        }
    }

    @FXML
    void clickAddUsuario(EventHandler event){
        
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
                //System.out.println(selecionado);
                if (selecionado != null) {
                    //System.out.println("Apagar usuário: " + selecionado.getNome());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmação de Exclusão");
                    alert.setHeaderText("Você está prestes a apagar um usuário permanentemente.");
                    alert.setContentText("Tem certeza que deseja apagar o usuário '" + selecionado.getNome() + "'?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        System.out.println("Usuário confirmou a exclusão de: " + selecionado.getNome());
                        usuarioDAO.deletar(selecionado.getId());
                        atualizarUsuarios(); // Atualiza a tabela
                    } else {
                        System.out.println("Exclusão cancelada pelo usuário.");
                    }
                }
            });

            editarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                if (selecionado != null) {
                    //System.out.println("Editar usuário: " + selecionado.getNome());
                }
            });

            // Só mostra o menu se a linha não estiver vazia
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });

    }
}