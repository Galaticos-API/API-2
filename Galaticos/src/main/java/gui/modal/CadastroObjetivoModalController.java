package gui.modal;

import dao.ObjetivoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Objetivo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CadastroObjetivoModalController {
    @FXML
    private TextArea descricaoField;
    @FXML
    private DatePicker prazoField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea comentariosField;
    @FXML
    private TextField pesoField;
    @FXML
    private TextField pontuacaoField;
    @FXML
    private Label mensagemErro;

    private int pdiId;
    private Stage dialogStage;
    private boolean salvo = false;

    public void setPdiId(int pdiId) {
        this.pdiId = pdiId;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void initialize() {
        statusComboBox.getItems().addAll("Não Iniciado", "Em Progresso", "Concluído");
    }

    @FXML
    private void handleSalvar() {
        try {
            String descricao = descricaoField.getText();
            LocalDate prazo = prazoField.getValue();
            String status = statusComboBox.getValue();
            String comentarios = comentariosField.getText();
            float peso = pesoField.getText().isEmpty() ? 0 : Float.parseFloat(pesoField.getText());
            float pontuacao = pontuacaoField.getText().isEmpty() ? 0 : Float.parseFloat(pontuacaoField.getText());

            if (descricao == null || descricao.trim().isEmpty()) {
                mensagemErro.setText("Descrição é obrigatória.");
                return;
            }
            if (prazo == null) {
                mensagemErro.setText("Prazo é obrigatório.");
                return;
            }
            if (status == null) {
                mensagemErro.setText("Status é obrigatório.");
                return;
            }

            Objetivo objetivo = new Objetivo();
            objetivo.setPdiId(pdiId);
            objetivo.setDescricao(descricao.trim());
            objetivo.setPrazo(Date.from(prazo.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            objetivo.setStatus(status);
            objetivo.setComentarios(comentarios != null ? comentarios.trim() : "");
            objetivo.setPeso(peso);
            objetivo.setPontuacao(pontuacao);

            ObjetivoDAO dao = new ObjetivoDAO();
            dao.adicionar(objetivo);

            salvo = true;
            dialogStage.close();
        } catch (Exception e) {
            mensagemErro.setText("Erro ao salvar objetivo: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}