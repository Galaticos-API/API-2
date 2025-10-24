package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
// 1. Import do controller correto do modal
import gui.modal.AvaliacaoObjetivoModalController;
import javafx.fxml.FXML;
// 2. Imports adicionais necessários para abrir o modal
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Para usar Alertas (opcional)
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI;
import modelo.Usuario;
import util.Util; // Importe sua classe Util se tiver

import java.io.IOException; // Para tratar erro do FXML
// import java.sql.Date; // Remova este import se não estiver usando java.sql.Date diretamente
import java.time.LocalDate;
import java.time.ZoneId; // Necessário para formatarData
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Date; // Importe java.util.Date

public class ObjetivosController {

    @FXML private Label lblTituloPDI;
    @FXML private VBox colunaNaoIniciado;
    @FXML private VBox colunaEmProgresso;
    @FXML private VBox colunaConcluido;

    private Usuario usuarioLogado; // Armazena o usuário logado
    private PdiDAO pdiDAO = new PdiDAO(); // DAO para buscar PDIs do usuário
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Método chamado pela tela principal para passar o usuário logado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTela(); // Chama a configuração e carregamento
    }

    /**
     * Configura o título e carrega os objetivos apropriados.
     */
    private void configurarTela() {
        if (usuarioLogado == null) return;

        // Limpa as colunas
        colunaNaoIniciado.getChildren().clear();
        colunaEmProgresso.getChildren().clear();
        colunaConcluido.getChildren().clear();

        try {
            // Verifica se é RH para carregar todos os objetivos
            if ("RH".equals(usuarioLogado.getTipo_usuario())) {
                System.out.println("entrou RH");
                lblTituloPDI.setText("Visão Geral de Todos os Objetivos (RH)");
                List<ObjetivoComPDI> todosObjetivos = objetivoDAO.listarTodosComPDI();
                for (ObjetivoComPDI obj : todosObjetivos) {
                    Node cardObjetivo = criarCardObjetivoRH(obj); // Usa método específico para RH
                    distribuirCard(cardObjetivo, obj.getStatus());
                }
            } else {
                System.out.println("entrou comum");
                PDI pdiPrincipal = pdiDAO.buscarPorColaborador(usuarioLogado.getId());
                if (pdiPrincipal != null) {
                    lblTituloPDI.setText("Meus Objetivos - PDI " + pdiPrincipal.getId());
                    List<Objetivo> meusObjetivos = objetivoDAO.buscarPorPdiId(pdiPrincipal.getId());
                    for (Objetivo obj : meusObjetivos) {
                        Node cardObjetivo = criarCardObjetivoPadrao(obj); // Usa método padrão
                        distribuirCard(cardObjetivo, obj.getStatus());
                    }
                } else {
                    lblTituloPDI.setText("Meus Objetivos (Nenhum PDI encontrado)");
                }
            }

            // Adiciona placeholders se alguma coluna estiver vazia
            adicionarPlaceholderSeVazio(colunaNaoIniciado);
            adicionarPlaceholderSeVazio(colunaEmProgresso);
            adicionarPlaceholderSeVazio(colunaConcluido);

        } catch (RuntimeException e) {
            e.printStackTrace();
            // Mostrar alerta de erro
            lblTituloPDI.setText("Erro ao carregar objetivos");
            // Adicionado tratamento visual de erro
            if (Util.class != null) { Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os objetivos: " + e.getMessage()); }
            colunaNaoIniciado.getChildren().clear(); colunaEmProgresso.getChildren().clear(); colunaConcluido.getChildren().clear();
            adicionarPlaceholderSeVazio(colunaNaoIniciado); adicionarPlaceholderSeVazio(colunaEmProgresso); adicionarPlaceholderSeVazio(colunaConcluido);
        }
    }

    /**
     * Adiciona um card à coluna Kanban correta com base no status.
     */
    private void distribuirCard(Node card, String status) {
        // Adicionado tratamento para status nulo ou inesperado
        if (status == null) status = "Não Iniciado";
        switch (status) {
            case "Não Iniciado":
                colunaNaoIniciado.getChildren().add(card);
                break;
            case "Em Progresso":
                colunaEmProgresso.getChildren().add(card);
                break;
            case "Concluído":
                colunaConcluido.getChildren().add(card);
                break;
            default: // Caso de fallback
                System.err.println("Status inesperado: " + status + ". Colocando em Não Iniciado.");
                colunaNaoIniciado.getChildren().add(card);
                break;
        }
    }

    /**
     * Cria um card visual para um objetivo padrão (visão do colaborador).
     */
    private Node criarCardObjetivoPadrao(Objetivo objetivo) {
        VBox card = new VBox(5);
        card.getStyleClass().add("objetivo-card");

        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");

        // --- CORREÇÃO AQUI ---
        // Garante que está chamando formatarData
        Label prazoLabel = new Label("Prazo: " + formatarData(objetivo.getPrazo()));
        // --- FIM DA CORREÇÃO ---
        prazoLabel.getStyleClass().add("objetivo-card-prazo");

        card.getChildren().addAll(descricaoLabel, prazoLabel);
        adicionarAcaoClique(card, objetivo); // Adiciona ação de clique
        return card;
    }

    /**
     * Cria um card visual para um objetivo na visão do RH (com mais informações).
     */
    private Node criarCardObjetivoRH(ObjetivoComPDI objetivo) {
        VBox card = new VBox(5);
        card.getStyleClass().add("objetivo-card");

        Label infoColaborador = new Label(objetivo.getNomeUsuario() + " (PDI: " + objetivo.getPdiIdOriginal() + ")");
        infoColaborador.getStyleClass().add("objetivo-card-info-rh"); // Pode ter estilo CSS

        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");

        // --- CORREÇÃO AQUI ---
        // Garante que está chamando formatarData
        Label prazoLabel = new Label("Prazo: " + formatarData(objetivo.getPrazo()));
        // --- FIM DA CORREÇÃO ---
        prazoLabel.getStyleClass().add("objetivo-card-prazo");

        card.getChildren().addAll(infoColaborador, descricaoLabel, prazoLabel);
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
                    if(Util.class != null) Util.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Dados incompletos para avaliação.");
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
            if (Util.class != null) { Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível carregar a tela de avaliação (AvaliacaoModal.fxml): " + e.getMessage()); }
        } catch (Exception e) { // Outros erros
            e.printStackTrace();
            if (Util.class != null) { Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao tentar abrir a tela de avaliação: " + e.getMessage()); }
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
        }
        catch (Exception e) { // Captura outros erros de formatação
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