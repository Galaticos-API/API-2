package gui.modal;

import dao.ObjetivoDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.Documento;
import modelo.Objetivo;
import modelo.PDI;
import modelo.Usuario;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class EditarPDIModalController implements Initializable {

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
    @FXML
    private TableView<Documento> documentoTable;

    private PDI pdiAtual; // Armazena o PDI que está sendo editado
    private Stage dialogStage;
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusPdiComboBox.getItems().addAll("Em Andamento", "Concluído", "Arquivado");
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
}