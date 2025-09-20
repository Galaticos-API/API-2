package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import modelo.Usuario;
import util.SceneManager;
import util.Session;

public class MainGUIController {

    @FXML
    private Label funcaoUsuario;
    @FXML
    private Label logadoComo;


    @FXML
    public void initialize() {
    }

    public void setUsuario(Usuario usuario) {
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
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