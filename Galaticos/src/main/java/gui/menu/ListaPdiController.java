package gui.menu;

import dao.PdiDAO;
import dao.UsuarioDAO;
import gui.modal.CadastroPdiModalController;
import gui.modal.EditarPDIModalController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox; // <<< Importar HBox
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
    private FlowPane pdiFlowPane;
    @FXML
    private HBox filterBar; // <<< Adicionar @FXML para a barra de filtro

    // --- DAOs, Cache e Variável de Usuário Logado ---
    private PdiDAO pdiDAO;
    private UsuarioDAO usuarioDAO;
    private Map<Integer, String> mapaNomesUsuarios;
    private Usuario usuarioLogado; // <<< Adicionar variável para o usuário logado

    // Formatador de data
    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.pdiDAO = new PdiDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.mapaNomesUsuarios = new HashMap<>();

        // Carrega o cache de nomes (essencial para a visão RH)
        carregarMapaDeUsuarios();

        // A carga inicial de PDIs será feita após receber o usuário logado
        // remover a chamada carregarTodosOsPDIs() daqui
    }

    /**
     * MÉTODO NOVO: Chamado pelo MainController para passar o usuário logado.
     * Este método agora inicia o carregamento dos PDIs corretos.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTelaParaUsuario(); // Chama a configuração baseada no usuário
    }

    /**
     * Configura a visibilidade dos componentes e carrega os PDIs
     * de acordo com o tipo de usuário logado.
     */
    private void configurarTelaParaUsuario() {
        if (usuarioLogado == null) {
            exibirAlerta("Erro Crítico", "Usuário não definido para a tela de PDIs.");
            return;
        }

        carregarTodosOsPDIs();
    }

    /**
     * Carrega o mapa de nomes de usuários.
     */
    private void carregarMapaDeUsuarios() {
        // ... (código sem alterações)
        try {
            List<Usuario> todosOsUsuarios = usuarioDAO.lerTodos();
            for (Usuario u : todosOsUsuarios) {
                mapaNomesUsuarios.put(Integer.parseInt(u.getId()), u.getNome());
            }
        } catch (RuntimeException | SQLException e) { // Captura SQLException também
            e.printStackTrace();
            exibirAlerta("Erro Crítico", "Não foi possível carregar a lista de usuários.");
        }
    }

    /**
     * Limpa e preenche o FlowPane com os cards de PDI.
     */
    private void popularFlowPane(List<PDI> pdis) {
        pdiFlowPane.getChildren().clear();
        if (pdis == null || pdis.isEmpty()) {
            // Adiciona um placeholder se não houver PDIs
            Label placeholder = new Label("Nenhum PDI encontrado.");
            placeholder.getStyleClass().add("kanban-empty-placeholder"); // Reutiliza estilo do Kanban
            placeholder.setMaxWidth(Double.MAX_VALUE);
            placeholder.setAlignment(Pos.CENTER);
            pdiFlowPane.setAlignment(Pos.CENTER); // Centraliza o placeholder
            pdiFlowPane.getChildren().add(placeholder);
        } else {
            pdiFlowPane.setAlignment(Pos.TOP_LEFT); // Alinhamento normal para os cards
            for (PDI pdi : pdis) {
                VBox card = createPdiCard(pdi);
                pdiFlowPane.getChildren().add(card);
            }
        }
    }

    /**
     * Cria o card visual para um PDI.
     */
    private VBox createPdiCard(PDI pdi) {
        // ... (código do createPdiCard sem alterações)
        VBox card = new VBox(10);
        card.getStyleClass().add("pdi-card");
        card.setPrefWidth(270);

        Label anoLabel = new Label("PDI " + pdi.getId()); // Usa o Ano do PDI
        anoLabel.getStyleClass().add("pdi-card-title");

        String nome = mapaNomesUsuarios.getOrDefault(pdi.getColaboradorId(), "Usuário ID: " + pdi.getColaboradorId());
        Label nomeLabel = new Label(nome);
        nomeLabel.getStyleClass().add("pdi-card-subtitle");

        Label statusLabel = new Label(pdi.getStatus());
        statusLabel.getStyleClass().addAll("pdi-card-status", getStatusStyleClass(pdi.getStatus()));

        ProgressBar progressBar = new ProgressBar(pdi.getPontuacaoGeral());
        progressBar.setMaxWidth(Double.MAX_VALUE);
        Label progressLabel = new Label(String.format("%.1f%%", pdi.getPontuacaoGeral() * 100));
        progressLabel.getStyleClass().add("pdi-card-progress-text");
        HBox progressBox = new HBox(5, progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        String dataFormatada = (pdi.getDataCriacao() != null) ?
                FORMATADOR_DATA.format(pdi.getDataCriacao().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) : "N/A";
        Label dataLabel = new Label("Criado em: " + dataFormatada);
        dataLabel.getStyleClass().add("pdi-card-date");

        card.getChildren().addAll(anoLabel, nomeLabel, statusLabel, progressBox, dataLabel);

        // Menu de Contexto (só permite editar/excluir se for RH?) - AJUSTE SE NECESSÁRIO
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

        // Abrir ao clicar com o botão esquerdo (pode ser para todos)
        card.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                handleEditarPdi(pdi); // Ou talvez abrir uma visão de "detalhes" apenas
            }
        });
        card.getStyleClass().add("clickable-card");

        return card;
    }

    /**
     * Carrega todos os PDIs (Visão RH).
     */
    private void carregarTodosOsPDIs() {
        if (mapaNomesUsuarios.isEmpty()) {
            carregarMapaDeUsuarios();
        }
        try {
            List<PDI> pdis = pdiDAO.lerTodos();
            popularFlowPane(pdis);
        } catch (RuntimeException e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível carregar os PDIs.");
            popularFlowPane(Collections.emptyList()); // Limpa a tela em caso de erro
        }
    }


    /**
     * Ação do botão Buscar (apenas para RH).
     */
    @FXML
    void handleBuscarAction(ActionEvent event) {
        String idTexto = usuarioIdTextField.getText();
        if (idTexto == null || idTexto.trim().isEmpty()) {
            exibirAlerta("Erro", "O campo de ID não pode estar vazio.");
            return;
        }
        try {
            // Corrige a busca: buscarPorColaborador retorna LISTA
            PDI pdisFiltrados = pdiDAO.buscarPorColaborador(idTexto);
            if (pdisFiltrados == null) {
                exibirAlerta("Informação", "Nenhum PDI encontrado para o ID: " + idTexto);
            }
            popularFlowPane((List<PDI>) pdisFiltrados);
        } catch (NumberFormatException e) {
            exibirAlerta("Erro de Formato", "Por favor, insira um número de ID válido.");
        } catch (RuntimeException e) { // Captura erros do DAO
            e.printStackTrace();
            exibirAlerta("Erro", "Não foi possível buscar os PDIs.");
        }
    }

    /**
     * Ação do botão Limpar Filtro/Mostrar Todos.
     * Agora recarrega a visão correta (todos para RH, do usuário para outros).
     */
    @FXML
    void handleLimparFiltroAction(ActionEvent event) {
        usuarioIdTextField.clear();
        // Recarrega a visualização correta
        configurarTelaParaUsuario();
    }

    /**
     * Abre o modal de cadastro de PDI (apenas para RH).
     */
    @FXML
    private void handleAbrirModalCadastro() {
        // ... (código sem alterações, mas a visibilidade do botão já controla o acesso)
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

    /**
     * Confirma e exclui um PDI (apenas para RH).
     */
    private void confirmarEExcluirPDI(PDI pdi) {
        // ... (código sem alterações, mas o menu de contexto já controla o acesso)
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

    // --- Métodos auxiliares (sem alterações) ---
    private String getStatusStyleClass(String status) {
        if (status == null) return "status-arquivado";
        switch (status) {
            case "Em Andamento":
                return "status-andamento";
            case "Concluído":
                return "status-concluido";
            case "Arquivado":
            default:
                return "status-arquivado";
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

            // Atualiza a lista após fechar o modal
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