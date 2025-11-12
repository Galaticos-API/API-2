package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import gui.modal.AvaliacaoObjetivoModalController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
// IMPORTS CORRIGIDOS E FALTANTES
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI; // Pode ser removido se 'popularDetalhesPDI' for removido
import modelo.Usuario;
import util.Util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

// IMPORTS APACHE POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// FIM IMPORTS APACHE POI

public class ObjetivosController {

    // --- Componentes FXML ---
    @FXML
    private Label lblTituloPDI;

    // Container Kanban
    @FXML
    private HBox kanbanContainer;
    @FXML
    private VBox colunaNaoIniciado;
    @FXML
    private VBox colunaEmProgresso;
    @FXML
    private VBox colunaConcluido;

    // NOVO: Botão de exportar (Adicionado no FXML)
    @FXML
    private Button btnExportarExcel;

    // Container Detalhes PDI
    @FXML private VBox pdiDetalhesContainer;
    @FXML private Label lblPdiStatus;
    @FXML private Label lblPdiId;
    @FXML private Label lblPdiDataCriacao;
    @FXML private Label lblPdiDataFechamento;
    @FXML private ProgressBar progressBarGeral;
    @FXML private Text textPontuacaoGeral;
    @FXML private Label lblSemPdi;
    @FXML private VBox colunaNaoIniciadoUser;
    @FXML private VBox colunaEmProgressoUser;
    @FXML private VBox colunaConcluidoUser;


    // --- DAOs e Variáveis ---
    private Usuario usuarioLogado;
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    // Corrigido: Adicionado o ponto (.)
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // NOVO: Variável para armazenar a lista completa de objetivos carregada
    private ObservableList<ObjetivoComPDI> listaCompletaDeObjetivos = FXCollections.observableArrayList();


    /**
     * Chamado pelo MainController para passar o usuário logado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTela();
    }

    /**
     * Configura a tela.
     */
    private void configurarTela() {
        if (usuarioLogado == null) return;

        limparConteudos();

        kanbanContainer.setVisible(true);
        kanbanContainer.setManaged(true);
        pdiDetalhesContainer.setVisible(false);
        pdiDetalhesContainer.setManaged(false);

        try {
            String tipoUsuario = usuarioLogado.getTipo_usuario();
            List<ObjetivoComPDI> objetivosParaMostrar;

            switch (tipoUsuario) {
                case "RH":
                    lblTituloPDI.setText("Gerenciar Objetivos (Visão RH)");
                    objetivosParaMostrar = objetivoDAO.listarTodosComPDI(usuarioLogado.getId());
                    break;

                case "Gestor Geral":
                    lblTituloPDI.setText("Objetivos (Visão Geral de Setores)");
                    objetivosParaMostrar = objetivoDAO.listarTodosComPDI(usuarioLogado.getId());
                    break;

                case "Gestor de Area":
                    lblTituloPDI.setText("Objetivos (Colaboradores da sua Área)");
                    String setorDoGestor = usuarioLogado.getSetor_id();
                    if (setorDoGestor != null && !setorDoGestor.isEmpty()) {
                        objetivosParaMostrar = objetivoDAO.listarPorSetor(setorDoGestor, usuarioLogado.getId());
                    } else {
                        System.err.println("AVISO: Gestor de Área (ID: " + usuarioLogado.getId() + ") não possui um setor_id definido.");
                        objetivosParaMostrar = Collections.emptyList();
                    }
                    break;

                default:
                    lblTituloPDI.setText("Acesso não autorizado");
                    objetivosParaMostrar = Collections.emptyList();
                    Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Permissão", "Você não tem acesso a esta tela.");
                    break;
            }

            // ATUALIZA A LISTA COMPLETA
            listaCompletaDeObjetivos.clear();
            listaCompletaDeObjetivos.addAll(objetivosParaMostrar);

            // Popula o Kanban
            for (ObjetivoComPDI obj : listaCompletaDeObjetivos) {
                Node cardObjetivo = criarCardObjetivoRH(obj);
                distribuirCard(cardObjetivo, obj.getStatus());
            }

            // Adiciona placeholders
            adicionarPlaceholderSeVazio(colunaNaoIniciado);
            adicionarPlaceholderSeVazio(colunaEmProgresso);
            adicionarPlaceholderSeVazio(colunaConcluido);

        } catch (RuntimeException e) {
            e.printStackTrace();
            lblTituloPDI.setText("Erro ao carregar dados");
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados: " + e.getMessage());
            limparConteudos();
        }
    }

    // --- MÉTODOS DE EXPORTAÇÃO (NOVO) ---

    /**
     * Método acionado pelo atributo onAction="#handleExportarExcel" no FXML
     */
    @FXML
    private void handleExportarExcel(ActionEvent event) {
        if (listaCompletaDeObjetivos != null) {
            exportarParaExcel(listaCompletaDeObjetivos);
        } else {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "A lista de objetivos não foi carregada para exportação.");
        }
    }

    /**
     * Lógica principal para exportar os dados para um arquivo Excel (XLSX).
     */
    private void exportarParaExcel(ObservableList<ObjetivoComPDI> dados) {
        if (dados.isEmpty()) {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não há dados para exportar.");
            return;
        }

        // 1. Configurar FileChooser
        Stage stage = (Stage) lblTituloPDI.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Objetivos");
        fileChooser.setInitialFileName("Relatorio_Objetivos.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // 2. Criar e preencher o Workbook
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Dados de Objetivos");

                escreverCabecalho(sheet);
                escreverDados(sheet, dados);

                // 3. Salvar o arquivo
                workbook.write(fileOut);

                // 4. Auto-ajustar a largura das colunas e fechar
                autoSizeColunas(sheet);
                workbook.close();

                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados exportados com sucesso para:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void escreverCabecalho(Sheet sheet) {
        // Campos de Objetivo + campos extras de ObjetivoComPDI (Nome Colaborador)
        String[] headers = {"ID", "PDI ID", "COLABORADOR", "DESCRIÇÃO", "PRAZO", "STATUS", "COMENTÁRIOS", "PESO", "PONTUAÇÃO"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void escreverDados(Sheet sheet, ObservableList<ObjetivoComPDI> dados) {
        int rowNum = 1; // Começa na linha 1 (abaixo do cabeçalho)

        for (ObjetivoComPDI objetivo : dados) {
            Row row = sheet.createRow(rowNum++);

            // Preenchendo as células na ordem das colunas
            row.createCell(0).setCellValue(objetivo.getId());
            row.createCell(1).setCellValue(objetivo.getPdiId());
            row.createCell(2).setCellValue(objetivo.getNomeUsuario()); // Extra de ObjetivoComPDI
            row.createCell(3).setCellValue(objetivo.getDescricao());
            // O getPrazoString() é herdado de Objetivo e está na sua classe modelo
            row.createCell(4).setCellValue(objetivo.getPrazoString());
            row.createCell(5).setCellValue(objetivo.getStatus());
            row.createCell(6).setCellValue(objetivo.getComentarios());
            row.createCell(7).setCellValue(objetivo.getPeso());
            row.createCell(8).setCellValue(objetivo.getPontuacao());
        }
    }

    private void autoSizeColunas(Sheet sheet) {
        if (sheet.getRow(0) != null) {
            int numCols = sheet.getRow(0).getLastCellNum();
            for(int i = 0; i < numCols; i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    // --- FIM DOS MÉTODOS DE EXPORTAÇÃO ---

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


    /**
     * Cria um card visual para um objetivo na visão do RH (com mais informações).
     * Este card será usado por todos os gestores nesta tela.
     */
    private Node criarCardObjetivoRH(ObjetivoComPDI objetivo) {
        VBox card = new VBox();
        card.getStyleClass().add("objetivo-mini-card");

        // Informações extras (Colaborador e PDI ID)
        Label infoColaborador = new Label("Colaborador: " + objetivo.getNomeUsuario());
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

    private void adicionarAcaoClique(Node card, Objetivo objetivo) {
        ContextMenu contextMenu = new ContextMenu();
        Menu mudarStatusMenu = new Menu("Mudar status");
        MenuItem naoIniciadoItem = new MenuItem("Não Iniciado");
        naoIniciadoItem.setOnAction(e -> handleChangeStatus(objetivo, "Não Iniciado"));
        MenuItem emProgressoItem = new MenuItem("Em Progresso");
        emProgressoItem.setOnAction(e -> handleChangeStatus(objetivo, "Em Progresso"));

        mudarStatusMenu.getItems().addAll(naoIniciadoItem, emProgressoItem);

        MenuItem avaliarItem = new MenuItem("Avaliar");
        avaliarItem.setOnAction(e -> {
            if (objetivo instanceof ObjetivoComPDI) {
                handleAbrirModalAvaliacao((ObjetivoComPDI) objetivo);
            } else {
                System.err.println("Erro: Tentativa de avaliar objetivo sem dados completos de PDI/Usuário. Objetivo ID: " + objetivo.getId());
                Util.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Dados incompletos para avaliação.");
            }
        });
        avaliarItem.setDisable(!"Em Progresso".equals(objetivo.getStatus()));

        contextMenu.getItems().addAll(mudarStatusMenu, avaliarItem);

        card.setOnContextMenuRequested(event -> {
            String tipoUsuario = usuarioLogado.getTipo_usuario();
            if ("RH".equals(tipoUsuario) || "Gestor Geral".equals(tipoUsuario) || "Gestor de Area".equals(tipoUsuario)) {
                avaliarItem.setDisable(!"Em Progresso".equals(objetivo.getStatus()));
                contextMenu.show(card, event.getScreenX(), event.getScreenY());
            }
        });

        card.getStyleClass().add("clickable-card");
    }

    private void handleChangeStatus(Objetivo objetivo, String novoStatus) {
        try {
            objetivo.setStatus(novoStatus);
            objetivoDAO.atualizar(objetivo);
            configurarTela();
            PdiDAO.atualizarPontuacaoGeral(objetivo.getPdiId());
        } catch (Exception e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível alterar o status do objetivo.");
        }
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

    // --- MÉTODOS NÃO MAIS UTILIZADOS (CORRIGIDOS) ---

    private void popularDetalhesPDI(PDI pdi) { /* ... */ }
    private Node criarCardObjetivoPadrao(Objetivo objetivo) { /* ... */ return null; } // Adicionado 'return null'

    /**
     * [CORRIGIDO]
     * Formata um objeto java.util.Date para String dd/MM/yyyy.
     */
    private String formatarData(Date data) {
        if (data == null) {
            return "N/A";
        }
        try {
            LocalDate localDate;
            if (data instanceof java.sql.Date) {
                localDate = ((java.sql.Date) data).toLocalDate();
            } else {
                // A conversão de java.util.Date para LocalDate é complexa,
                // mas dado que você usa java.sql.Date, este bloco é menos provável de ser usado.
                // Se o seu DAO retorna java.util.Date, você precisará adaptar a chamada.
                // Por segurança, vamos usar o toInstant() se for um java.util.Date genérico:
                localDate = data.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            return localDate.format(FORMATADOR_DATA);
        } catch (Exception e) {
            System.err.println("Erro ao formatar data (" + data.getClass().getName() + "): " + e.getMessage());
            return "Inválida"; // Garante o retorno em caso de exceção
        }
    }
}