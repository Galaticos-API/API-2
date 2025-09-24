package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import modelo.Usuario;
import util.SceneManager;
import util.Session;

public class MainGUIController {

    private Usuario usuarioLogado;

    @FXML
    private Label funcaoUsuario;
    @FXML
    private Label logadoComo;


    @FXML
    public void initialize() {
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;

        logadoComo.setText("Logado como: " + this.usuarioLogado.getNome());
        funcaoUsuario.setText("Função: " + this.usuarioLogado.getTipo_usuario());
    }

    @FXML
    void clickUsuarios(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListaUsuarios.fxml"));
        Parent root = loader.load();
        MainGUIController controller = loader.getController();
    }

    @FXML
    void clickDeslogar(ActionEvent event) {
        Session.setUsuarioAtual(null);
        SceneManager.mudarCena("LoginGUI", "Login");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}