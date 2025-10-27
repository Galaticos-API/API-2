package gui.modal;

import dao.SetorDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Setor;
import modelo.Usuario;
import util.CriptografiaUtil; // <-- IMPORT ADICIONADO
import util.Util;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EditarUsuarioModalController {

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
    @FXML
    private ComboBox<String> comboStatus;

    private Stage dialogStage;
    private Usuario usuarioEditado;
    private boolean salvo = false;

    private SetorDAO setorDAO = new SetorDAO();

    @FXML
    private void initialize() {
        List<Setor> setores = setorDAO.listarTodos();

        comboSetor.setItems(FXCollections.observableArrayList(setores));
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
        comboTipoUsuario.setValue(usuario.getTipo_usuario());
        comboStatus.setValue(usuario.getStatus());
        comboSetor.setValue(usuario.getSetor());
    }

    @FXML
    private void handleSalvar() {
        if (isInputValid()) {
            try {
                usuarioEditado.setNome(txtNome.getText().trim());
                usuarioEditado.setEmail(txtEmail.getText().trim());
                usuarioEditado.setTipo_usuario(comboTipoUsuario.getValue());
                usuarioEditado.setStatus(comboStatus.getValue());
                usuarioEditado.setSetor_id(comboSetor.getValue().getId());

                if (!txtSenha.getText().trim().isEmpty()) {
                    String senhaPlana = txtSenha.getText().trim();
                    String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);
                    usuarioEditado.setSenha(senhaCriptografada);
                }

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.atualizar(usuarioEditado);

                Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");

                salvo = true;
                dialogStage.close();

            } catch (RuntimeException | SQLException e) {
                Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Atualizar", e.getMessage());
            } catch (Exception e) {
                // Adicionado catch para erros de criptografia
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

        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            errorMessage += "Nome inválido!\n";
        }
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty() || !txtEmail.getText().contains("@")) {
            errorMessage += "E-mail inválido!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos", "Por favor, corrija os campos inválidos: \n" + errorMessage);
            return false;
        }
    }
}