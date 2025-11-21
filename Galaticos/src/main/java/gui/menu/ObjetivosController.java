package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import gui.modal.AvaliacaoObjetivoModalController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI;
import modelo.Usuario;
import util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    // --- MÉTODOS DE EXPORTAÇÃO EXCEL ---

    /**
     * Retorna a lista de Objetivos que está sendo exibida na tela.
     */
    private Optional<List<ObjetivoComPDI>> getObjetivosParaExportar() {
        // Usa a lista já carregada
        if (listaCompletaDeObjetivos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(listaCompletaDeObjetivos);
    }

    /**
     * Manipulador do botão de exportação. Vinculado ao FXML.
     */
    @FXML
    private void handleExportarExcel() {
        List<ObjetivoComPDI> objetivos = getObjetivosParaExportar().orElse(Collections.emptyList());

        if (objetivos.isEmpty()) {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Nenhum objetivo encontrado para exportar.");
            return;
        }

        // 1. Configurar o FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Objetivos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Objetivos_Relatorio_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

        // Abre a janela de diálogo
        File arquivo = fileChooser.showSaveDialog(lblTituloPDI.getScene().getWindow());

        if (arquivo != null) {
            if (exportarParaExcel(objetivos, arquivo)) {
                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados exportados com sucesso para:\n" + arquivo.getAbsolutePath());
            } else {
                // O erro específico é tratado dentro de exportarParaExcel,
                // mas a mensagem genérica aqui cobre falhas de IO.
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar o arquivo Excel.");
            }
        }
    }

    /**
     * Cria e salva o arquivo Excel usando Apache POI.
     */
    private boolean exportarParaExcel(List<ObjetivoComPDI> objetivos, File arquivo) {
        // Usa XSSFWorkbook para o formato .xlsx
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(arquivo)) {

            Sheet sheet = workbook.createSheet("Relatório de Objetivos");

            // 1. Criar o Cabeçalho
            String[] colunas = {"ID", "PDI ID", "Status", "Descrição", "Prazo", "Peso (f)", "Colaborador"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
            }

            // 2. Preencher as Linhas de Dados
            int rowNum = 1;
            for (ObjetivoComPDI obj : objetivos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(obj.getId());
                row.createCell(1).setCellValue(obj.getPdiId());
                row.createCell(2).setCellValue(obj.getStatus());
                row.createCell(3).setCellValue(obj.getDescricao());

                // CORREÇÃO: Usa o método formatarData(Date data) para converter o prazo
                // para String no formato "dd/MM/yyyy", impedindo a conversão para número serial.
                // Assumindo que obj.getPrazo() retorna java.sql.Date ou java.util.Date
                row.createCell(4).setCellValue(formatarData((Date) obj.getPrazo()));

                row.createCell(5).setCellValue(obj.getPeso());
                row.createCell(6).setCellValue(obj.getNomeUsuario());
            }

            // 3. Auto-ajustar colunas
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. Escrever o arquivo
            workbook.write(fileOut);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassCastException e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Dados", "O campo 'Prazo' não pôde ser formatado. Certifique-se de que getPrazo() retorna um tipo de Data válido (java.util.Date ou java.sql.Date).");
            e.printStackTrace();
            return false;
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

        MenuItem avaliarItem = new MenuItem("Finalizar");
        avaliarItem.setOnAction(e -> {
            if (objetivo instanceof ObjetivoComPDI) {
                handleAbrirModalAvaliacao((ObjetivoComPDI) objetivo);
            } else {
                System.err.println("Erro: Tentativa de finalizar objetivo sem dados completos de PDI/Usuário. Objetivo ID: " + objetivo.getId());
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

    // --- MÉTODOS NÃO MAIS UTILIZADOS (DEIXADO PARA REFERÊNCIA) ---

    /**
     * [NÃO UTILIZADO NESTA TELA]
     * Popula os Labels e ProgressBar com os detalhes do PDI.
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
     * [NÃO UTILIZADO NESTA TELA]
     * Cria um card visual para um objetivo padrão (visão do colaborador).
     */
    private Node criarCardObjetivoPadrao(Objetivo objetivo) {
        VBox card = new VBox();
        card.getStyleClass().add("objetivo-mini-card");

        Label descricaoLabel = new Label(objetivo.getDescricao());
        descricaoLabel.setWrapText(true);
        descricaoLabel.getStyleClass().add("objetivo-card-descricao");
        VBox.setMargin(descricaoLabel, new Insets(10, 10, 10, 10));

        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 10, 0, 10));

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
        card.getChildren().addAll(descricaoLabel, separator, detailsBox);

        return card;
    }


    /**
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