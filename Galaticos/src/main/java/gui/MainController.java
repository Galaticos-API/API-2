package gui;

import dao.PdiDAO;
import gui.menu.ListaPdiController;
import gui.menu.MeuPdiController;
import gui.menu.ObjetivosController;
import gui.menu.UsuariosController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import modelo.PDI;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public Label logadoComo;
    public Label funcaoUsuario;
    private Usuario usuarioLogado;

    @FXML
    private StackPane conteudo; // A área central que será atualizada

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
    private List<Button> navigationButtons;

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
        initialize();
    }

    @FXML
    public void initialize() {
        navigationButtons = Arrays.asList(btnPerfil, btnPDI, btnUsuarios, btnDashboard, btnObjetivos);

        if (usuarioLogado != null) {
            //validação do layout para tipo de usuário
            boolean podeVerUsuarios = "RH".equals(usuarioLogado.getTipo_usuario());
            btnUsuarios.setManaged(podeVerUsuarios);
            btnDashboard.setManaged(podeVerUsuarios);

            handleMenuDashboard();
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
            if (controller instanceof UsuariosController usuariosController) {
                usuariosController.setUsuario(this.usuarioLogado);
                usuariosController.initialize();
            } else if (controller instanceof ObjetivosController objetivosController) {
                objetivosController.setUsuarioLogado(usuarioLogado);
            } else if (controller instanceof ListaPdiController listaPdiController) {
                listaPdiController.setUsuarioLogado(usuarioLogado);
            } else if (controller instanceof MeuPdiController meuPdiController) {
                meuPdiController.setUsuarioLogado(usuarioLogado);
            }

            conteudo.getChildren().setAll(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Falha ao carregar a página: " + fxmlFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}