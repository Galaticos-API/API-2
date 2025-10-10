package gui.menu;

import dao.UsuarioDAO;
import factory.ConnectionFactory;
import gui.modal.CadastroUsuarioModalController;
import gui.modal.EditarUsuarioModalController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Usuario;
import util.Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuariosController {

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

    public void setUsuario(Usuario usuario) throws SQLException {
        this.usuarioLogado = usuario;
    }

    @FXML
    public void initialize() throws SQLException {
        if (usuarioLogado != null) {
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            btnAddUsuario.setOnAction(event -> handleAbrirModalCadastro());

            atualizarUsuarios();
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
            dialogStage.initOwner(btnAddUsuario.getScene().getWindow()); // Define a janela principal como "pai"
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CadastroUsuarioModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                System.out.println("Modal fechado com sucesso, atualizando a tabela...");
                atualizarUsuarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleAbrirModalEditar(Usuario usuario) {
        try {
            // 1. Carrega o arquivo FXML do modal.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarUsuarioModal.fxml"));
            Parent page = loader.load();

            // 2. Cria um novo Stage (janela) para o modal.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Usuário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAddUsuario.getScene().getWindow()); // Define a janela principal como "pai"
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. Obtém o controller do modal e passa o Stage.
            EditarUsuarioModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUsuario(usuario);

            // 4. Mostra o modal e espera até que ele seja fechado.
            dialogStage.showAndWait();

            // 5. Após o fechamento, verifica se o usuário foi salvo.
            if (controller.isSalvo()) {
                System.out.println("Modal fechado com sucesso, atualizando a tabela...");
                atualizarUsuarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void atualizarUsuarios() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();

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
                if (selecionado != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmação de Exclusão");
                    alert.setHeaderText("Você está prestes a apagar um usuário permanentemente.");
                    alert.setContentText("Tem certeza que deseja apagar o usuário '" + selecionado.getNome() + "'?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            usuarioDAO.deletar(selecionado.getId());
                            Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário apagado com sucesso!");
                            atualizarUsuarios();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (RuntimeException e) {
                            // Se o serviço lançou uma exceção, o rollback já foi feito.
                            // Apenas informe o usuário sobre o erro.
                            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro na Exclusão", e.getMessage());
                        }

                    } else {
                        System.out.println("Exclusão cancelada pelo usuário.");
                    }
                }
            });

            editarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                if (selecionado != null) {
                    handleAbrirModalEditar(selecionado);
                }
            });

            // Só mostra o menu se a linha não estiver vazia
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });

    }
}