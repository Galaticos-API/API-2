package gui.modal;

import dao.AvaliacaoDAO;
import dao.ObjetivoDAO;
import dao.PdiDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Avaliacao;
import modelo.ObjetivoComPDI;
import modelo.Usuario;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class AvaliacaoObjetivoModalController {

    @FXML
    private Label lblColaborador;
    @FXML
    private Label lblPdiId;
    @FXML
    private TextArea txtObjetivo;
    @FXML
    public TextField txtTipo;
    @FXML
    private ComboBox<Double> comboNota;
    @FXML
    private Label lblMensagemErro;

    private Stage dialogStage;
    private ObjetivoComPDI objetivo;
    private Usuario avaliador;
    private boolean salvo = false;

    private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();

    @FXML
    public void initialize() {
        comboNota.setItems(FXCollections.observableArrayList(
                DoubleStream.iterate(0.0, n -> n <= 10.0, n -> n + 0.5)
                        .boxed()
                        .collect(Collectors.toList())
        ));

        // comboStatus.getItems().addAll("Não Iniciado", "Em Progresso", "Concluído");
    }


    public void setDados(ObjetivoComPDI objetivo, Usuario avaliador) {
        this.objetivo = objetivo;
        this.avaliador = avaliador;


        lblColaborador.setText(objetivo.getNomeUsuario());
        lblPdiId.setText(String.valueOf(objetivo.getPdiIdOriginal()));
        txtObjetivo.setText(objetivo.getDescricao());
        txtTipo.setText(objetivo.getComentarios());
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try {

            Avaliacao novaAvaliacao = new Avaliacao();
            novaAvaliacao.setObjetivoId(objetivo.getId());
            novaAvaliacao.setAvaliadorId(Integer.parseInt(avaliador.getId()));
            novaAvaliacao.setNota(comboNota.getValue());
            novaAvaliacao.setComentario(txtTipo.getText().trim());
            novaAvaliacao.setStatus_objetivo("Concluído");
            novaAvaliacao.setDataAvaliacao(LocalDate.now());

            avaliacaoDAO.adicionar(novaAvaliacao);


            objetivo.setStatus("Concluído");

            objetivoDAO.atualizar(objetivo);
            PdiDAO.atualizarPontuacaoGeral(objetivo.getPdiId());

            salvo = true;
            dialogStage.close();

        } catch (NumberFormatException e) {
            lblMensagemErro.setText("Erro: ID do avaliador é inválido.");
            e.printStackTrace();
        } catch (RuntimeException e) {
            lblMensagemErro.setText("Erro ao salvar no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (comboNota.getValue() == null) {
            lblMensagemErro.setText("Por favor, selecione uma nota.");
            return false;
        }
        lblMensagemErro.setText("");
        return true;
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }
}