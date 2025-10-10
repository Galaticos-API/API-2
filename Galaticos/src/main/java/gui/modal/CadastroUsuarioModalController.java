package gui.modal;

import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Usuario;
import util.Util;

import java.sql.SQLException;

public class CadastroUsuarioModalController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private ComboBox<String> comboTipoUsuario;

    private Stage dialogStage;
    private boolean salvo = false;

    // Seus serviços ou DAOs
    // private UsuarioService cadastroService = new UsuarioService();

    @FXML
    private void initialize() {
        // Preenche o ComboBox com as opções
        comboTipoUsuario.setItems(FXCollections.observableArrayList("RH", "Gestor de Área", "Gestor Geral"));
        comboTipoUsuario.getSelectionModel().selectFirst();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void handleSalvar() {
        if (isInputValid()) {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();
            String tipo_usuario = comboTipoUsuario.getValue();
            try {
                Usuario usuario = new Usuario(nome, email, senha, tipo_usuario, "Ativo", null, "", "");

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.adicionar(usuario);

                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso!");

                txtNome.clear();
                txtEmail.clear();
                txtSenha.clear();
            } catch (RuntimeException e) {
                // O catch continua o mesmo, pois o serviço vai lançar a exceção em caso de erro.
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro no Cadastro", e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // SIMULAÇÃO DE SUCESSO PARA O EXEMPLO:
            System.out.println("Usuário salvo com sucesso (simulação).");
            salvo = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (txtNome.getText() == null || txtNome.getText().isEmpty()) {
            errorMessage += "Nome inválido!\n";
        }
        if (txtEmail.getText() == null || txtEmail.getText().isEmpty() || !txtEmail.getText().contains("@")) {
            errorMessage += "E-mail inválido!\n";
        }
        if (txtSenha.getText() == null || txtSenha.getText().isEmpty()) {
            errorMessage += "Senha inválida!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Mostra a mensagem de erro.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Campos Inválidos");
            alert.setHeaderText("Por favor, corrija os campos inválidos.");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}