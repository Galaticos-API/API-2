package gui.menu;

import dao.PdiDAO;
import gui.modal.CadastroPdiModalController;
import gui.modal.CadastroUsuarioModalController;
import gui.modal.SinglePDIModalController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.PDI;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListaPdiController implements Initializable {

    @FXML
    private TextField colaboradorIdTextField;

    @FXML
    private Button buscarButton;

    @FXML
    private Button limparFiltroButton;

    @FXML
    private TableView<PDI> pdiTableView;

    @FXML
    private TableColumn<PDI, Integer> idColumn;

    @FXML
    private TableColumn<PDI, Integer> colaboradorIdColumn;

    @FXML
    private TableColumn<PDI, Integer> anoColumn;

    @FXML
    private TableColumn<PDI, String> statusColumn;

    @FXML
    private TableColumn<PDI, Date> dataCriacaoColumn;

    @FXML
    private TableColumn<PDI, Float> pontuacaoColumn;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button cadastrarPdiButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ... seu código existente para inicializar a tela ...
        this.pdiDAO = new PdiDAO();
        configurarColunasTabela();
        carregarTodosOsPDIs();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editarItem = new MenuItem("Editar PDI");
        MenuItem excluirItem = new MenuItem("Apagar PDI");

        editarItem.setOnAction(event -> {
            PDI pdiSelecionado = pdiTableView.getSelectionModel().getSelectedItem();
            if (pdiSelecionado != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/SinglePDIModal.fxml"));
                    Parent page = loader.load();

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Editar informações do PDI");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(cadastrarPdiButton.getScene().getWindow()); // Define a janela principal como "pai"
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);

                    SinglePDIModalController controller = loader.getController();
                    controller.setDialogStage(dialogStage);

                    dialogStage.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        excluirItem.setOnAction(event -> {
            PDI pdiSelecionado = pdiTableView.getSelectionModel().getSelectedItem();
            if (pdiSelecionado != null) {
                confirmarEExcluirPDI(pdiSelecionado);
            }
        });

        cadastrarPdiButton.setOnAction(event -> handleAbrirModalCadastro());

        contextMenu.getItems().addAll(editarItem, excluirItem);

        pdiTableView.setRowFactory(tv -> {
            TableRow<PDI> row = new TableRow<>();

            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });
    }

    @FXML
    private void handleAbrirModalCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroPdiModal.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar novo PDI");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(cadastrarPdiButton.getScene().getWindow()); // Define a janela principal como "pai"
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CadastroPdiModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                System.out.println("Modal fechado com sucesso, atualizando a tabela...");
                carregarTodosOsPDIs();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PdiDAO pdiDAO;

    private ObservableList<PDI> pdiObservableList;

    private void confirmarEExcluirPDI(PDI pdi) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir PDI do colaborador ID: " + pdi.getColaboradorId());
        alert.setContentText("Você tem certeza que deseja excluir este PDI? Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sucesso = pdiDAO.deletar(pdi.getId());

            if (sucesso) {
                pdiObservableList.remove(pdi);
                exibirAlerta("Sucesso", "PDI excluído com sucesso.");
            } else {
                exibirAlerta("Erro", "Ocorreu um erro ao excluir o PDI do banco de dados.");
            }
        }
    }

    private void configurarColunasTabela() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        colaboradorIdColumn.setCellValueFactory(new PropertyValueFactory<>("colaboradorId"));
        anoColumn.setCellValueFactory(new PropertyValueFactory<>("ano"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dataCriacaoColumn.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        pontuacaoColumn.setCellValueFactory(new PropertyValueFactory<>("pontuacaoGeral"));
    }


    private void carregarTodosOsPDIs() {
        List<PDI> pdis = pdiDAO.lerTodos();
        pdiObservableList = FXCollections.observableArrayList(pdis);
        pdiTableView.setItems(pdiObservableList);
    }


    @FXML
    void handleBuscarAction(ActionEvent event) {
        String idTexto = colaboradorIdTextField.getText();
        if (idTexto == null || idTexto.trim().isEmpty()) {
            exibirAlerta("Erro", "O campo de ID do colaborador não pode estar vazio.");
            return;
        }

        try {
            int colaboradorId = Integer.parseInt(idTexto);
            List<PDI> pdisFiltrados = pdiDAO.buscarPorColaborador(colaboradorId);

            if (pdisFiltrados.isEmpty()) {
                exibirAlerta("Informação", "Nenhum PDI encontrado para o colaborador com ID: " + colaboradorId);
            }

            pdiObservableList = FXCollections.observableArrayList(pdisFiltrados);
            pdiTableView.setItems(pdiObservableList);

        } catch (NumberFormatException e) {
            exibirAlerta("Erro de Formato", "Por favor, insira um número de ID válido.");
        }
    }

    @FXML
    void handleLimparFiltroAction(ActionEvent event) {
        colaboradorIdTextField.clear();
        carregarTodosOsPDIs();
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}