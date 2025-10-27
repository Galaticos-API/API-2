package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import modelo.Usuario;
import util.Session;
import util.StageManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public Label logadoComo;
    public Label funcaoUsuario;
    private Usuario usuarioLogado;

    @FXML
    private StackPane conteudo;

    @FXML
    private HBox navigationBar;
    @FXML
    public Button btnObjetivos;
    @FXML
    private Button btnPerfil;
    @FXML
    private Button btnPDI;
    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnDashboard;
    @FXML
    private Separator rhSeparator;
    private List<Button> navigationButtons;

    /**
     * Ponto de entrada principal. Chamado após o login para injetar o usuário.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());

        // Chama a lógica de configuração da UI baseada no usuário
        configurarPermissoes();
    }

    /**
     * Chamado pelo FXML Loader. Executa ANTES de setUsuario().
     * Coloque aqui apenas inicializações que não dependem do usuário.
     */
    @FXML
    public void initialize() {
        // Apenas mapeia a lista de botões para o método updateActiveButton()
        navigationButtons = Arrays.asList(btnObjetivos, btnPerfil, btnPDI, btnUsuarios, btnDashboard);

        // A lógica de permissões foi movida para configurarPermissoes()
        // para garantir que 'usuarioLogado' não seja nulo.
    }

    /**
     * Configura a visibilidade, texto e ordem dos botões
     * com base no tipo de usuário logado.
     */
    private void configurarPermissoes() {
        if (usuarioLogado == null) return;

        String tipoUsuario = usuarioLogado.getTipo_usuario();

        // --- 1. Estado Padrão (O mais restrito: Colaborador) ---
        // Todos veem "Meu Perfil"
        btnPerfil.setVisible(true);
        btnPerfil.setManaged(true);

        // Colaborador vê "Meu PDI"
        btnPDI.setVisible(true);
        btnPDI.setManaged(true);
        btnPDI.setText("Meu PDI"); // O handler 'handleMenuPDI' já sabe carregar a tela certa

        // O resto começa escondido
        btnObjetivos.setVisible(false);
        btnObjetivos.setManaged(false);
        btnDashboard.setVisible(false);
        btnDashboard.setManaged(false);
        btnUsuarios.setVisible(false);
        btnUsuarios.setManaged(false);
        rhSeparator.setVisible(false);
        rhSeparator.setManaged(false);

        // Define a página inicial padrão
        Runnable paginaInicial = this::handleMenuPDI; // Padrão: Colaborador começa em "Meu PDI"

        // --- 2. Aplica permissões adicionais ---
        switch (tipoUsuario) {
            case "RH":
                // Mostrar tudo
                btnPDI.setText("Gerenciar PDIs"); // Texto original do FXML
                btnDashboard.setVisible(true);
                btnDashboard.setManaged(true);
                btnUsuarios.setVisible(true);
                btnUsuarios.setManaged(true);
                rhSeparator.setVisible(true);
                rhSeparator.setManaged(true);

                // Botão Objetivos
                btnObjetivos.setVisible(true);
                btnObjetivos.setManaged(true);
                btnObjetivos.setText("Gerenciar Objetivos");

                // Reordenar para RH (como no código original)
                navigationBar.getChildren().remove(btnObjetivos);
                int indiceInsercao = navigationBar.getChildren().indexOf(btnPDI) + 1;
                navigationBar.getChildren().add(indiceInsercao, btnObjetivos);

                paginaInicial = this::handleMenuDashboard; // RH começa no Dashboard
                break;

            case "Gestor Geral":
                btnObjetivos.setVisible(true);
                btnObjetivos.setManaged(true);
                btnObjetivos.setText("Objetivos (Geral)");
                paginaInicial = this::handleMenuObjetivos; // Gestor começa em Objetivos
                break;

            case "Gestor de Area":
                btnObjetivos.setVisible(true);
                btnObjetivos.setManaged(true);
                btnObjetivos.setText("Objetivos (" + usuarioLogado.getSetorNome() + ")");
                paginaInicial = this::handleMenuObjetivos; // Gestor começa em Objetivos
                break;

            case "Colaborador":
                paginaInicial = this::handleMenuPDI;
                break;

            default:
                btnPDI.setVisible(false);
                btnPDI.setManaged(false);
                paginaInicial = this::handleMenuPerfil;
                break;
        }

        // --- 3. Carregar a página inicial definida ---
        paginaInicial.run();
    }


    @FXML
    void handleMenuPerfil() {
        loadPage("PerfilGUI", btnPerfil);
    }

    @FXML
    void handleMenuPDI() {
        if (usuarioLogado == null) return;

        boolean isRh = "RH".equals(usuarioLogado.getTipo_usuario());
        if (isRh) {
            loadPage("PDIsGUI", btnPDI);
        } else {
            loadPage("MeuPdiGUI", btnPDI);
        }
    }

    @FXML
    void handleMenuDashboard() {
        loadPage("DashboardGUI", btnDashboard);
    }

    @FXML
    void handleMenuUsuarios() {
        loadPage("UsuariosGUI", btnUsuarios);
    }

    @FXML
    void handleMenuObjetivos() {
        loadPage("ObjetivosGUI", btnObjetivos);
    }

    @FXML
    void handleSair(ActionEvent event) {
        try {
            Session.setUsuarioAtual(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/login/LoginGUI.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = StageManager.getStage();

            if (stage != null && stage.getScene() != null) {
                stage.getScene().setRoot(loginRoot);
            } else {
                System.err.println("Erro: Não foi possível obter o Stage ou a Scene para deslogar.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            util.Util.mostrarAlerta(javafx.scene.control.Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela de login.");
        }
    }

    private void updateActiveButton(Button activeButton) {
        for (Button button : navigationButtons) {
            button.getStyleClass().remove("active");
        }
        activeButton.getStyleClass().add("active");
    }

    private void loadPage(String fxmlFile, Button btnClicado) {
        updateActiveButton(btnClicado);
        try {
            String resourcePath = "/gui/menu/" + fxmlFile + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Node page = loader.load();

            // Injeta o usuário logado no controller da página carregada
            Object controller = loader.getController();
            try {
                Method setUsuarioMethod = controller.getClass().getMethod("setUsuario", Usuario.class);
                setUsuarioMethod.invoke(controller, this.usuarioLogado);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                // Normal. O controller da página não precisa/não tem o método setUsuario()
            }

            conteudo.getChildren().setAll(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Falha ao carregar a página: " + fxmlFile);
        }
    }
}