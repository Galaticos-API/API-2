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

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
        initialize();
    }

    @FXML
    public void initialize() {
        navigationButtons = Arrays.asList(btnObjetivos, btnPerfil, btnPDI, btnUsuarios, btnDashboard);

        if (usuarioLogado != null) {
            boolean isRh = "RH".equals(usuarioLogado.getTipo_usuario());

            rhSeparator.setVisible(isRh);
            rhSeparator.setManaged(isRh);
            btnPDI.setVisible(isRh);
            btnPDI.setManaged(isRh);
            btnDashboard.setVisible(isRh);
            btnDashboard.setManaged(isRh);
            btnUsuarios.setVisible(isRh);
            btnUsuarios.setManaged(isRh);

            if (isRh) {
                btnObjetivos.setText("Gerenciar objetivos");

                navigationBar.getChildren().remove(btnObjetivos);
                int indiceInsercao = navigationBar.getChildren().indexOf(btnPDI) + 1;
                if (indiceInsercao >= 1 && indiceInsercao <= navigationBar.getChildren().size()) {
                    navigationBar.getChildren().add(indiceInsercao, btnObjetivos);
                } else {
                    navigationBar.getChildren().add(btnObjetivos);
                }
            } else {
                btnObjetivos.setText("Meu PDI");
            }

            if (isRh) {
                handleMenuDashboard();
            } else {
                handleMenuObjetivos();
            }
        }
    }

    @FXML
    void handleMenuPerfil(ActionEvent event) {
        loadPage("PerfilGUI", btnPerfil);
    }

    @FXML
    void handleMenuPDI() {
        if (usuarioLogado == null) return;
        boolean isRh = "RH".equals(usuarioLogado.getTipo_usuario());

        if (isRh) {
            loadPage("ListaPdiGUI", btnPDI);
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
            // 1. Clear the current user session
            Session.setUsuarioAtual(null);

            // 2. Load the Login screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/login/LoginGUI.fxml")); // Adjust path if needed
            Parent loginRoot = loader.load();

            // 3. Get the main stage and set the login screen as its root
            Stage stage = StageManager.getStage(); // Or get stage from button: (Stage) btnSair.getScene().getWindow();
            if (stage != null && stage.getScene() != null) {
                stage.getScene().setRoot(loginRoot);
                // Optional: Reset title if needed, ensure maximized state persists automatically
                // stage.setTitle("Sistema PDI - Login");
            } else {
                System.err.println("Erro: Não foi possível obter o Stage ou a Scene para deslogar.");
                // Handle error appropriately, maybe show an alert
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert to the user
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
            Object controller = loader.getController();

            try {
                Method setUsuarioMethod = controller.getClass().getMethod("setUsuario", Usuario.class);
                setUsuarioMethod.invoke(controller, this.usuarioLogado);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                // Controller não tem setUsuario — tudo bem, só não faz nada
            }

            conteudo.getChildren().setAll(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Falha ao carregar a página: " + fxmlFile);
        }
    }

}