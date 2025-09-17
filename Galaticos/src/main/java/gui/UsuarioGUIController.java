package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import modelo.Cliente;

public class UsuarioGUIController {

    // @FXML conecta as variáveis do Java com os componentes do FXML pelo fx:id
    @FXML
    private TextField nomeCliente;
    @FXML
    private TextField codigoCliente;

    @FXML
    private Button cadastrarClienteBtn;

    @FXML
    private Button sairBtn;

    /**
     * Esta função é chamada quando o botão "Cadastrar" é clicado.
     * Ela pega o texto do campo nomeCliente, exibe no console e limpa o campo.
     *
     * @param event O evento de clique do botão.
     */
    @FXML
    void clickCadastrar(ActionEvent event) {
        String nome = nomeCliente.getText().trim();
        String codigoStr = codigoCliente.getText().trim();
        int codigo = Integer.parseInt(codigoStr);

        if (nome.isEmpty() || codigoStr.isEmpty()) {
            System.out.println("O campo nome não pode estar vazio.");
        } else {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Cliente cliente = new Cliente(codigo, nome);
            usuarioDAO.adiciona(cliente);
            nomeCliente.clear(); // Limpa o campo de texto após o cadastro
        }
    }

    /**
     * Esta função é chamada quando o botão "Sair" é clicado.
     * Ela fecha a aplicação de forma segura.
     *
     * @param event O evento de clique do botão.
     */
    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit(); // Maneira correta de fechar uma aplicação JavaFX
        System.exit(0);
    }
}