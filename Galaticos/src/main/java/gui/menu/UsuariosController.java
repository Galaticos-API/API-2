package gui.menu;

import dao.SetorDAO; // <<< 1. IMPORTAR SetorDAO
import dao.UsuarioDAO;
import factory.ConnectionFactory;
import gui.modal.CadastroUsuarioModalController;
import gui.modal.EditarUsuarioModalController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty; // <<< 2. IMPORTAR SimpleStringProperty
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Setor; // <<< 3. IMPORTAR Setor
import modelo.Usuario;
import util.Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap; // <<< 4. IMPORTAR HashMap
import java.util.List;
import java.util.Map; // <<< 5. IMPORTAR Map
import java.util.Optional;

public class UsuariosController {

    private Usuario usuarioLogado; // Renamed for clarity

    @FXML
    private TableView<Usuario> tabelaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colTipo;
    @FXML
    private TableColumn<Usuario, String> colSetor;
    @FXML
    private TableColumn<Usuario, String> colStatus;
    @FXML
    private Button btnAddUsuario;

    // --- DAOs and Cache Map ---
    private UsuarioDAO usuarioDAO;
    private SetorDAO setorDAO; // <<< 6. DECLARAR SetorDAO
    private Map<Integer, String> mapaNomesSetores; // <<< 7. DECLARAR o Mapa

    // Renamed setUsuario to avoid confusion with initialize logic
    public void setUsuario(Usuario usuario) throws SQLException {
        this.usuarioLogado = usuario;
        // Load data when user is set, initialize only sets up columns
        atualizarUsuarios();
    }

    @FXML
    public void initialize() { // Removed SQLException, handle exceptions internally
        // Initialize DAOs and Map
        this.usuarioDAO = new UsuarioDAO();
        this.setorDAO = new SetorDAO(); // <<< 8. INSTANCIAR SetorDAO
        this.mapaNomesSetores = new HashMap<>(); // <<< 9. INSTANCIAR o Mapa

        // Load the sector names map ONCE
        carregarMapaDeSetores();

        // Configure table columns
        configurarColunas();

        // Setup button action and context menu
        btnAddUsuario.setOnAction(event -> handleAbrirModalCadastro());
        configurarContextMenuTabela();

        System.out.println("initialize UsuariosController");
        // Initial data load moved to setUsuarioLogado
    }

    /**
     * Loads sector names into the map for quick lookup.
     */
    private void carregarMapaDeSetores() {
        try {
            List<Setor> setores = setorDAO.listarTodos();
            for (Setor s : setores) {
                mapaNomesSetores.put(Integer.parseInt(s.getId()), s.getNome());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro Crítico", "Não foi possível carregar a lista de setores.");
        }
    }

    /**
     * Configures the CellValueFactories for the table columns.
     */
    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colSetor.setCellValueFactory(cellData -> {
            Usuario usuarioDaLinha = cellData.getValue();
            String setorId = usuarioDaLinha.getSetor().getId();
            String nomeSetor = mapaNomesSetores.getOrDefault(Integer.parseInt(setorId), "N/A"); // Default to "N/A" if not found or ID is 0/invalid
            return new SimpleStringProperty(nomeSetor);
        });
    }


    /**
     * Configures the right-click context menu for the table rows.
     */
    private void configurarContextMenuTabela() { // <<< 13. NOVO MÉTODO (extracted logic)
        tabelaUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem apagarUsuario = new MenuItem("Apagar Usuário");
            MenuItem editarUsuario = new MenuItem("Editar Usuário");
            contextMenu.getItems().addAll(apagarUsuario, editarUsuario);

            apagarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                if (selecionado != null) {
                    confirmarEApagarUsuario(selecionado); // Call helper method
                }
            });

            editarUsuario.setOnAction(event -> {
                Usuario selecionado = row.getItem();
                if (selecionado != null) {
                    handleAbrirModalEditar(selecionado);
                }
            });

            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
            return row;
        });
    }

    @FXML
    public void atualizarUsuarios() {
        try {
            List<Usuario> listaUsuarios = usuarioDAO.lerTodos();
            tabelaUsuarios.setItems(FXCollections.observableArrayList(listaUsuarios));
        } catch (RuntimeException | SQLException e) { // Catch potential exceptions
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar a lista de usuários.");
        }
    }


    /**
     * Handles opening the modal for adding a new user.
     */
    @FXML
    private void handleAbrirModalCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroUsuarioModal.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar Novo Usuário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAddUsuario.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            CadastroUsuarioModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (controller.isSalvo()) {
                atualizarUsuarios();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de cadastro.");
        }
    }

    /**
     * Handles opening the modal for editing an existing user.
     */
    private void handleAbrirModalEditar(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarUsuarioModal.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Usuário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAddUsuario.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            EditarUsuarioModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUsuario(usuario); // Pass the user to edit
            dialogStage.showAndWait();
            if (controller.isSalvo()) {
                atualizarUsuarios();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de edição.");
        }
    }

    /**
     * Shows confirmation and deletes the user if confirmed.
     */
    private void confirmarEApagarUsuario(Usuario selecionado) { // <<< 14. NOVO MÉTODO (extracted logic)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Exclusão");
        alert.setHeaderText("Apagar usuário permanentemente?");
        alert.setContentText("Tem certeza que deseja apagar '" + selecionado.getNome() + "'?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean sucesso = usuarioDAO.deletar(selecionado.getId());
                if (sucesso) {
                    Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário apagado com sucesso!");
                    atualizarUsuarios();
                } else {
                    Util.mostrarAlerta(Alert.AlertType.WARNING, "Falha", "Não foi possível apagar o usuário.");
                }
            } catch (SQLException | RuntimeException e) {
                e.printStackTrace();
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro na Exclusão", e.getMessage());
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}