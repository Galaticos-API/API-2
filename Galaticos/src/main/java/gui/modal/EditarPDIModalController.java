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

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class EditarPDIModalController implements Initializable {

    // Variável de instância para armazenar o PDI atual
    private PDI pdiAtual; // <-- 1. VARIÁVEL ADICIONADA

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
    private TableColumn<Objetivo, String> acoesColumn;

    @FXML
    private ObservableList<Objetivo> objetivoObservableList;


    // Aba 4: Documentos
    @FXML
    private TableView<Documento> documentoTable;


    private Stage dialogStage;
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusPdiComboBox.getItems().addAll("Em Andamento", "Concluído", "Arquivado");

        configurarColunasTabela();
        carregarTodosOsPDIs();
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
            // 1. Buscar dados do Usuário
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            // Assumindo que pdi.getColaboradorId() agora retorna o ID do usuário
            Usuario usuario = usuarioDAO.buscarPorId(pdiAtual.getColaboradorId());

            if (usuario != null) {
                usuarioNomeField.setText(usuario.getNome());
                usuarioCargoField.setText(usuario.getTipo_usuario());
            }

            // 2. Preencher dados do PDI
            statusPdiComboBox.setValue(pdiAtual.getStatus());

            if (pdiAtual.getDataCriacao() != null) {
                dataCriacaoPicker.setValue(pdiAtual.getDataCriacao().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (pdiAtual.getDataFechamento() != null) {
                dataFechamentoPicker.setValue(pdiAtual.getDataFechamento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
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
        String status = statusPdiComboBox.getValue();

        if (status != null && !status.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Dados gerais do PDI atualizados!");
            salvo = true;
        } else {
            showAlert(Alert.AlertType.WARNING, "Atenção", "Selecione o Status do PDI.");
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
        // Lógica para abrir FileChooser e processar upload (US-06)
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidade", "Abrir seletor de arquivos para Upload de Documento.");
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
        acoesColumn.setCellValueFactory(new PropertyValueFactory<>("acoes"));
    }

    private void carregarTodosOsPDIs() {
        List<Objetivo> objetivos = ObjetivoDAO.lerTodos();
        objetivoObservableList = FXCollections.observableArrayList(objetivos);
        objetivoTable.setItems(objetivoObservableList);
    }
}