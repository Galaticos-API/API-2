package gui.menu;

import dao.PdiDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import modelo.PDI;

import java.io.IOException;
import java.net.URL;
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


    @FXML
    void handleCadastrarPdiAction(ActionEvent event) {
        try {
            String fxmlPath = "/gui/CadastroPdiGUI.fxml";

            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Erro Crítico: Não foi possível encontrar o arquivo FXML em: " + fxmlPath);
                exibirAlerta("Erro de Arquivo", "Não foi possível encontrar a tela de cadastro.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent telaCadastro = loader.load();

            AnchorPane parentContainer = (AnchorPane) rootPane.getParent();
            parentContainer.getChildren().setAll(telaCadastro);

            AnchorPane.setTopAnchor(telaCadastro, 0.0);
            AnchorPane.setBottomAnchor(telaCadastro, 0.0);
            AnchorPane.setLeftAnchor(telaCadastro, 0.0);
            AnchorPane.setRightAnchor(telaCadastro, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro de Navegação", "Não foi possível carregar a tela de cadastro de PDI.");
        }
    }

    private PdiDAO pdiDAO;

    private ObservableList<PDI> pdiObservableList;

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
                System.out.println("Opção 'Editar PDI' clicada para o PDI de ID: " + pdiSelecionado.getId());
            }
        });

        excluirItem.setOnAction(event -> {
            PDI pdiSelecionado = pdiTableView.getSelectionModel().getSelectedItem();
            if (pdiSelecionado != null) {
                confirmarEExcluirPDI(pdiSelecionado);
            }
        });

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