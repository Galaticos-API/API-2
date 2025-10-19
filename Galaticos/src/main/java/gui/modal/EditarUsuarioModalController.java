package gui.modal;

import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Usuario;
import util.Util;

import java.sql.SQLException;
import java.time.LocalDate;

public class EditarUsuarioModalController {

    // --- Campos FXML ---
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private TextField txtCpf;
    @FXML
    private TextField txtTelefone;
    @FXML
    private DatePicker datePickerNascimento;
    @FXML
    private ComboBox<String> comboTipoUsuario;
    @FXML
    private ComboBox<String> comboStatus;

    private Stage dialogStage;
    private Usuario usuarioEditado;
    private boolean salvo = false;

    @FXML
    private void initialize() {
        // Preenche os ComboBoxes com as opções
        comboTipoUsuario.setItems(FXCollections.observableArrayList("RH", "Gestor de Área", "Gestor Geral", "Colaborador"));
        comboStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioEditado = usuario;

        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        txtCpf.setText(usuario.getCpf());
        datePickerNascimento.setValue(usuario.getData_nascimento());
        comboTipoUsuario.setValue(usuario.getTipo_usuario());
        comboStatus.setValue(usuario.getStatus());
    }

    @FXML
    private void handleSalvar() {
        if (isInputValid()) {
            try {
                usuarioEditado.setNome(txtNome.getText().trim());
                usuarioEditado.setEmail(txtEmail.getText().trim());
                usuarioEditado.setCpf(txtCpf.getText().trim());
                usuarioEditado.setData_nascimento(datePickerNascimento.getValue());
                usuarioEditado.setTipo_usuario(comboTipoUsuario.getValue());
                usuarioEditado.setStatus(comboStatus.getValue());

                if (!txtSenha.getText().trim().isEmpty()) {
                    usuarioEditado.setSenha(txtSenha.getText().trim());
                }

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.atualizar(usuarioEditado);

                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");

                salvo = true;
                dialogStage.close();

            } catch (RuntimeException | SQLException e) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Atualizar", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            errorMessage += "Nome inválido!\n";
        }
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty() || !txtEmail.getText().contains("@")) {
            errorMessage += "E-mail inválido!\n";
        }
        if (txtCpf.getText() == null || txtCpf.getText().trim().isEmpty()) {
            errorMessage += "CPF inválido!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos", "Por favor, corrija os campos inválidos: \n" + errorMessage);
            return false;
        }
    }
}