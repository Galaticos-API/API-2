package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import modelo.Usuario;
import util.SceneManager;

public class PdiGUIController {

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
        initialize();
    }

    @FXML
    public void initialize() {
        if (usuarioLogado != null) {
            SceneManager.mudarCena("PdiGUI", "Perfil");

            boolean podeVerUsuarios = "RH".equals(usuarioLogado.getTipo_usuario()) || "Gestor Geral".equals(usuarioLogado.getTipo_usuario());
            menuUsuarios.setVisible(podeVerUsuarios);
        }
    }

    // Métodos para lidar com os cliques nos menus
    @FXML
    void handleMenuPerfil() {
        SceneManager.mudarCena("PerfilGUI", "Perfil");
    }

    @FXML
    void handleMenuPDI() {
        SceneManager.mudarCena("PdiGUI", "PDI");
    }

    @FXML
    void handleMenuDashboard() {
        SceneManager.mudarCena("DashboardGUI", "Dashboard");
    }

    @FXML
    void handleMenuUsuarios() {
        SceneManager.mudarCena("UsuariosGUI", "Usuários");
    }

}