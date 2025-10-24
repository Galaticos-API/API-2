package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import dao.UsuarioDAO; // Importar se precisar buscar o nome do usuário novamente
import gui.modal.EditarPDIModalController; // Importar modal de edição
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Documento; // Supondo que você tenha esse modelo
import modelo.Objetivo;
import modelo.PDI;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.sql.Date; // Use java.sql.Date se for o tipo do DAO
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MeuPdiController implements Initializable {

    // --- Componentes FXML ---
    @FXML
    private Label lblMensagemSemPdi;
    @FXML
    private VBox pdiContentBox; // Container principal do PDI
    @FXML
    private ProgressBar progressBarGeral;
    @FXML
    private Text textPontuacaoGeral;
    @FXML
    private TextField usuarioNomeField;
    @FXML
    private TextField usuarioCargoField;
    @FXML
    private TextField statusPdiField; // Alterado de ComboBox para TextField (somente leitura)
    @FXML
    private DatePicker dataCriacaoPicker;
    @FXML
    private DatePicker dataFechamentoPicker;
    @FXML
    private TabPane tabPanePDI;
    @FXML
    private Button btnAbrirEdicao; // Botão para abrir o modal de edição

    // Tabela Objetivos
    @FXML
    private TableView<Objetivo> objetivoTable;
    @FXML
    private TableColumn<Objetivo, String> descricaoColumn;
    @FXML
    private TableColumn<Objetivo, Date> prazoColumn; // Mantenha o tipo do seu modelo
    @FXML
    private TableColumn<Objetivo, String> statusColumn;
    @FXML
    private TableColumn<Objetivo, Void> acoesColumn;
    @FXML
    private Button btnAdicionarObjetivo;

    // Tabela Documentos (Exemplo)
    @FXML
    private TableView<Documento> documentoTable;
    @FXML
    private TableColumn<Documento, String> docNomeArquivoCol;
    @FXML
    private TableColumn<Documento, String> docTipoCol;
    @FXML
    private TableColumn<Documento, Date> docDataUploadCol; // Mantenha o tipo do seu modelo
    @FXML
    private TableColumn<Documento, Void> docAcoesCol;
    @FXML
    private Button btnUploadDocumento;

    // --- DAOs e Variáveis ---
    private Usuario usuarioLogado;
    private PDI pdiDoUsuario;
    private PdiDAO pdiDAO = new PdiDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    // private DocumentoDAO documentoDAO = new DocumentoDAO(); // Se tiver

    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configura as colunas das tabelas que não dependem dos dados
        configurarTabelaObjetivos();
        configurarTabelaDocumentos(); // Chame se tiver a tabela de documentos
    }

    /**
     * Chamado pelo MainController para iniciar a tela.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarDadosPDI();
    }

    /**
     * Busca o PDI do usuário e popula a interface.
     */
    private void carregarDadosPDI() {
        if (usuarioLogado == null) return;

        try {
            // Busca o PDI do usuário logado
            PDI pdiDoUsuario = pdiDAO.buscarPorColaborador(usuarioLogado.getId());

            if (pdiDoUsuario != null) {

                // Esconde a mensagem e mostra o conteúdo do PDI
                lblMensagemSemPdi.setVisible(false);
                lblMensagemSemPdi.setManaged(false);
                pdiContentBox.setVisible(true);
                pdiContentBox.setManaged(true);

                // Popula os campos da aba "Dados Gerais"
                usuarioNomeField.setText(usuarioLogado.getNome()); // Pega do usuário logado
                usuarioCargoField.setText(usuarioLogado.getTipo_usuario());
                statusPdiField.setText(pdiDoUsuario.getStatus());

                if (pdiDoUsuario.getDataCriacao() != null) {
                    LocalDate dataCriacao = LocalDate.parse(pdiDoUsuario.getDataCriacao());
                    dataCriacaoPicker.setValue(dataCriacao);
                }
                if (pdiDoUsuario.getDataFechamento() != null) {
                    LocalDate dataFechamento = LocalDate.parse(pdiDoUsuario.getDataFechamento());
                    dataFechamentoPicker.setValue(dataFechamento);
                }

                // Atualiza progresso
                float pontuacao = pdiDoUsuario.getPontuacaoGeral();
                progressBarGeral.setProgress(pontuacao);
                textPontuacaoGeral.setText(String.format("%.1f%% Concluído", pontuacao * 100));

                // Carrega os dados das tabelas das outras abas
                carregarObjetivosNaTabela();
                carregarDocumentosNaTabela(); // Chame se tiver a tabela

            } else {
                // Se não encontrou PDI, mostra a mensagem e esconde o resto
                this.pdiDoUsuario = null;
                lblMensagemSemPdi.setVisible(true);
                lblMensagemSemPdi.setManaged(true);
                pdiContentBox.setVisible(false);
                pdiContentBox.setManaged(false);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível carregar os dados do seu PDI.");
            // Esconde tudo em caso de erro
            lblMensagemSemPdi.setText("Erro ao carregar PDI.");
            lblMensagemSemPdi.setVisible(true);
            lblMensagemSemPdi.setManaged(true);
            pdiContentBox.setVisible(false);
            pdiContentBox.setManaged(false);
        }
    }

    /**
     * Configura as colunas da tabela de objetivos.
     */
    private void configurarTabelaObjetivos() {
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formatação da coluna Prazo
        prazoColumn.setCellValueFactory(new PropertyValueFactory<>("prazo"));
        prazoColumn.setCellFactory(column -> new TableCell<Objetivo, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : formatarDataSql(item));
            }
        });

        // Configuração da coluna de Ações (Exemplo: Botão Editar)
        acoesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditarObj = new Button("✏️");
            private final HBox pane = new HBox(btnEditarObj);

            {
                pane.setAlignment(Pos.CENTER);
                btnEditarObj.setOnAction(event -> {
                    Objetivo objetivo = getTableView().getItems().get(getIndex());
                    handleEditarObjetivo(objetivo);
                });
                // Adicione estilos se desejar
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Configura as colunas da tabela de documentos (Exemplo).
     */
    private void configurarTabelaDocumentos() {
        if (documentoTable == null) return; // Verifica se a tabela existe no FXML

        docNomeArquivoCol.setCellValueFactory(new PropertyValueFactory<>("nomeArquivo"));
        docTipoCol.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));

        // Formatação da Data de Upload
        docDataUploadCol.setCellValueFactory(new PropertyValueFactory<>("dataUpload"));
        docDataUploadCol.setCellFactory(column -> new TableCell<Documento, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : formatarDataUtil(item)); // Usa formatador diferente se for java.util.Date
            }
        });

        // Coluna de Ações para Documentos (Exemplo: Download, Excluir)
        docAcoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnDownload = new Button("⬇️");
            private final Button btnExcluirDoc = new Button("🗑️");
            private final HBox pane = new HBox(5, btnDownload, btnExcluirDoc);

            {
                pane.setAlignment(Pos.CENTER);
                btnDownload.setOnAction(event -> { /* Lógica de download */ });
                btnExcluirDoc.setOnAction(event -> { /* Lógica de exclusão */ });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /**
     * Carrega os objetivos do PDI atual na tabela.
     */
    private void carregarObjetivosNaTabela() {
        if (pdiDoUsuario != null) {
            List<Objetivo> objetivos = objetivoDAO.buscarPorPdiId(pdiDoUsuario.getId());
            objetivoTable.setItems(FXCollections.observableArrayList(objetivos));
        } else {
            objetivoTable.getItems().clear();
        }
    }

    /**
     * Carrega os documentos do PDI atual na tabela.
     */
    private void carregarDocumentosNaTabela() {
        if (documentoTable == null || pdiDoUsuario == null) return;

        // try {
        //     List<Documento> documentos = documentoDAO.buscarPorPdiId(pdiDoUsuario.getId());
        //     documentoTable.setItems(FXCollections.observableArrayList(documentos));
        // } catch (RuntimeException e) {
        //     e.printStackTrace();
        //     exibirAlerta("Erro", "Não foi possível carregar os documentos.");
        //     documentoTable.getItems().clear();
        // }

        // Simulação enquanto não há DAO
        documentoTable.getItems().clear();
        System.out.println("Lógica para carregar documentos aqui.");

    }

    /**
     * Abre o modal de edição do PDI atual.
     */
    @FXML
    private void handleAbrirEdicao() {
        if (pdiDoUsuario == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarPDIModal.fxml"));
            Parent page = loader.load();

            EditarPDIModalController controller = loader.getController();
            controller.setPDI(this.pdiDoUsuario); // Passa o PDI atual

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Meu PDI");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAbrirEdicao.getScene().getWindow()); // Usa o botão como referência
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Se o modal foi salvo, recarrega os dados nesta tela
            if (controller.isSalvo()) {
                carregarDadosPDI();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Métodos de Ação para Objetivos e Documentos (Abrir Modais) ---
    @FXML
    private void handleAdicionarObjetivo() {
        if (pdiDoUsuario == null) return;
        System.out.println("Abrir modal para adicionar objetivo ao PDI ID: " + pdiDoUsuario.getId());
        // Lógica para abrir o modal de cadastro de objetivo
        // ...
        // Após fechar o modal, chamar carregarObjetivosNaTabela()
    }

    private void handleEditarObjetivo(Objetivo objetivo) {
        System.out.println("Abrir modal para editar objetivo ID: " + objetivo.getId());
        // Lógica para abrir o modal de edição de objetivo
        // ...
        // Após fechar o modal, chamar carregarObjetivosNaTabela()
    }

    @FXML
    private void handleUploadDocumento() {
        if (pdiDoUsuario == null) return;
        System.out.println("Abrir FileChooser para upload no PDI ID: " + pdiDoUsuario.getId());
        // Lógica de upload
        // ...
        // Após upload, chamar carregarDocumentosNaTabela()
    }


    // --- Métodos Auxiliares ---
    private String formatarDataSql(Date prazoSqlDate) {
        if (prazoSqlDate == null) return "N/A";
        try {
            LocalDate prazoLocalDate = prazoSqlDate.toLocalDate();
            return FORMATADOR_DATA.format(prazoLocalDate);
        } catch (Exception e) {
            return "Data inválida";
        }
    }

    // Formatador para java.util.Date (ex: Data Upload do Documento)
    private String formatarDataUtil(java.util.Date dataUtil) {
        if (dataUtil == null) return "N/A";
        try {
            LocalDate dataLocal = dataUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return FORMATADOR_DATA.format(dataLocal);
        } catch (Exception e) {
            return "Data inválida";
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}