package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelo.Usuario;
import util.SceneManager;

import java.util.List;

public class mainGUIController {

    @FXML
    public void initialize() {
    }

    @FXML
    void clickMudarTelaCadastro(ActionEvent event) {
        SceneManager.mudarCena("CadastroGUI", "Cadastro");
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}