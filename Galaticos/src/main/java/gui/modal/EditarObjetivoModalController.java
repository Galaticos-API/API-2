package gui.modal;

import dao.ObjetivoDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Objetivo;
import util.Util;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditarObjetivoModalController implements Initializable {

    @FXML
    private TextArea descricaoField;
    @FXML
    private DatePicker prazoPicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<String> tipoComboBox;
    @FXML
    private TextField pesoField;
    @FXML
    private TextField pontuacaoField;

    private Stage dialogStage;
    private Objetivo objetivoAtual;
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusComboBox.getItems().addAll("Não Iniciado", "Em Progresso", "Concluído");
        tipoComboBox.getItems().addAll("Hard skill", "Soft skill");
    }

    /**
     * Define o Stage deste modal.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Recebe o objetivo selecionado na tabela e popula os campos.
     */
    public void setObjetivo(Objetivo objetivo) {
        this.objetivoAtual = objetivo;
        popularDados();
    }

    /**
     * Preenche os campos do formulário com os dados do objetivo atual.
     */
    private void popularDados() {
        if (objetivoAtual == null) return;

        descricaoField.setText(objetivoAtual.getDescricao());

        // Converte java.sql.Date para LocalDate
        if (objetivoAtual.getPrazo() != null) {
            prazoPicker.setValue(objetivoAtual.getPrazo().toLocalDate());
        }

        statusComboBox.setValue(objetivoAtual.getStatus());

        // "Tipo" é o campo "comentarios"
        tipoComboBox.setValue(objetivoAtual.getComentarios());

        pesoField.setText(String.format("%.1f", objetivoAtual.getPeso()));
        pontuacaoField.setText(String.format("%.1f", objetivoAtual.getPontuacao()));
    }

    /**
     * Chamado ao clicar no botão Salvar.
     */
    @FXML
    private void handleSalvarObjetivo() {
        if (!validarCampos()) {
            return;
        }

        try {
            // Atualiza o objeto 'objetivoAtual' com os novos dados
            objetivoAtual.setDescricao(descricaoField.getText().trim());

            LocalDate localDatePrazo = prazoPicker.getValue();
            objetivoAtual.setPrazo(localDatePrazo != null ? Date.valueOf(localDatePrazo) : null);

            objetivoAtual.setStatus(statusComboBox.getValue());

            // Salva o "Tipo" no campo "comentarios"
            objetivoAtual.setComentarios(tipoComboBox.getValue());

            objetivoAtual.setPeso(Float.parseFloat(pesoField.getText()));
            objetivoAtual.setPontuacao(Float.parseFloat(pontuacaoField.getText()));

            // Persiste a atualização no banco de dados
            objetivoDAO.atualizar(objetivoAtual);

            salvo = true;
            Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Objetivo atualizado com sucesso!");
            dialogStage.close();

        } catch (NumberFormatException e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "Peso e Pontuação devem ser números válidos (ex: 0.0).");
        } catch (RuntimeException e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco", "Não foi possível salvar as alterações: " + e.getMessage());
        }
    }

    /**
     * Valida se os campos obrigatórios estão preenchidos.
     */
    private boolean validarCampos() {
        if (descricaoField.getText() == null || descricaoField.getText().trim().isEmpty()) {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "A Descrição não pode estar vazia.");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Selecione um Status.");
            return false;
        }
        return true;
    }

    /**
     * Chamado ao clicar no botão Cancelar.
     */
    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    /**
     * Retorna true se o objetivo foi salvo.
     */
    public boolean isSalvo() {
        return salvo;
    }
}