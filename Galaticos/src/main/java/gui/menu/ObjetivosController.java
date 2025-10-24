package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import gui.modal.AvaliacaoObjetivoModalController; // Mantenha se o RH avalia
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text; // Import Text
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI;
import modelo.Usuario;
import util.Util;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ObjetivosController {

    // --- Componentes FXML ---
    @FXML
    private Label lblTituloPDI;
    // Kanban RH
    @FXML
    private HBox kanbanContainer; // Container Kanban (RH)
    @FXML
    private VBox colunaNaoIniciado;
    @FXML
    private VBox colunaEmProgresso;
    @FXML
    private VBox colunaConcluido;
    // Detalhes PDI (Não-RH)
    @FXML
    private VBox pdiDetalhesContainer; // Container Detalhes PDI (Não-RH)
    @FXML
    private Label lblPdiStatus;
    @FXML
    private Label lblPdiId;
    @FXML
    private Label lblPdiDataCriacao;
    @FXML
    private Label lblPdiDataFechamento;
    @FXML
    private ProgressBar progressBarGeral;
    @FXML
    private Text textPontuacaoGeral;
    @FXML
    private Label lblSemPdi;
    // Kanban Objetivos (Não-RH)
    @FXML
    private VBox colunaNaoIniciadoUser;
    @FXML
    private VBox colunaEmProgressoUser;
    @FXML
    private VBox colunaConcluidoUser;


    // --- DAOs e Variáveis ---
    private Usuario usuarioLogado;
    private PdiDAO pdiDAO = new PdiDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Chamado pelo MainController para passar o usuário logado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTela();
    }

    /**
     * Configura a tela mostrando o Kanban (RH) ou os detalhes do PDI (Outros).
     */
    private void configurarTela() {
        if (usuarioLogado == null) return;

        limparConteudos(); // Limpa ambos os containers

        try {
            boolean isRh = "RH".equals(usuarioLogado.getTipo_usuario());

            // Define qual container principal estará visível
            kanbanContainer.setVisible(isRh);
            kanbanContainer.setManaged(isRh);
            pdiDetalhesContainer.setVisible(!isRh);
            pdiDetalhesContainer.setManaged(!isRh);

            if (isRh) {
                // Configuração para RH (Kanban Geral)
                lblTituloPDI.setText("Visão Geral de Todos os Objetivos (RH)");
                List<ObjetivoComPDI> todosObjetivos = objetivoDAO.listarTodosComPDI();
                for (ObjetivoComPDI obj : todosObjetivos) {
                    Node cardObjetivo = criarCardObjetivoRH(obj);
                    distribuirCard(cardObjetivo, obj.getStatus(), true); // true indica visão RH
                }
                // Adiciona placeholders no Kanban RH
                adicionarPlaceholderSeVazio(colunaNaoIniciado);
                adicionarPlaceholderSeVazio(colunaEmProgresso);
                adicionarPlaceholderSeVazio(colunaConcluido);
            } else {
                // Configuração para Usuário Comum (Detalhes do PDI + Kanban específico)
                lblTituloPDI.setText("Meu Plano de Desenvolvimento");
                PDI pdiUsuario = pdiDAO.buscarPorColaborador(usuarioLogado.getId()); // Busca o PDI

                if (pdiUsuario != null) {
                    // Esconde mensagem "Sem PDI"
                    lblSemPdi.setVisible(false);
                    lblSemPdi.setManaged(false);

                    // Popula os detalhes do PDI
                    popularDetalhesPDI(pdiUsuario);

                    // Carrega e popula o Kanban de objetivos DESTE PDI
                    List<Objetivo> meusObjetivos = objetivoDAO.buscarPorPdiId(pdiUsuario.getId());
                    for (Objetivo obj : meusObjetivos) {
                        Node cardObjetivo = criarCardObjetivoPadrao(obj);
                        distribuirCard(cardObjetivo, obj.getStatus(), false); // false indica visão User
                    }
                    // Adiciona placeholders no Kanban do usuário
                    adicionarPlaceholderSeVazio(colunaNaoIniciadoUser);
                    adicionarPlaceholderSeVazio(colunaEmProgressoUser);
                    adicionarPlaceholderSeVazio(colunaConcluidoUser);
                } else {
                    // Mostra mensagem "Sem PDI" e esconde o resto
                    lblSemPdi.setVisible(true);
                    lblSemPdi.setManaged(true);
                    // Opcional: Esconder os cards de detalhes e objetivos
                    pdiDetalhesContainer.lookup(".pdi-details-card").setVisible(false);
                    pdiDetalhesContainer.lookup(".pdi-details-card").setManaged(false);
                    pdiDetalhesContainer.lookup(".kanbanObjetivosUsuario").getParent().setVisible(false); // Esconde VBox dos objetivos
                    pdiDetalhesContainer.lookup(".kanbanObjetivosUsuario").getParent().setManaged(false);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            lblTituloPDI.setText("Erro ao carregar dados");
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados: " + e.getMessage());
            limparConteudos(); // Limpa tudo em caso de erro
        }
    }

    /**
     * Limpa o conteúdo de ambos os containers (Kanban RH e Detalhes User).
     */
    private void limparConteudos() {
        // Kanban RH
        colunaNaoIniciado.getChildren().clear();
        colunaEmProgresso.getChildren().clear();
        colunaConcluido.getChildren().clear();
        // Detalhes PDI
        lblPdiStatus.setText("-");
        lblPdiId.setText("-");
        lblPdiDataCriacao.setText("-");
        lblPdiDataFechamento.setText("-");
        progressBarGeral.setProgress(0.0);
        textPontuacaoGeral.setText("0.0%");
        // Kanban User
        colunaNaoIniciadoUser.getChildren().clear();
        colunaEmProgressoUser.getChildren().clear();
        colunaConcluidoUser.getChildren().clear();
        // Mensagem Sem PDI
        lblSemPdi.setVisible(false);
        lblSemPdi.setManaged(false);
    }

    /**
     * Popula os Labels e ProgressBar com os detalhes do PDI fornecido.
     */
    private void popularDetalhesPDI(PDI pdi) {

        // --- CORREÇÃO: Pegar as Strings formatadas diretamente ---
        String dataCriacaoStr = pdi.getDataCriacao(); // Já vem formatada como String
        String dataFechamentoStr = pdi.getDataFechamento(); // Já vem formatada como String

        // Define os textos nos Labels, com verificação de nulo/vazio
        lblPdiStatus.setText(pdi.getStatus() != null ? pdi.getStatus() : "-");
        lblPdiDataCriacao.setText(dataCriacaoStr != null ? dataCriacaoStr : "N/A");
        lblPdiDataFechamento.setText(dataFechamentoStr != null ? dataFechamentoStr : "N/A");

        // Atualiza a barra de progresso (sem alterações)
        float pontuacao = pdi.getPontuacaoGeral();
        progressBarGeral.setProgress(pontuacao);
        textPontuacaoGeral.setText(String.format("%.1f%%", pontuacao * 100));
    }


    /**
     * Adiciona um card à coluna Kanban correta, direcionando para o Kanban certo.
     */
    private void distribuirCard(Node card, String status, boolean isRhView) {
        VBox targetColumn;
        if (status == null) status = "Não Iniciado"; // Tratamento de nulo

        switch (status) {
            case "Em Progresso":
                targetColumn = isRhView ? colunaEmProgresso : colunaEmProgressoUser;
                break;
            case "Concluído":
                targetColumn = isRhView ? colunaConcluido : colunaConcluidoUser;
                break;
            case "Não Iniciado":
            default:
                targetColumn = isRhView ? colunaNaoIniciado : colunaNaoIniciadoUser;
                break;
        }
        targetColumn.getChildren().add(card);
    }


    private Node criarCardObjetivoPadrao(Objetivo objetivo) {
        VBox card = new VBox(); // Remover espaçamento padrão, controlar com padding
        card.getStyleClass().add("objetivo-mini-card"); // Nova classe CSS

        // Descrição (parte principal)
        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");
        VBox.setMargin(descricaoLabel, new Insets(10, 10, 10, 10)); // Margens internas

        // Separador sutil
        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 10, 0, 10)); // Padding para não tocar as bordas

        // Informações Adicionais (Prazo, talvez Peso)
        Label prazoLabel = new Label("Prazo: " + objetivo.getPrazo());
        prazoLabel.getStyleClass().add("objetivo-card-detail");

        // Opcional: Adicionar Peso
        Label pesoLabel = null;
        if (objetivo.getPeso() > 0) { // Mostra só se tiver peso definido
            pesoLabel = new Label("Peso: " + String.format("%.1f", objetivo.getPeso()));
            pesoLabel.getStyleClass().add("objetivo-card-detail");
        }

        // VBox para os detalhes (prazo, peso)
        VBox detailsBox = new VBox(3); // Espaçamento pequeno entre detalhes
        detailsBox.getChildren().add(prazoLabel);
        if (pesoLabel != null) {
            detailsBox.getChildren().add(pesoLabel);
        }
        VBox.setMargin(detailsBox, new Insets(8, 10, 10, 10)); // Margens internas

        // Adiciona elementos ao card
        card.getChildren().addAll(descricaoLabel, separator, detailsBox);

        adicionarAcaoClique(card, objetivo); // Adiciona ação de clique
        return card;
    }

    /**
     * Cria um card visual para um objetivo na visão do RH (com mais informações).
     */
    private Node criarCardObjetivoRH(ObjetivoComPDI objetivo) {
        VBox card = new VBox(); // Remover espaçamento padrão
        card.getStyleClass().add("objetivo-mini-card"); // Nova classe CSS

        // Informações extras para RH (no topo)
        Label infoColaborador = new Label(objetivo.getNomeUsuario() + " (PDI: " + objetivo.getPdiIdOriginal() + ")");
        infoColaborador.getStyleClass().add("objetivo-card-info-rh");
        VBox.setMargin(infoColaborador, new Insets(10, 10, 8, 10)); // Margens internas

        // Separador após info RH
        Separator separatorRh = new Separator();
        separatorRh.setPadding(new Insets(0, 10, 0, 10));

        // Descrição
        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");
        VBox.setMargin(descricaoLabel, new Insets(10, 10, 10, 10));

        // Separador após descrição
        Separator separatorDesc = new Separator();
        separatorDesc.setPadding(new Insets(0, 10, 0, 10));

        // Detalhes (Prazo, Peso)
        Label prazoLabel = new Label("Prazo: " + objetivo.getPrazo());
        prazoLabel.getStyleClass().add("objetivo-card-detail");

        Label pesoLabel = null;
        if (objetivo.getPeso() > 0) {
            pesoLabel = new Label("Peso: " + String.format("%.1f", objetivo.getPeso()));
            pesoLabel.getStyleClass().add("objetivo-card-detail");
        }

        VBox detailsBox = new VBox(3);
        detailsBox.getChildren().add(prazoLabel);
        if (pesoLabel != null) {
            detailsBox.getChildren().add(pesoLabel);
        }
        VBox.setMargin(detailsBox, new Insets(8, 10, 10, 10));

        // Adiciona elementos ao card
        card.getChildren().addAll(infoColaborador, separatorRh, descricaoLabel, separatorDesc, detailsBox);

        adicionarAcaoClique(card, objetivo); // Adiciona ação de clique
        return card;
    }

    /**
     * Adiciona a funcionalidade de clique a um card de objetivo.
     */
    private void adicionarAcaoClique(Node card, Objetivo objetivo) {
        card.setOnMouseClicked(event -> {
            // Verifica se é RH e se o objetivo tem os dados completos
            if ("RH".equals(usuarioLogado.getTipo_usuario()) && objetivo instanceof ObjetivoComPDI) {
                // Chama o método que abre o modal de avaliação
                handleAbrirModalAvaliacao((ObjetivoComPDI) objetivo);
            } else {
                // Ação para outros usuários ou caso de erro
                if (!"RH".equals(usuarioLogado.getTipo_usuario())) {
                    System.out.println("Usuário não-RH clicou no objetivo ID:" + objetivo.getId());
                    // Poderia abrir um modal de visualização aqui
                } else {
                    System.err.println("Erro: Tentativa de avaliar objetivo sem dados completos (não é ObjetivoComPDI). Objetivo ID: " + objetivo.getId());
                    if (Util.class != null)
                        Util.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Dados incompletos para avaliação.");
                }
            }
        });
        // Adiciona estilo CSS para indicar clicável (ex: mudar cursor)
        card.getStyleClass().add("clickable-card");
    }

    // --- MÉTODO NOVO ADICIONADO ---

    /**
     * Abre o modal (janela pop-up) para o RH registrar uma nova avaliação
     * para o objetivo especificado.
     *
     * @param objetivo O objeto ObjetivoComPDI que foi clicado e será avaliado.
     */
    private void handleAbrirModalAvaliacao(ObjetivoComPDI objetivo) {
        try {
            // Carrega o FXML do modal de CRIAÇÃO (AvaliacaoModal.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/AvaliacaoModal.fxml"));
            Parent page = loader.load(); // Carrega a interface

            // Obtém o controller do modal (AvaliacoesModalController)
            AvaliacaoObjetivoModalController controller = loader.getController();

            // Cria a janela (Stage) para o modal
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar Avaliação de Objetivo");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Bloqueia janela principal
            dialogStage.initOwner(lblTituloPDI.getScene().getWindow()); // Define janela pai
            Scene scene = new Scene(page);
            dialogStage.setScene(scene); // Define conteúdo

            // Passa dados e o stage para o controller do modal
            controller.setDialogStage(dialogStage);
            controller.setDados(objetivo, usuarioLogado);

            // Mostra o modal e espera
            dialogStage.showAndWait();

            // Se salvou, recarrega o Kanban
            if (controller.isSalvo()) {
                configurarTela();
            }

        } catch (IOException e) { // Erro ao carregar FXML
            e.printStackTrace();
            if (Util.class != null) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível carregar a tela de avaliação (AvaliacaoModal.fxml): " + e.getMessage());
            }
        } catch (Exception e) { // Outros erros
            e.printStackTrace();
            if (Util.class != null) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao tentar abrir a tela de avaliação: " + e.getMessage());
            }
        }
    }
    // --- FIM DO MÉTODO NOVO ADICIONADO ---


    /**
     * Formata um objeto java.util.Date para String dd/MM/yyyy.
     * Lida com a conversão correta, incluindo java.sql.Date.
     */
    // --- MÉTODO formatarData CORRIGIDO ---
    private String formatarData(Date data) { // Recebe java.util.Date
        if (data == null) {
            return "N/A";
        }
        try {
            // Converte java.util.Date genérico para LocalDate
            LocalDate localDate = data.toInstant()
                    .atZone(ZoneId.systemDefault()) // Usa fuso horário padrão
                    .toLocalDate();
            return localDate.format(FORMATADOR_DATA); // Formata
        } catch (UnsupportedOperationException e) {
            // Tratamento específico para java.sql.Date se toInstant() não for suportado
            try {
                if (data instanceof java.sql.Date) {
                    LocalDate localDate = ((java.sql.Date) data).toLocalDate();
                    return localDate.format(FORMATADOR_DATA);
                } else {
                    throw e; // Relança se não for sql.Date
                }
            } catch (Exception e2) {
                System.err.println("Erro secundário ao formatar data (tipo: " + data.getClass().getName() + "): " + e2.getMessage());
                return "Inválida";
            }
        } catch (Exception e) { // Captura outros erros de formatação
            System.err.println("Erro geral ao formatar data: " + e.getMessage());
            return "Inválida";
        }
    }
    // --- FIM DO MÉTODO formatarData CORRIGIDO ---


    /**
     * Verifica se um VBox está vazio e adiciona um Label de placeholder.
     */
    private void adicionarPlaceholderSeVazio(VBox coluna) {
        if (coluna.getChildren().isEmpty()) {
            Label placeholder = new Label("Nenhum objetivo nesta etapa.");
            placeholder.getStyleClass().add("kanban-empty-placeholder");
            placeholder.setMaxWidth(Double.MAX_VALUE);
            placeholder.setAlignment(Pos.CENTER);
            coluna.setAlignment(Pos.CENTER);
            coluna.getChildren().add(placeholder);
        } else {
            coluna.setAlignment(Pos.TOP_LEFT); // Alinha cards ao topo
        }
    }
}