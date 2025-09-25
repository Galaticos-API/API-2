package gui;

import dao.UsuarioDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import modelo.Usuario;
import util.SceneManager;

import java.util.List;

public class UsuariosGUIController {

    private Usuario usuarioLogado;

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        atualizarUsuarios();
    }

    @FXML
    public void initialize() {
        if (usuarioLogado != null) {
            atualizarUsuarios();
        }
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
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });

    }
}