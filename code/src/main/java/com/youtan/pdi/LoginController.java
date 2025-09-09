package com.youtan.pdi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Button loginButton;

    @FXML
    private Text mensagemTexto;

    @FXML
    protected void handleLoginButtonAction() {
        String email = emailField.getText();
        String senha = senhaField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mensagemTexto.setText("Preencha todos os campos!");
            return;
        }

        // Lógica de autenticação aqui
        // Exemplo simples:
        if ("admin@youtan.com".equals(email) && "12345".equals(senha)) {
            mensagemTexto.setText("Login bem-sucedido!");
            // Em uma aplicação real, você fecharia a janela de login e abriria a tela principal.
        } else {
            mensagemTexto.setText("E-mail ou senha incorretos.");
        }
    }
}