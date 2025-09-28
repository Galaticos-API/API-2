package gui.modal;

import dao.PdiDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;

import dao.ColaboradorDAO;
import javafx.stage.Stage;
import modelo.Colaborador;
import modelo.PDI;

import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class CadastroPdiModalController implements Initializable {

    @FXML
    private TextField colaboradorIdField;
    @FXML
    private Text colaboradorNomeText;
    @FXML
    private DatePicker dataFechamentoField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Text mensagemErro;

    // Simulação do ID do colaborador buscado
    private int idColaboradorBuscado = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Popula o ComboBox com os ENUMs permitidos para o status inicial
        statusComboBox.getItems().addAll("Em Andamento", "Arquivado");
        // O status "Concluído" não é um status inicial válido
    }

    @FXML
    private void handleBuscarColaborador() throws SQLException {
        String inputId = colaboradorIdField.getText();
        if (inputId.trim().isEmpty()) {
            mensagemErro.setText("Insira o ID do colaborador.");
            return;
        }

        Colaborador colaborador = new Colaborador();
        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
        colaborador = colaboradorDAO.buscarPorUsuario_id(inputId);

        if (colaborador != null) {
            try {
                int id = Integer.parseInt(inputId);
                idColaboradorBuscado = id;
                colaboradorNomeText.setText("Nome: " + colaborador.getNome() + " (ID: " + idColaboradorBuscado + ")");
                mensagemErro.setText("");

            } catch (NumberFormatException e) {
                mensagemErro.setText("ID inválido. Use apenas números.");
                idColaboradorBuscado = -1;
            }
        } else {
            mensagemErro.setText("Colaborador não encontrado.");
        }

    }

    @FXML
    private void handleCriarPdi() throws SQLException {
        LocalDate dataFechamentoStr = dataFechamentoField.getValue();
        Instant instant = dataFechamentoStr.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date dataFechamento = Date.from(instant);

        Colaborador colaborador = new Colaborador();
        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
        colaborador = colaboradorDAO.buscarPorUsuario_id(Integer.toString(idColaboradorBuscado));

        String status = statusComboBox.getValue();

        if (idColaboradorBuscado == -1) {
            mensagemErro.setText("Primeiro, busque e valide um Colaborador.");
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            mensagemErro.setText("Todos os campos obrigatórios (Ano e Status) devem ser preenchidos.");
            return;
        }

        mensagemErro.setText(""); // Limpa erros se a validação passou

        PDI novoPdi = new PDI(colaborador.getId(), status, new Date(), dataFechamento);
        PdiDAO pdiDao = new PdiDAO();
        pdiDao.adicionar(novoPdi);

        showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                "PDI criado com sucesso. Agora você pode adicionar Objetivos e Documentos!");
        dialogStage.close();
    }

    @FXML
    private void handleCancelar() {
        // Lógica para fechar a janela de cadastro (ex: Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); stage.close();)
        showAlert(Alert.AlertType.INFORMATION, "Ação", "Cadastro cancelado.");
        dialogStage.close();
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