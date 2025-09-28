package gui.modal;

import dao.PdiDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import modelo.Objetivo;
import modelo.Documento;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

// A classe precisa implementar Initializable para carregar dados na inicialização
public class SinglePDIModalController implements Initializable {

    // Elementos da Barra de Progresso
    @FXML
    private ProgressBar progressBarGeral;
    @FXML
    private Text textPontuacaoGeral;

    // Aba 1: Dados Gerais
    @FXML
    private TextField colaboradorNomeField;
    @FXML
    private TextField colaboradorCargoField;
    @FXML
    private ComboBox<String> statusPdiComboBox; // Tipo String para o ENUM
    @FXML
    private DatePicker dataCriacaoPicker;
    @FXML
    private DatePicker dataFechamentoPicker;

    // Aba 2: Objetivos e Metas
    @FXML
    private TableView<Objetivo> objetivoTable;

    // Aba 3: Avaliação de Skills
    //@FXML private TableView<PdiHabilidade> skillTable;

    // Aba 4: Documentos
    @FXML
    private TableView<Documento> documentoTable;

    private String id;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusPdiComboBox.getItems().addAll("Em Andamento", "Concluído", "Arquivado");

        loadPdiData(getId());
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    private void loadPdiData(String pdiId) {

        PdiDAO pdiDAO = new PdiDAO();

        float pontuacao = 0f;
        String statusAtual = "Em Andamento";

        colaboradorNomeField.setText("João da Silva");
        colaboradorCargoField.setText("Desenvolvedor Java Jr");
        statusPdiComboBox.setValue(statusAtual);
        // dataCriacaoPicker.setValue(pdi.getDataCriacao().toLocalDate());
        progressBarGeral.setProgress(pontuacao);
        textPontuacaoGeral.setText(String.format("%.1f%% Concluído", pontuacao * 100));

        // 3. Carregar Tabelas (simulação)
        // objetivoTable.setItems(ObjetivoDAO.findByPdiId(pdiId));
        // skillTable.setItems(PdiHabilidadeDAO.findByPdiId(pdiId));
        // documentoTable.setItems(DocumentoDAO.findByPdiId(pdiId));
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
    private void handleAddObjective() {
        // Abre uma nova janela/modal para o cadastro de um novo Objetivo (US-03)
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidade", "Abrir modal para criação de novo Objetivo.");
    }

    @FXML
    private void handleCalculateGeneralScore() {
        // Lógica para calcular a pontuação ponderada de todos os objetivos
        // 1. Chama o método de cálculo: float novoScore = PdiService.calcularPontuacao(pdi.getId());
        // 2. Atualiza a tela: progressBarGeral.setProgress(novoScore);

        showAlert(Alert.AlertType.INFORMATION, "Cálculo", "Pontuação geral recalculada e atualizada.");
    }

    @FXML
    private void handleAddSkill() {
        // Abre um modal para adicionar e avaliar uma Hard ou Soft Skill (US-04)
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidade", "Abrir modal para adicionar e avaliar Skill.");
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

    private Stage dialogStage;
    private boolean salvo = false;
    public boolean isSalvo() {
        return salvo;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}