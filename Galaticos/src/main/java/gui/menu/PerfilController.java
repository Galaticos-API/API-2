package gui.menu;

import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Usuario;
import util.CriptografiaUtil; // <-- IMPORT ADICIONADO
import util.Util; // Sua classe utilitária para mostrar alertas

public class PerfilController {

    @FXML
    private Label lblIniciais;
    @FXML
    private Label lblNomeUsuario;
    @FXML
    private Label lblTipoUsuario;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtCpf;
    @FXML
    private PasswordField txtSenha;

    private Usuario usuarioLogado;

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        System.out.println(usuarioLogado.getNome());
        System.out.println(usuarioLogado.getEmail());
        System.out.println(usuarioLogado.getCpf());
        System.out.println(usuarioLogado.getStatus());
        System.out.println(usuarioLogado.getSenha());
        System.out.println();
        popularDados();
    }

    private void popularDados() {
        if (usuarioLogado == null) return;

        lblNomeUsuario.setText(usuarioLogado.getNome());
        lblTipoUsuario.setText(usuarioLogado.getTipo_usuario());
        lblIniciais.setText(getIniciais(usuarioLogado.getNome()));

        txtNome.setText(usuarioLogado.getNome());
        txtEmail.setText(usuarioLogado.getEmail());
        txtCpf.setText(usuarioLogado.getCpf());
    }
    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    @FXML
    private void handleSalvar() {
        if (isEmpty(txtNome) || isEmpty(txtEmail)) {
            Util.mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome e E-mail não podem estar vazios.");
            return;
        }

        try {
            // Atualiza o objeto com os dados do formulário
            usuarioLogado.setNome(txtNome.getText().trim());
            usuarioLogado.setEmail(txtEmail.getText().trim());
            if (!isEmpty(txtCpf)) {
                usuarioLogado.setCpf(txtCpf.getText().trim());
            }

            // --- ALTERAÇÃO AQUI ---
            // Só atualiza a senha se o campo não estiver vazio
            if (!isEmpty(txtSenha)) {
                System.out.println("CAMPO SENHA NÃO TÁ VAZIO");
                String senhaPlana = txtSenha.getText();
                String senhaCriptografada = CriptografiaUtil.encrypt(senhaPlana);
                usuarioLogado.setSenha(senhaCriptografada);
            }
            // --- FIM DA ALTERAÇÃO ---

            // Persiste as alterações no banco de dados
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.atualizar(usuarioLogado);
            System.out.println("teste teste testiano");

            // Atualiza a UI e mostra feedback
            popularDados();
            Util.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Suas informações foram atualizadas com sucesso!");

        } catch (Exception e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar as informações: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Pega as iniciais do nome para exibir no avatar.
     */
    private String getIniciais(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "?";
        }
        String[] nomes = nome.trim().split("\\s+");
        if (nomes.length > 1) {
            return String.valueOf(nomes[0].charAt(0)) + String.valueOf(nomes[nomes.length - 1].charAt(0));
        } else {
            return String.valueOf(nomes[0].charAt(0));
        }
    }
}