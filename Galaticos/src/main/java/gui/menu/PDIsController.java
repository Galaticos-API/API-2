package gui.menu;

import dao.PdiDAO;
import dao.UsuarioDAO;
import gui.modal.CadastroPdiModalController;
import gui.modal.EditarPDIModalController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ListaPdiController implements Initializable {

    // --- Componentes FXML ---
    @FXML
    private TextField usuarioIdTextField;
    @FXML
    private Button buscarButton;
    @FXML
    private Button limparFiltroButton;
    @FXML
    private Button cadastrarPdiButton;
    @FXML
    private HBox filterBar;
    @FXML
    private HBox kanbanBoardPdi; // HBox principal do Kanban
    // Colunas do Kanban
    @FXML
    private VBox colunaEmAndamento;
    @FXML
    private VBox colunaConcluido;
    @FXML
    private VBox colunaArquivado;

    // --- DAOs, Cache e Variáveis ---
    private PdiDAO pdiDAO;
    private UsuarioDAO usuarioDAO;
    private Map<Integer, String> mapaNomesUsuarios;
    private Usuario usuarioLogado;
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.pdiDAO = new PdiDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.mapaNomesUsuarios = new HashMap<>();
        carregarMapaDeUsuarios();
        // Carga inicial será feita pelo setUsuarioLogado
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTelaParaUsuario();
    }

    private void configurarTelaParaUsuario() {
        if (usuarioLogado == null) {
            exibirAlerta("Erro Crítico", "Usuário não definido.");
            return;
        }

        carregarEPopularPDIs(pdiDAO.lerTodos()); // Carrega todos
    }

    private void carregarMapaDeUsuarios() {
        try {
            List<Usuario> todosOsUsuarios = usuarioDAO.lerTodos();
            for (Usuario u : todosOsUsuarios) {
                mapaNomesUsuarios.put(Integer.parseInt(u.getId()), u.getNome());
            }
        } catch (RuntimeException | SQLException e) {
            e.printStackTrace();
            exibirAlerta("Erro Crítico", "Não foi possível carregar a lista de usuários.");
        }
    }

    /**
     * Limpa as colunas e as preenche com os cards de PDI fornecidos.
     */
    private void popularKanbanBoard(List<PDI> pdis) {
        // Limpa colunas
        colunaEmAndamento.getChildren().clear();
        colunaConcluido.getChildren().clear();
        colunaArquivado.getChildren().clear();

        if (pdis == null || pdis.isEmpty()) {
            // Adiciona placeholders se a lista estiver vazia
            adicionarPlaceholderSeVazio(colunaEmAndamento);
            adicionarPlaceholderSeVazio(colunaConcluido);
            adicionarPlaceholderSeVazio(colunaArquivado);
            return;
        }

        // Distribui os cards
        for (PDI pdi : pdis) {
            VBox card = createPdiCard(pdi);
            switch (pdi.getStatus()) {
                case "Em Andamento":
                    colunaEmAndamento.getChildren().add(card);
                    break;
                case "Concluído":
                    colunaConcluido.getChildren().add(card);
                    break;
                case "Arquivado":
                    colunaArquivado.getChildren().add(card);
                    break;
                default:
                    // Opcional: Lidar com status inesperado
                    System.err.println("Status de PDI desconhecido: " + pdi.getStatus());
                    break;
            }
        }

        // Adiciona placeholders às colunas que ficaram vazias após a distribuição
        adicionarPlaceholderSeVazio(colunaEmAndamento);
        adicionarPlaceholderSeVazio(colunaConcluido);
        adicionarPlaceholderSeVazio(colunaArquivado);
    }

    /**
     * Busca os PDIs no DAO e chama o método para popular o Kanban.
     */
    private void carregarEPopularPDIs(List<PDI> pdis) {
        if (mapaNomesUsuarios.isEmpty()) {
            carregarMapaDeUsuarios(); // Garante que os nomes estão prontos
        }
        try {
            popularKanbanBoard(pdis);
        } catch (RuntimeException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível carregar os PDIs.");
            popularKanbanBoard(Collections.emptyList()); // Limpa a tela em caso de erro
        }
    }


    /**
     * Cria o card visual para um PDI. (Lógica do card em si permanece a mesma)
     */
    private VBox createPdiCard(PDI pdi) {
        VBox card = new VBox(10);
        card.getStyleClass().add("pdi-card"); // Usará o mesmo estilo de card
        // card.setPrefWidth(270); // Não precisa mais de largura fixa no Kanban

        Label anoLabel = new Label("PDI " + pdi.getId()); // Usa o Ano do PDI
        anoLabel.getStyleClass().add("pdi-card-title");

        String nome = mapaNomesUsuarios.getOrDefault(pdi.getColaboradorId(), "Colaborador: " + pdi.getNomeColaborador());
        Label nomeLabel = new Label(nome);
        nomeLabel.getStyleClass().add("pdi-card-subtitle");

        // Status não é mais necessário no card, pois ele está implícito pela coluna
        // Label statusLabel = new Label(pdi.getStatus());
        // statusLabel.getStyleClass().addAll("pdi-card-status", getStatusStyleClass(pdi.getStatus()));

        ProgressBar progressBar = new ProgressBar(pdi.getPontuacaoGeral());
        progressBar.setMaxWidth(Double.MAX_VALUE);
        Label progressLabel = new Label(String.format("%.1f%%", pdi.getPontuacaoGeral() * 100));
        progressLabel.getStyleClass().add("pdi-card-progress-text");
        HBox progressBox = new HBox(5, progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        String dataFormatada = (pdi.getDataCriacao() != null) ? pdi.getDataCriacao() : "N/A";
        Label dataLabel = new Label("Criado em: " + dataFormatada);
        dataLabel.getStyleClass().add("pdi-card-date");

        // Adiciona os elementos (sem o statusLabel)
        card.getChildren().addAll(anoLabel, nomeLabel, progressBox, dataLabel);

        // Menu de Contexto e Clique (Lógica mantida)
        boolean isRh = usuarioLogado != null && "RH".equals(usuarioLogado.getTipo_usuario());
        if (isRh) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editarItem = new MenuItem("Editar PDI");
            MenuItem excluirItem = new MenuItem("Apagar PDI");
            editarItem.setOnAction(event -> handleEditarPdi(pdi));
            excluirItem.setOnAction(event -> confirmarEExcluirPDI(pdi));
            contextMenu.getItems().addAll(editarItem, excluirItem);
            card.setOnContextMenuRequested(event -> contextMenu.show(card, event.getScreenX(), event.getScreenY()));
        }

        card.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                handleEditarPdi(pdi);
            }
        });
        card.getStyleClass().add("clickable-card");

        return card;
    }

    /**
     * Ação do botão Buscar.
     */
    @FXML
    void handleBuscarAction(ActionEvent event) {
        String idTexto = usuarioIdTextField.getText();
        if (idTexto == null || idTexto.trim().isEmpty()) {
            exibirAlerta("Erro", "O campo de ID não pode estar vazio.");
            return;
        }
        try {
            List<PDI> pdisFiltrados = Collections.singletonList(pdiDAO.buscarPorColaborador(idTexto));
            carregarEPopularPDIs(pdisFiltrados); // Chama o método unificado
        } catch (NumberFormatException e) {
            exibirAlerta("Erro de Formato", "Por favor, insira um número de ID válido.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível buscar os PDIs.");
        }
    }

    /**
     * Ação do botão Limpar Filtro/Mostrar Todos.
     */
    @FXML
    void handleLimparFiltroAction(ActionEvent event) {
        usuarioIdTextField.clear();
        configurarTelaParaUsuario(); // Recarrega a visão completa
    }

    /**
     * Adiciona placeholder se a coluna estiver vazia.
     */
    private void adicionarPlaceholderSeVazio(VBox coluna) {
        if (coluna.getChildren().isEmpty()) {
            Label placeholder = new Label("Nenhum PDI nesta etapa.");
            placeholder.getStyleClass().add("kanban-empty-placeholder");
            placeholder.setMaxWidth(Double.MAX_VALUE);
            placeholder.setAlignment(Pos.CENTER);
            coluna.setAlignment(Pos.CENTER);
            coluna.getChildren().add(placeholder);
        } else {
            coluna.setAlignment(Pos.TOP_LEFT); // Ou TOP_CENTER
        }
    }

    @FXML
    private void handleAbrirModalCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroPdiModal.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar novo PDI");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(cadastrarPdiButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            CadastroPdiModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (controller.isSalvo()) {
                configurarTelaParaUsuario(); // Recarrega a visão correta
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmarEExcluirPDI(PDI pdi) {
        String nome = mapaNomesUsuarios.get(pdi.getColaboradorId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir PDI de " + nome);
        alert.setContentText("Você tem certeza que deseja excluir o PDI (ID: " + pdi.getId() + ")? Esta ação não pode ser desfeita.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean sucesso = pdiDAO.deletar(pdi.getId());
            if (sucesso) {
                configurarTelaParaUsuario(); // Recarrega a visão correta
                exibirAlerta("Sucesso", "PDI excluído com sucesso.");
            } else {
                exibirAlerta("Erro", "Ocorreu um erro ao excluir o PDI.");
            }
        }
    }

    private void handleEditarPdi(PDI pdiSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarPDIModal.fxml"));
            Parent page = loader.load();
            EditarPDIModalController controller = loader.getController();
            controller.setPDI(pdiSelecionado);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar informações do PDI");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(cadastrarPdiButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            handleLimparFiltroAction(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

}