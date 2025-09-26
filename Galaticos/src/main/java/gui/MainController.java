package gui;

import gui.menu.UsuariosController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {

    public Label logadoComo;
    public Label funcaoUsuario;
    private Usuario usuarioLogado;

    @FXML
    private AnchorPane contentArea; // A área central que será atualizada

    @FXML
    private Menu menuPerfil;
    @FXML
    private Menu menuPDI;
    @FXML
    private Menu menuDashboard;
    @FXML
    private Menu menuUsuarios;

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
        initialize();
    }

    @FXML
    public void initialize() {
        if (usuarioLogado != null) {
            boolean podeVerUsuarios = "RH".equals(usuarioLogado.getTipo_usuario());
            menuUsuarios.setVisible(podeVerUsuarios);
        }
    }

    // Métodos para lidar com os cliques nos menus
    @FXML
    void handleMenuPerfil() {
        loadPage("PerfilGUI");
    }

    @FXML
    void handleMenuPDI() {
        loadPage("ListaPdiGUI");
    }

    @FXML
    void handleMenuDashboard() {
        loadPage("DashboardGUI");
    }

    @FXML
    void handleMenuUsuarios() {
        loadPage("UsuariosGUI");
    }

    private void loadPage(String fxmlFile) {
        try {
            String resourcePath = "/gui/menu/" + fxmlFile + ".fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Node page = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UsuariosController usuariosController) {
                usuariosController.setUsuario(this.usuarioLogado);
                usuariosController.initialize();
            } else {
                System.out.println("IF nao entrou");
            }
            // Você pode adicionar outros 'else if' para outras telas que precisem do usuário
            // else if (controller instanceof PerfilGUIController) { ... }

            contentArea.getChildren().setAll(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Falha ao carregar a página: " + fxmlFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}