package gui.modal;

import dao.DocumentoDAO; // Import DocumentoDAO
import dao.ObjetivoDAO;
import dao.PdiDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Keep necessary imports
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Documento;
import modelo.Objetivo;
import modelo.PDI;
import modelo.Usuario;
import util.Util; // Assuming your Util class

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditarPDIModalController implements Initializable {

    // --- FXML Components from the Correct FXML ---
    @FXML
    private ProgressBar progressBarGeral;
    @FXML
    private Text textPontuacaoGeral;
    // Tab: Dados Gerais
    @FXML
    private TextField usuarioNomeField;
    @FXML
    private TextField usuarioCargoField;
    @FXML
    private ComboBox<String> statusPdiComboBox;
    @FXML
    private DatePicker dataCriacaoPicker;
    @FXML
    private DatePicker dataFechamentoPicker;
    // Tab: Objetivos
    @FXML
    private TableView<Objetivo> objetivoTable;
    @FXML
    private TableColumn<Objetivo, String> descricaoColumn; // Changed type back to String
    @FXML
    private TableColumn<Objetivo, Date> prazoColumn;    // Keep type from Model
    @FXML
    private TableColumn<Objetivo, String> statusColumn;
    @FXML
    private TableColumn<Objetivo, Void> acoesColumn;    // Correct type for action column
    // Tab: Documentos
    @FXML
    private TableView<Documento> documentoTable;
    @FXML
    private TableColumn<Documento, String> docNomeArquivoCol;
    @FXML
    private TableColumn<Documento, String> docTipoCol;
    @FXML
    private TableColumn<Documento, java.util.Date> docDataUploadCol; // Keep type from Model
    @FXML
    private TableColumn<Documento, Void> docAcoesCol;    // Correct type for action column

    // --- DAOs and Control Variables ---
    private PDI pdiAtual;
    private Usuario usuarioDoPDI;
    private Stage dialogStage;
    private boolean salvo = false;
    private PdiDAO pdiDAO = new PdiDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DocumentoDAO documentoDAO = new DocumentoDAO(); // Instantiate DocumentoDAO

    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusPdiComboBox.getItems().addAll("Em Andamento", "Concluído", "Arquivado");
        configurarTabelaObjetivos();
        configurarTabelaDocumentos();
    }

    /**
     * Called by the previous screen to set the PDI.
     */
    public void setPDI(PDI pdi) {
        if (pdi == null) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro Interno", "PDI nulo recebido.");
            if (dialogStage != null) dialogStage.close();
            return;
        }
        this.pdiAtual = pdi;
        loadPdiData();
    }

    /**
     * Loads data into UI components.
     */
    private void loadPdiData() {
        try {
            // Load User Info
            this.usuarioDoPDI = usuarioDAO.buscarPorId(pdiAtual.getColaboradorId());
            if (usuarioDoPDI != null) {
                usuarioNomeField.setText(usuarioDoPDI.getNome());
                usuarioCargoField.setText(usuarioDoPDI.getTipo_usuario());
            } else {
                usuarioNomeField.setText("Usuário não encontrado (ID: " + pdiAtual.getColaboradorId() + ")");
                usuarioCargoField.setText("-");
            }

            // Load General PDI Info
            statusPdiComboBox.setValue(pdiAtual.getStatus());
            // Assuming PDI model returns String for dates now based on previous interactions
            if (pdiAtual.getDataCriacao() != null && !pdiAtual.getDataCriacao().isEmpty()) {
                try {
                    // Use the correct formatter based on the string format returned by getDataCriacao()
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Or "yyyy-MM-dd" if it changed
                    dataCriacaoPicker.setValue(LocalDate.parse(pdiAtual.getDataCriacao(), inputFormatter));
                } catch (Exception e) {
                    System.err.println("Error parsing dataCriacao String: " + pdiAtual.getDataCriacao());
                    dataCriacaoPicker.setValue(null); // Clear on error
                }
            } else {
                dataCriacaoPicker.setValue(null);
            }
            if (pdiAtual.getDataFechamento() != null && !pdiAtual.getDataFechamento().isEmpty()) {
                try {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Or "yyyy-MM-dd"
                    dataFechamentoPicker.setValue(LocalDate.parse(pdiAtual.getDataFechamento(), inputFormatter));
                } catch (Exception e) {
                    System.err.println("Error parsing dataFechamento String: " + pdiAtual.getDataFechamento());
                    dataFechamentoPicker.setValue(null); // Clear on error
                }
            } else {
                dataFechamentoPicker.setValue(null);
            }


            // Update Progress Bar
            float pontuacao = pdiAtual.getPontuacaoGeral();
            progressBarGeral.setProgress(pontuacao);
            textPontuacaoGeral.setText(String.format("%.1f%% Concluído", pontuacao * 100));

            // Load Tables
            carregarObjetivosNaTabela();
            carregarDocumentosNaTabela();

        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar os dados do PDI: " + e.getMessage());
        }
    }

    // --- Table Configuration ---

    private void configurarTabelaObjetivos() {
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));


        prazoColumn.setCellValueFactory(new PropertyValueFactory<>("prazo"));
        prazoColumn.setCellFactory(column -> new TableCell<Objetivo, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : formatarData(item));
            }
        });

        // Configure Ações Column
        acoesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditarObj = new Button("Editar");
            private final Button btnExcluirObj = new Button("Excluir");
            private final HBox pane = new HBox(5, btnEditarObj, btnExcluirObj);

            {
                pane.setAlignment(Pos.CENTER);
                btnEditarObj.setOnAction(event -> {
                    Objetivo objetivo = getTableView().getItems().get(getIndex());
                    handleEditarObjetivo(objetivo);
                });
                btnExcluirObj.setOnAction(event -> {
                    Objetivo objetivo = getTableView().getItems().get(getIndex());
                    handleExcluirObjetivo(objetivo);
                });
                btnEditarObj.getStyleClass().add("table-action-button-edit");
                btnExcluirObj.getStyleClass().add("table-action-button-delete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void configurarTabelaDocumentos() {
        docNomeArquivoCol.setCellValueFactory(new PropertyValueFactory<>("nome")); // Use 'nome' based on DocumentoDAO
        docTipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));    // Use 'tipo' based on DocumentoDAO
        docDataUploadCol.setCellValueFactory(new PropertyValueFactory<>("dataUpload"));

        // Format Data Upload Column
        docDataUploadCol.setCellFactory(column -> new TableCell<Documento, java.util.Date>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                // Use SimpleDateFormat for Timestamp/java.util.Date
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Adjust format if dataUpload is Timestamp
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    setText(sdf.format(item));
                }
            }
        });

        // Configure Ações Column
        docAcoesCol.setCellFactory(param -> new TableCell<>() {
            // private final Button btnDownload = new Button("⬇️"); // Download might be complex
            private final Button btnExcluirDoc = new Button("🗑️");
            private final HBox pane = new HBox(5, /*btnDownload,*/ btnExcluirDoc);

            {
                pane.setAlignment(Pos.CENTER);
                // btnDownload.setOnAction(event -> { /* Download logic */ });
                btnExcluirDoc.setOnAction(event -> {
                    Documento doc = getTableView().getItems().get(getIndex());
                    confirmarEExcluirDocumento(doc);
                });
                // btnDownload.getStyleClass().add("table-action-button-download");
                btnExcluirDoc.getStyleClass().add("table-action-button-delete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // --- Data Loading for Tables ---

    private void carregarObjetivosNaTabela() {
        if (pdiAtual != null) {
            try {
                // Assuming PDI ID is int now
                List<Objetivo> objetivos = objetivoDAO.buscarPorPdiId(pdiAtual.getId());
                objetivoTable.setItems(FXCollections.observableArrayList(objetivos));
            } catch (RuntimeException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os objetivos.");
            }
        } else {
            objetivoTable.getItems().clear();
        }
    }

    private void carregarDocumentosNaTabela() {
        if (pdiAtual != null) {
            try {
                // Assuming PDI ID is int
                List<Documento> documentos = documentoDAO.buscarPorPdiId(pdiAtual.getId());
                documentoTable.setItems(FXCollections.observableArrayList(documentos));
            } catch (RuntimeException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os documentos.");
            }
        } else {
            documentoTable.getItems().clear();
        }
    }

    // --- Action Handlers ---

    @FXML
    private void handleSavePdiDetails() {
        if (pdiAtual == null) return;

        String novoStatus = statusPdiComboBox.getValue();
        LocalDate novaDataCriacaoLocal = dataCriacaoPicker.getValue();
        LocalDate novaDataFechamentoLocal = dataFechamentoPicker.getValue();

        // Basic Validations
        if (novoStatus == null || novoStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione o Status.");
            return;
        }
        if (novaDataCriacaoLocal == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Data Início obrigatória.");
            return;
        }
        // Fechamento can be null

        try {
            Date dataCriacao = Date.valueOf(novaDataCriacaoLocal);
            Date dataFechamento = Date.valueOf(novaDataFechamentoLocal);

            pdiAtual.setStatus(novoStatus);
            pdiAtual.setDataCriacao(dataCriacao);
            pdiAtual.setDataFechamento(dataFechamento);

            // Persist
            boolean sucesso = pdiDAO.atualizar(pdiAtual);

            if (sucesso) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Dados gerais atualizados!");
                salvo = true;
                // Update progress bar?
                loadPdiData(); // Reload to show potential changes reflected by DB triggers etc.
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o PDI.");
            }

        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Data", "Formato de data inválido ao salvar.");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddObjective() {
        if (pdiAtual == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroObjetivoModal.fxml"));
            Parent page = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Adicionar Novo Objetivo");
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(objetivoTable.getScene().getWindow());
            Scene scene = new Scene(page);
            modalStage.setScene(scene);

            CadastroObjetivoModalController controller = loader.getController();
            controller.setDialogStage(modalStage);
            controller.setPdiId(pdiAtual.getId()); // Pass PDI ID (assuming it's int)

            modalStage.showAndWait();

            if (controller.isSalvo()) {
                carregarObjetivosNaTabela(); // Refresh objective table
                this.salvo = true; // Marca que o PDI teve mudanças
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de cadastro de objetivo.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro Interno", "ID do PDI inválido ao adicionar objetivo.");
        }
    }

    private void handleEditarObjetivo(Objetivo objetivo) {
        if (objetivo == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarObjetivoModal.fxml"));
            Parent page = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Objetivo");
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(objetivoTable.getScene().getWindow());
            Scene scene = new Scene(page);
            modalStage.setScene(scene);

            // Pega o controller do NOVO modal
            EditarObjetivoModalController controller = loader.getController();
            controller.setDialogStage(modalStage);
            controller.setObjetivo(objetivo); // Passa o objetivo selecionado para o modal

            // Mostra o modal e espera
            modalStage.showAndWait();

            // Se o usuário salvou, atualiza a tabela
            if (controller.isSalvo()) {
                carregarObjetivosNaTabela(); // Atualiza a tabela de objetivos
                this.salvo = true; // Marca que o PDI teve mudanças
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de edição de objetivo.");
        }
    }


    private void handleExcluirObjetivo(Objetivo objetivo) {
        if (objetivo == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setHeaderText("Excluir Objetivo?");
        confirm.setContentText("Tem certeza que deseja excluir o objetivo:\n'" + objetivo.getDescricao() + "'?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean sucesso = objetivoDAO.remover(objetivo.getId());
                if (sucesso) {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Objetivo excluído.");
                    carregarObjetivosNaTabela();
                    this.salvo = true; // Marca que o PDI teve mudanças
                } else {
                    showAlert(Alert.AlertType.WARNING, "Falha", "Não foi possível excluir o objetivo do banco.");
                }
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir o objetivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUploadDocument() {
        if (pdiAtual == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "PDI não carregado.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Documento");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );
        Stage stage = (Stage) documentoTable.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);

        if (arquivoSelecionado != null) {
            try {
                Path pastaUploads = Paths.get("documentos_pdi", String.valueOf(pdiAtual.getId())); // Subpasta por PDI ID
                Files.createDirectories(pastaUploads); // Cria a pasta se não existir

                String nomeOriginal = arquivoSelecionado.getName();
                String nomeUnico = System.currentTimeMillis() + "_" + nomeOriginal;
                Path caminhoDestino = pastaUploads.resolve(nomeUnico);

                Files.copy(arquivoSelecionado.toPath(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

                Documento novoDocumento = new Documento();
                novoDocumento.setPdi_id(Integer.parseInt(pdiAtual.getId()));
                novoDocumento.setNome(nomeOriginal);
                novoDocumento.setCaminhoArquivo(caminhoDestino.toString().replace("\\", "/")); // Store relative path with forward slashes
                // novoDocumento.setDataUpload(new Date()); // Handled by DB default

                String tipo = "desconhecido";
                int lastDot = nomeOriginal.lastIndexOf('.');
                if (lastDot > 0) tipo = nomeOriginal.substring(lastDot + 1).toLowerCase();
                novoDocumento.setTipo(tipo);

                documentoDAO.adicionar(novoDocumento);
                carregarDocumentosNaTabela(); // Refresh table
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Upload realizado com sucesso!");

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Arquivo", "Não foi possível salvar: " + e.getMessage());
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco", "Não foi possível registrar: " + e.getMessage());
            }
        }
    }

    private void confirmarEExcluirDocumento(Documento doc) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir Documento: " + doc.getNome());
        alert.setContentText("Tem certeza? Esta ação não pode ser desfeita.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 1. Delete DB record
                boolean sucessoDB = documentoDAO.remover(doc.getId());

                if (sucessoDB) {
                    // 2. Delete physical file (only if DB deletion was successful)
                    try {
                        Files.deleteIfExists(Paths.get(doc.getCaminhoArquivo()));
                    } catch (IOException e) {
                        // Log or warn about file deletion failure, but proceed
                        System.err.println("Aviso: Falha ao excluir arquivo físico: " + doc.getCaminhoArquivo() + " - " + e.getMessage());
                    }
                    // 3. Remove from table view
                    documentoTable.getItems().remove(doc);
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Documento excluído.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Falha", "Não foi possível excluir o registro do documento do banco.");
                }

            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao excluir documento: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCloseModal() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }


    // --- Helper Methods ---

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo; // Updated by handleSavePdiDetails
    }

    private String formatarData(Date data) {
        if (data == null) return "N/A";
        try {
            LocalDate localDate;
            if (data instanceof java.sql.Date) {
                localDate = ((java.sql.Date) data).toLocalDate();
            } else { // Assume java.util.Date or Timestamp
                localDate = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            return localDate.format(FORMATADOR_DATA);
        } catch (Exception e) {
            System.err.println("Erro formatar data (" + data.getClass().getName() + "): " + e.getMessage());
            return "Inválida";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        // Use your Util class or standard Alert
        if (Util.class != null) {
            Util.mostrarAlerta(type, title, message);
        } else { /* Standard Alert code */ }
    }
}