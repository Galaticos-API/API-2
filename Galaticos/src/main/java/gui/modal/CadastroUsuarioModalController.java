package gui.modal;

import dao.SetorDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Setor;
import modelo.Usuario;
import util.CriptografiaUtil; // <-- IMPORT ADICIONADO
import util.Util;

import java.sql.SQLException;
import java.util.List;

public class CadastroUsuarioModalController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private ComboBox<String> comboTipoUsuario;
    @FXML
    public ComboBox<Setor> comboSetor;

    private Stage dialogStage;
    private boolean salvo = false;

    private SetorDAO setorDAO = new SetorDAO();

    @FXML
    private void initialize() {
        List<Setor> setores = setorDAO.listarTodos();

        comboSetor.setItems(FXCollections.observableArrayList(setores));
        comboTipoUsuario.setItems(FXCollections.observableArrayList("RH", "Gestor de Área", "Gestor Geral", "Colaborador"));
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
            String senhaPlana = txtSenha.getText().trim();
            String tipo_usuario = comboTipoUsuario.getValue();
            Setor setorSelecionado = comboSetor.getValue();
            String setor_id = "";

            if (setorSelecionado != null) {
                setor_id = setorSelecionado.getId();
            } else {
                Util.mostrarAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, selecione um setor.");
            }
            try {
                String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);
                Usuario usuario = new Usuario(nome, email, senhaCriptografada, tipo_usuario, "Ativo", null, "", "", setor_id);

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.adicionar(usuario);

                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso!");

                txtNome.clear();
                txtEmail.clear();
                txtSenha.clear();

                salvo = true;
                dialogStage.close();

            } catch (SQLException e) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro no Cadastro", e.getMessage());
            } catch (Exception e) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro de Segurança", "Não foi possível processar a senha: " + e.getMessage());
            }
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