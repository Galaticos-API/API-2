package gui.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import modelo.PDI;
import dao.PdiDAO;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SinglePdiController {

    @FXML private Label labelTituloPdi;
    @FXML private Label labelColaboradorId;
    @FXML private Label labelStatus;
    @FXML private Label labelDataCriacao;
    @FXML private Label labelDataFechamento;
    @FXML private Label labelPontuacaoGeral;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private final PdiDAO pdiDAO = new PdiDAO();

    /**
     * Carrega a PDI do banco de dados e preenche os campos da tela.
     * @param pdiId O ID da PDI a ser carregada.
     */
    public void setPdiData(int pdiId) {

        // 1. CHAMA O DAO usando o buscarPorId
        PDI pdi = pdiDAO.buscarPorId(pdiId);

        // 2. Preencher os Labels com os dados
        if (pdi != null) {

            labelTituloPdi.setText("PDI " + pdi.getId());
            labelColaboradorId.setText("Colaborador: " + pdi.getColaboradorId());
            labelStatus.setText("Status: " + pdi.getStatus());

            String dataCriacao = pdi.getDataCriacao() != null ? DATE_FORMAT.format(pdi.getDataCriacao()) : "N/A";
            String dataFechamento = pdi.getDataFechamento() != null ? DATE_FORMAT.format(pdi.getDataFechamento()) : "Em aberto";

            labelDataCriacao.setText("Início em: " + dataCriacao);
            labelDataFechamento.setText("Término em: " + dataFechamento);

            String pontuacao = String.format("%.2f", pdi.getPontuacaoGeral());

            labelPontuacaoGeral.setText("Pontuação: " + pontuacao);

        } else {
            // Caso a PDI não seja encontrada
            labelTituloPdi.setText("PDI Não Encontrada (ID: " + pdiId + ")");
            labelColaboradorId.setText("Colaborador: -");
            labelStatus.setText("Status: Falha ao carregar");
        }
    }
}