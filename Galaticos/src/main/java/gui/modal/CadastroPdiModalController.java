package gui.modal;

import dao.PdiDAO;
import dao.UsuarioDAO; // Importe o DAO correto
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.Usuario; // Importe o modelo correto

import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class CadastroPdiModalController implements Initializable {

    @FXML
    private TextField usuarioIdField; // Renomeado
    @FXML
    private Text usuarioNomeText; // Renomeado
    @FXML
    private DatePicker dataFechamentoField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Text mensagemErro;

    private Usuario usuarioEncontrado; // Armazena o objeto Usuario completo
    private Stage dialogStage;
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.getItems().addAll("Em Andamento", "Arquivado");
    }

    @FXML
    private void handleBuscarUsuario() { // Renomeado
        String inputIdStr = usuarioIdField.getText().trim();
        if (inputIdStr.isEmpty()) {
            mensagemErro.setText("Insira o ID do usuário.");
            return;
        }

        try {
            String idUsuario = inputIdStr;
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            this.usuarioEncontrado = usuarioDAO.buscarPorId(idUsuario);

            if (this.usuarioEncontrado != null) {
                usuarioNomeText.setText("Nome: " + this.usuarioEncontrado.getNome() + " (ID: " + this.usuarioEncontrado.getId() + ")");
                mensagemErro.setText(""); // Limpa o erro
            } else {
                mensagemErro.setText("Usuário não encontrado.");
                this.usuarioEncontrado = null;
            }
        } catch (NumberFormatException e) {
            mensagemErro.setText("ID inválido. Use apenas números.");
            this.usuarioEncontrado = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleCriarPdi() {
        if (usuarioEncontrado == null) {
            mensagemErro.setText("Primeiro, busque e encontre um usuário válido.");
            return;
        }

        if (statusComboBox.getValue() == null) {
            mensagemErro.setText("Selecione um status para o PDI.");
            return;
        }

        if (dataFechamentoField.getValue() == null) {
            mensagemErro.setText("Selecione uma data de fechamento.");
            return;
        }

        mensagemErro.setText(""); // Limpa erros

        // Converte LocalDate para Date
        Date dataFechamento = Date.from(dataFechamentoField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Cria o novo PDI usando o ID do usuário encontrado
        PDI novoPdi = new PDI(usuarioEncontrado.getId(), statusComboBox.getValue(), new Date(), dataFechamento);
        PdiDAO pdiDao = new PdiDAO();
        pdiDao.adicionar(novoPdi);

        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI criado com sucesso para o usuário: " + usuarioEncontrado.getNome());

        salvo = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
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