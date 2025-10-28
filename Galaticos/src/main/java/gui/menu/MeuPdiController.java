package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import dao.AvaliacaoDAO;
import gui.modal.AvaliacaoObjetivoModalController;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI; // Pode ser removido se 'popularDetalhesPDI' for removido
import modelo.Usuario;
import modelo.Avaliacao;
import util.Util;

import java.io.IOException;
import java.sql.Date;
// import java.text.SimpleDateFormat; // Não utilizado
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import gui.modal.AvaliacoesObjetivoModalController;
import java.io.IOException;

public class MeuPdiController {

    // --- Componentes FXML ---
    @FXML
    private Label lblTituloPDI;

    // Container Kanban (AGORA USADO POR TODOS: RH, Gestor Geral, Gestor de Area)
    @FXML
    private HBox kanbanContainer;
    @FXML
    private VBox colunaNaoIniciado;
    @FXML
    private VBox colunaEmProgresso;
    @FXML
    private VBox colunaConcluido;

    // Container Detalhes PDI (NÃO MAIS USADO NESTA TELA)
    // Esta lógica agora pertence ao 'MeuPdiGUIController'
    @FXML
    private VBox pdiDetalhesContainer;
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
    @FXML
    private VBox colunaNaoIniciadoUser;
    @FXML
    private VBox colunaEmProgressoUser;
    @FXML
    private VBox colunaConcluidoUser;


    private Usuario usuarioLogado;
    private PdiDAO pdiDAO = new PdiDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter FORMATADOR_DATA_AVALIACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Chamado pelo MainController para passar o usuário logado.
     */
    public void setUsuario(Usuario usuario) {
        System.out.println("setusuario Meu pdi");
        this.usuarioLogado = usuario;
        configurarTela();
    }

    /**
     * Configura a tela. Agora, SEMPRE mostrará o Kanban,
     * filtrando os dados com base no tipo de usuário.
     */
    private void configurarTela() {
        if (usuarioLogado == null) return;

        limparConteudos();

        kanbanContainer.setVisible(false);
        kanbanContainer.setManaged(false);
        pdiDetalhesContainer.setVisible(true);
        pdiDetalhesContainer.setManaged(true);
        System.out.println("configurarTela Meu pdi");
        try {
            lblTituloPDI.setText("Meu Plano de Desenvolvimento");
            PDI pdiUsuario = pdiDAO.buscarPorColaborador(usuarioLogado.getId()); // Busca o PDI

            if (pdiUsuario != null) {
                lblSemPdi.setVisible(false);
                lblSemPdi.setManaged(false);

                popularDetalhesPDI(pdiUsuario);

                List<Objetivo> meusObjetivos = objetivoDAO.buscarPorPdiId(pdiUsuario.getId());
                System.out.println("MeuPdiController: Objetivos encontrados: " + meusObjetivos.size());

                for (Objetivo obj : meusObjetivos) {
                    Node cardObjetivo = criarCardObjetivoPadrao(obj);
                    distribuirCardMeuPDI(cardObjetivo, obj.getStatus());
                }
                adicionarPlaceholderSeVazio(colunaNaoIniciadoUser);
                adicionarPlaceholderSeVazio(colunaEmProgressoUser);
                adicionarPlaceholderSeVazio(colunaConcluidoUser);
            } else {
//                lblSemPdi.setVisible(true);
//                lblSemPdi.setManaged(true);
//                pdiDetalhesContainer.lookup(".pdi-details-card").setVisible(false);
//                pdiDetalhesContainer.lookup(".pdi-details-card").setManaged(false);
//                pdiDetalhesContainer.lookup(".kanbanObjetivosUsuario").getParent().setVisible(false);
//                pdiDetalhesContainer.lookup(".kanbanObjetivosUsuario").getParent().setManaged(false);

                // Se não há PDI:
                lblSemPdi.setVisible(true); // Mostra a mensagem "sem PDI"
                lblSemPdi.setManaged(true);

                // Esconde TODO o container de detalhes do PDI
                pdiDetalhesContainer.setVisible(false);
                pdiDetalhesContainer.setManaged(false);

                // Garante que o container do Kanban geral (para gestores) permaneça escondido
                kanbanContainer.setVisible(false);
                kanbanContainer.setManaged(false);
            }

            // Adiciona placeholders no Kanban principal
            adicionarPlaceholderSeVazio(colunaNaoIniciado);
            adicionarPlaceholderSeVazio(colunaEmProgresso);
            adicionarPlaceholderSeVazio(colunaConcluido);

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
        // Kanban User (limpa por segurança)
        colunaNaoIniciadoUser.getChildren().clear();
        colunaEmProgressoUser.getChildren().clear();
        colunaConcluidoUser.getChildren().clear();
        // Detalhes PDI (limpa por segurança)
        lblPdiStatus.setText("-");
        lblPdiId.setText("-");
        lblPdiDataCriacao.setText("-");
        lblPdiDataFechamento.setText("-");
        progressBarGeral.setProgress(0.0);
        textPontuacaoGeral.setText("0.0%");
        lblSemPdi.setVisible(false);
        lblSemPdi.setManaged(false);
    }

    /**
     * Adiciona um card à coluna Kanban correta (MÉTODO SIMPLIFICADO).
     */
    private void distribuirCard(Node card, String status) {
        VBox targetColumn;
        if (status == null) status = "Não Iniciado"; // Tratamento de nulo

        switch (status) {
            case "Em Progresso":
                targetColumn = colunaEmProgresso; // Sempre usa as colunas principais
                break;
            case "Concluído":
                targetColumn = colunaConcluido; // Sempre usa as colunas principais
                break;
            case "Não Iniciado":
            default:
                targetColumn = colunaNaoIniciado; // Sempre usa as colunas principais
                break;
        }
        targetColumn.getChildren().add(card);
    }

    private void distribuirCardMeuPDI(Node card, String status) {
        VBox targetColumn;
        if (status == null) status = "Não Iniciado";

        switch (status) {
            case "Em Progresso":
                targetColumn = colunaEmProgressoUser;
                break;
            case "Concluído":
                targetColumn = colunaConcluidoUser;
                break;
            case "Não Iniciado":
            default:
                targetColumn = colunaNaoIniciadoUser;
                break;
        }
        if (targetColumn != null) {
            targetColumn.getChildren().removeIf(node -> node.getStyleClass().contains("kanban-empty-placeholder"));
            targetColumn.getChildren().add(card);
            targetColumn.setAlignment(Pos.TOP_LEFT);
        } else {
            System.err.println("Erro: Coluna alvo para o status '" + status + "' não encontrada no FXML (coluna...User).");
        }
    }

    private void handleMostrarAvaliacoes(Objetivo objetivo) {
        if (objetivo == null) return;

        try {
            List<Avaliacao> avaliacoes = avaliacaoDAO.buscarPorObjetivoId(objetivo.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/AvaliacoesObjetivoModal.fxml"));
            Parent page = loader.load();

            AvaliacoesObjetivoModalController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Histórico de Avaliações");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(lblTituloPDI.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            controller.setDados(objetivo, avaliacoes);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível carregar a tela de visualização de avaliações: " + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível buscar ou exibir as avaliações: " + e.getMessage());
        }
    }


    /**
     * Cria um card visual para um objetivo na visão do RH (com mais informações).
     * Este card será usado por todos os gestores nesta tela.
     */
    private Node criarCardObjetivoRH(ObjetivoComPDI objetivo) {
        VBox card = new VBox();
        card.getStyleClass().add("objetivo-mini-card");

        // Informações extras (Colaborador e PDI ID)
        Label infoColaborador = new Label(objetivo.getNomeUsuario() + " (PDI: " + objetivo.getPdiIdOriginal() + ")");
        infoColaborador.getStyleClass().add("objetivo-card-info-rh");
        VBox.setMargin(infoColaborador, new Insets(10, 10, 8, 10));

        Separator separatorRh = new Separator();
        separatorRh.setPadding(new Insets(0, 10, 0, 10));

        // Descrição
        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");
        VBox.setMargin(descricaoLabel, new Insets(10, 10, 10, 10));

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

        card.getChildren().addAll(infoColaborador, separatorRh, descricaoLabel, separatorDesc, detailsBox);

        adicionarAcaoClique(card, objetivo); // Adiciona ação de clique
        return card;
    }

    /**
     * Adiciona a funcionalidade de clique a um card de objetivo.
     * ATENÇÃO: A lógica atual SÓ permite que RH abra o modal.
     * Gestores (Geral/Area) podem clicar, mas nada acontecerá.
     * Se desejar que eles também avaliem, mude o IF.
     */
    private void adicionarAcaoClique(Node card, Objetivo objetivo) {
        card.setOnMouseClicked(event -> {
            // Apenas RH pode abrir o modal de avaliação
            if ("RH".equals(usuarioLogado.getTipo_usuario()) && objetivo instanceof ObjetivoComPDI) {
                handleAbrirModalAvaliacao((ObjetivoComPDI) objetivo);
            } else if (objetivo instanceof ObjetivoComPDI) {
                // Gestores (não-RH) clicam, mas não faz nada
                System.out.println("Gestor (" + usuarioLogado.getTipo_usuario() + ") visualizou objetivo ID:" + objetivo.getId());
            } else {
                System.err.println("Erro: Tentativa de avaliar objetivo sem dados completos. Objetivo ID: " + objetivo.getId());
                if (Util.class != null)
                    Util.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Dados incompletos para avaliação.");
            }
        });
        card.getStyleClass().add("clickable-card");
    }

    /**
     * Abre o modal (janela pop-up) para o RH registrar uma nova avaliação.
     */
    private void handleAbrirModalAvaliacao(ObjetivoComPDI objetivo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/AvaliacaoModal.fxml"));
            Parent page = loader.load();

            AvaliacaoObjetivoModalController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar Avaliação de Objetivo");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(lblTituloPDI.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            controller.setDados(objetivo, usuarioLogado);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                configurarTela(); // Recarrega o Kanban
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (Util.class != null) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível carregar a tela de avaliação (AvaliacaoModal.fxml): " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Util.class != null) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao tentar abrir a tela de avaliação: " + e.getMessage());
            }
        }
    }


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

    // --- MÉTODOS NÃO MAIS UTILIZADOS NESTA TELA ---
    // (Podem ser removidos se você tiver certeza)

    /**
     * [NÃO UTILIZADO]
     * Popula os Labels e ProgressBar com os detalhes do PDI.
     * Esta lógica agora pertence a 'MeuPdiGUIController'.
     */
    private void popularDetalhesPDI(PDI pdi) {
        String dataCriacaoStr = pdi.getDataCriacao();
        String dataFechamentoStr = pdi.getDataFechamento();

        lblPdiStatus.setText(pdi.getStatus() != null ? pdi.getStatus() : "-");
        lblPdiDataCriacao.setText(dataCriacaoStr != null ? dataCriacaoStr : "N/A");
        lblPdiDataFechamento.setText(dataFechamentoStr != null ? dataFechamentoStr : "N/A");

        float pontuacao = pdi.getPontuacaoGeral();
        progressBarGeral.setProgress(pontuacao);
        textPontuacaoGeral.setText(String.format("%.1f%%", pontuacao * 100));
    }

    /**
     * [NÃO UTILIZADO]
     * Cria um card visual para um objetivo padrão (visão do colaborador).
     * Esta lógica agora pertence a 'MeuPdiGUIController'.
     */
    private Node criarCardObjetivoPadrao(Objetivo objetivo) {
        VBox card = new VBox();
        card.getStyleClass().add("objetivo-mini-card");

        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");
        VBox.setMargin(descricaoLabel, new Insets(10, 10, 10, 10));

        Separator separator = new Separator();

        Label prazoLabel = new Label("Prazo: " + (objetivo.getPrazo() != null ? formatarData(objetivo.getPrazo()) : "N/A"));
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

        card.getChildren().addAll(descricaoLabel, separator, detailsBox);

        card.setOnMouseClicked(event -> {
            if (usuarioLogado != null) {
                if ("Colaborador".equals(usuarioLogado.getTipo_usuario())) {
                    handleMostrarAvaliacoes(objetivo);
                }
            }
        });
        card.getStyleClass().add("clickable-card");

        return card;
    }

    /**
     * [NÃO UTILIZADO]
     * Formata um objeto java.util.Date para String dd/MM/yyyy.
     */
    private String formatarData(Date data) {
        if (data == null) {
            return "N/A";
        }
        try {
            LocalDate localDate;
            if (data instanceof Date) {
                localDate = ((Date) data).toLocalDate();
            } else {
                localDate = data.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            return localDate.format(FORMATADOR_DATA);
        } catch (Exception e) {
            System.err.println("Erro ao formatar data (" + data.getClass().getName() + "): " + e.getMessage());
            return "Inválida";
        }
    }
}