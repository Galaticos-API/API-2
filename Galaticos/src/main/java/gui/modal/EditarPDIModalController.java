package gui.modal;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- IMPORT ADICIONADO
import javafx.fxml.Initializable;
import javafx.scene.Parent; // <-- IMPORT ADICIONADO
import javafx.scene.Scene; // <-- IMPORT ADICIONADO
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality; // <-- IMPORT ADICIONADO
import javafx.stage.Stage;
import javafx.stage.Stage;
import modelo.Documento;
import modelo.Objetivo;
import modelo.PDI;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import dao.DocumentoDAO;

import javafx.util.Callback;
import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;

import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class EditarPDIModalController implements Initializable {

    // Elementos da Barra de Progresso
    // ... (outros @FXML)
    @FXML
    private ProgressBar progressBarGeral;
    @FXML
    private Text textPontuacaoGeral;
    @FXML
    private TextField usuarioNomeField; // Renomeado
    @FXML
    private TextField usuarioCargoField; // Renomeado
    @FXML
    private ComboBox<String> statusPdiComboBox;
    @FXML
    private DatePicker dataCriacaoPicker;
    @FXML
    private DatePicker dataFechamentoPicker;
    @FXML
    private TableView<Objetivo> objetivoTable;

    //Tabela Objetivo

    @FXML
    private TableColumn<Objetivo, Integer> descricaoColumn;

    @FXML
    private TableColumn<Objetivo, Date> prazoColumn;

    @FXML
    private TableColumn<Objetivo, Integer> pesoColumn;

    @FXML
    private TableColumn<Objetivo, Float> pontuacaoColumn;

    @FXML
    private TableColumn<Objetivo, String> statusColumn;

    @FXML
    private ObservableList<Objetivo> objetivoObservableList;


    // Aba 4: Documentos
    @FXML
    private TableView<Documento> documentoTable;

    @FXML
    private TableColumn<Documento, String> docNomeArquivoCol;
    @FXML
    private TableColumn<Documento, String> docTipoCol;
    @FXML
    private TableColumn<Documento, Date> docDataUploadCol;
    @FXML
    private TableColumn<Documento, Void> docAcoesCol;

    private PDI pdiAtual;
    private PdiDAO pdiDAO = new PdiDAO();

    private Stage dialogStage;
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusPdiComboBox.getItems().addAll("Em Andamento", "Concluído", "Arquivado");

        configurarColunasTabela();
        carregarTodosOsPDIs();

        configurarColunasDocumentos();
        carregarDocumentos();
    }

    public void setPDI(PDI pdi) {
        if (pdi != null) {
            this.pdiAtual = pdi;
            loadPdiData(); // Carrega os dados na tela
        }
    }

    private void loadPdiData() {
        if (pdiAtual == null) return;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorId(pdiAtual.getColaboradorId());

            if (usuario != null) {
                usuarioNomeField.setText(usuario.getNome());
                usuarioCargoField.setText(usuario.getTipo_usuario());
            }

            statusPdiComboBox.setValue(pdiAtual.getStatus());

            if (pdiAtual.getDataCriacao() != null) {
                // Supondo que getDataCriacao() retorna a String '20-10-2025'
                String dataCriacaoStr = pdiAtual.getDataCriacao();
                LocalDate dataCriacao = LocalDate.parse(dataCriacaoStr, formatter);
                dataCriacaoPicker.setValue(dataCriacao);
            }

            if (pdiAtual.getDataFechamento() != null) {
                String dataFechamentoStr = pdiAtual.getDataFechamento();
                LocalDate dataFechamento = LocalDate.parse(dataFechamentoStr, formatter);
                dataFechamentoPicker.setValue(dataFechamento);
            }

            float pontuacao = pdiAtual.getPontuacaoGeral();
            progressBarGeral.setProgress(pontuacao);
            textPontuacaoGeral.setText(String.format("%.1f%% Concluído", pontuacao * 100));

            ObjetivoDAO objetivoDAO = new ObjetivoDAO();
            objetivoTable.setItems(FXCollections.observableArrayList(objetivoDAO.buscarPorPdiId(pdiAtual.getId())));

        } catch (RuntimeException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados do PDI.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSavePdiDetails() {
        String novoStatus = statusPdiComboBox.getValue();
        LocalDate novaDataCriacaoLocal = dataCriacaoPicker.getValue();
        LocalDate novaDataFechamentoLocal = dataFechamentoPicker.getValue();

        if (novoStatus == null || novoStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione o Status do PDI.");
            return;
        }
        if (novaDataCriacaoLocal == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "A Data de Início é obrigatória.");
            return;
        }
        if (novaDataFechamentoLocal == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "A Data de Fechamento é obrigatória.");
            return;
        }

        try {
            Date novaDataCriacao = Date.from(novaDataCriacaoLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date novaDataFechamento = Date.from(novaDataFechamentoLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            pdiAtual.setStatus(novoStatus);
            pdiAtual.setDataCriacao(novaDataCriacao);
            pdiAtual.setDataFechamento(novaDataFechamento);
            boolean sucesso = pdiDAO.atualizar(pdiAtual);

            if (sucesso) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Dados gerais do PDI atualizados com sucesso!");
                salvo = true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o PDI no banco de dados.");
            }

        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleCalculateGeneralScore() {
        // Lógica para calcular a pontuação ponderada de todos os objetivos
        // 1. Chama o método de cálculo: float novoScore = PdiService.calcularPontuacao(pdi.getId());
        // 2. Atualiza a tela: progressBarGeral.setProgress(novoScore);

        showAlert(Alert.AlertType.INFORMATION, "Cálculo", "Pontuação geral recalculada e atualizada.");
    }

    @FXML
    private void handleAddObjective() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroObjetivoModal.fxml"));
            Parent page = loader.load();

            CadastroObjetivoModalController controller = loader.getController();
            // <-- 3. CORRIGIDO: Usa a variável de instância pdiAtual
            controller.setPdiId(Integer.parseInt(pdiAtual.getId()));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adicionar Novo Objetivo");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(objetivoTable.getScene().getWindow());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                // <-- 3. CORRIGIDO: Usa a variável de instância pdiAtual
                objetivoTable.setItems(FXCollections.observableArrayList(new ObjetivoDAO().buscarPorPdiId(pdiAtual.getId())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveSkills() {
        // Lógica para salvar os novos níveis de avaliação (pdi_habilidade)
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Avaliações de Skills salvas com sucesso!");
    }

    @FXML
    private void handleUploadDocument() {
        if (pdiAtual == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Nenhum PDI selecionado.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Documento para Upload");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) documentoTable.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);

        if (arquivoSelecionado != null) {
            try {
                Path pastaUploads = Paths.get("documentos_pdi");
                if (!Files.exists(pastaUploads)) {
                    Files.createDirectories(pastaUploads);
                }

                String nomeUnico = System.currentTimeMillis() + "_" + arquivoSelecionado.getName();
                Path caminhoDestino = pastaUploads.resolve(nomeUnico);

                Files.copy(arquivoSelecionado.toPath(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

                Documento novoDocumento = new Documento();
                novoDocumento.setPdi_id(Integer.parseInt(pdiAtual.getId())); // Associa ao PDI atual
                novoDocumento.setNome(arquivoSelecionado.getName()); // Nome original do arquivo
                novoDocumento.setCaminhoArquivo(caminhoDestino.toString()); // Caminho onde foi salvo
                novoDocumento.setDataUpload(new Date()); // Data atual

                String nomeOriginal = arquivoSelecionado.getName();
                int lastDot = nomeOriginal.lastIndexOf('.');
                if (lastDot > 0) {
                    novoDocumento.setTipo(nomeOriginal.substring(lastDot + 1));
                } else {
                    novoDocumento.setTipo("desconhecido");
                }

                DocumentoDAO documentoDAO = new DocumentoDAO();
                documentoDAO.adicionar(novoDocumento);

                carregarDocumentos();

                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Upload do arquivo '" + arquivoSelecionado.getName() + "' realizado com sucesso!");

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Arquivo", "Não foi possível salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erro Interno", "ID do PDI inválido.");
                e.printStackTrace();
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Não foi possível salvar o documento no banco: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Seleção de arquivo cancelada.");
        }
    }

    private void carregarDocumentos() {
        if (pdiAtual == null) return;

        try {
            DocumentoDAO documentoDAO = new DocumentoDAO();
            List<Documento> documentos = documentoDAO.buscarPorPdiId(pdiAtual.getId());
            documentoTable.setItems(FXCollections.observableArrayList(documentos));
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os documentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarColunasDocumentos() {
        docNomeArquivoCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        docTipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        docDataUploadCol.setCellValueFactory(new PropertyValueFactory<>("dataUpload"));

        docDataUploadCol.setCellFactory(column -> {
            TableCell<Documento, Date> cell = new TableCell<Documento, Date>() {
                private java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(format.format(item));
                    }
                }
            };
            return cell;
        });

        Callback<TableColumn<Documento, Void>, TableCell<Documento, Void>> cellFactory = new Callback<TableColumn<Documento, Void>, TableCell<Documento, Void>>() {
            @Override
            public TableCell<Documento, Void> call(final TableColumn<Documento, Void> param) {
                final TableCell<Documento, Void> cell = new TableCell<Documento, Void>() {

                    private final Button btnExcluir = new Button("Excluir");

                    {
                        btnExcluir.setOnAction((ActionEvent event) -> {
                            Documento doc = getTableView().getItems().get(getIndex());
                            confirmarEExcluirDocumento(doc);
                        });
                        btnExcluir.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnExcluir);
                        }
                    }
                };
                return cell;
            }
        };
        docAcoesCol.setCellFactory(cellFactory);

    }

    private void confirmarEExcluirDocumento(Documento doc) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir Documento: " + doc.getNome());
        alert.setContentText("Você tem certeza que deseja excluir este documento? Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DocumentoDAO dao = new DocumentoDAO();
                dao.remover(doc.getId());

                Files.deleteIfExists(Paths.get(doc.getCaminhoArquivo()));

                documentoTable.getItems().remove(doc);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Documento excluído com sucesso.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao excluir o arquivo físico: " + e.getMessage());
                e.printStackTrace();
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao excluir o documento do banco: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void configurarColunasTabela() {
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        prazoColumn.setCellValueFactory(new PropertyValueFactory<>("prazo"));
        pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
        pontuacaoColumn.setCellValueFactory(new PropertyValueFactory<>("pontuacao"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void carregarTodosOsPDIs() {
        List<Objetivo> objetivos = ObjetivoDAO.lerTodos();
        objetivoObservableList = FXCollections.observableArrayList(objetivos);
        objetivoTable.setItems(objetivoObservableList);
    }
}