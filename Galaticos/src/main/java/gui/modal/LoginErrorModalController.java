package gui.modal;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginErrorModalController {

    @FXML
    private Label lblMensagem;

    @FXML
    private Button btnOk;

    public void setMensagem(String mensagem) {
        lblMensagem.setText(mensagem);
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }
}
