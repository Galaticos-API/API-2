package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO; // Importar PdiDAO
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modelo.Objetivo;
import modelo.ObjetivoComPDI; // Importar novo modelo
import modelo.PDI;
import modelo.Usuario; // Importar Usuario

import java.sql.Date; // Usar java.sql.Date se for o tipo retornado pelo DAO
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

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
    public void setUsuarioLogado(Usuario usuario) {
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
        }
    }

    /**
     * Adiciona um card à coluna Kanban correta com base no status.
     */
    private void distribuirCard(Node card, String status) {
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

        Label prazoLabel = new Label("Prazo: " + objetivo.getPrazo());
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

        // Informações extras para RH
        Label infoColaborador = new Label(objetivo.getNomeUsuario() + " (PDI: " + objetivo.getPdiIdOriginal() + ")");
        infoColaborador.getStyleClass().add("objetivo-card-info-rh");

        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");

        Label prazoLabel = new Label("Prazo: " + objetivo.getPrazo());
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
            System.out.println("Clicou no objetivo ID: " + objetivo.getId() + " do PDI ID: " + objetivo.getPdiId());
            // handleEditarObjetivo(objetivo); // Chamar modal de edição
        });
    }

    /**
     * Formata um objeto java.sql.Date para String dd/MM/yyyy.
     * Lida com a conversão correta.
     */
    private String formatarData(Date prazoSqlDate) {
        if (prazoSqlDate == null) {
            return "Sem prazo";
        }
        try {
            // Converte java.sql.Date para LocalDate
            LocalDate prazoLocalDate = prazoSqlDate.toLocalDate();
            return FORMATADOR_DATA.format(prazoLocalDate);
        } catch (Exception e) {
            // Fallback caso a conversão falhe (pouco provável com java.sql.Date)
            return "Data inválida";
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
            coluna.setAlignment(Pos.TOP_LEFT); // Ou TOP_CENTER
        }
    }

    // ... (Método handleAdicionarObjetivo, handleEditarObjetivo, etc. se necessário)
}