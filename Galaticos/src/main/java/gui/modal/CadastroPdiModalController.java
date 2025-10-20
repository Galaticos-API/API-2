package gui.modal;

import dao.PdiDAO;
import dao.UsuarioDAO; // Importe o DAO correto
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.Usuario; // Importe o modelo correto

import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;


// soluçao temporaria
import factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroPdiModalController implements Initializable {

    @FXML
    private TextField usuarioIdField; // Renomeado
    @FXML
    private Text usuarioNomeText; // Renomeado
    @FXML
    private DatePicker dataFechamentoField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Text mensagemErro;

    private Usuario usuarioEncontrado; // Armazena o objeto Usuario completo
    private Stage dialogStage;
    private boolean salvo = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.getItems().addAll("Em Andamento", "Arquivado");
    }

    @FXML
    private void handleBuscarUsuario() { // Renomeado
        String inputIdStr = usuarioIdField.getText().trim();
        if (inputIdStr.isEmpty()) {
            mensagemErro.setText("Insira o ID do usuário.");
            return;
        }

        try {
            String idUsuario = inputIdStr;
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            this.usuarioEncontrado = usuarioDAO.buscarPorId(idUsuario);

            if (this.usuarioEncontrado != null) {
                usuarioNomeText.setText("Nome: " + this.usuarioEncontrado.getNome() + " (ID: " + this.usuarioEncontrado.getId() + ")");
                mensagemErro.setText(""); // Limpa o erro
            } else {
                mensagemErro.setText("Usuário não encontrado.");
                this.usuarioEncontrado = null;
            }
        } catch (NumberFormatException e) {
            mensagemErro.setText("ID inválido. Use apenas números.");
            this.usuarioEncontrado = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleCriarPdi() {
        if (usuarioEncontrado == null) {
            mensagemErro.setText("Primeiro, busque e encontre um usuário válido.");
            return;
        }

        if (statusComboBox.getValue() == null) {
            mensagemErro.setText("Selecione um status para o PDI.");
            return;
        }

        if (dataFechamentoField.getValue() == null) {
            mensagemErro.setText("Selecione uma data de fechamento.");
            return;
        }

        mensagemErro.setText(""); // Limpa erros


        // SOLUÇAO TEMPORARIA

        String colaboradorIdParaPdi = null; // Variável para guardar o ID do colaborador

        // --- Bloco Adicionado para buscar o ID do Colaborador ---
        String sqlBuscaColaborador = "SELECT id FROM colaborador WHERE usuario_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuscaColaborador)) {

            pstmt.setInt(1, Integer.parseInt(usuarioEncontrado.getId())); // Usa o ID do usuário encontrado
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    colaboradorIdParaPdi = rs.getString("id"); // Pega o ID do colaborador
                } else {
                    // Se não encontrar um colaborador associado ao usuário
                    mensagemErro.setText("Erro: O usuário encontrado (ID: " + usuarioEncontrado.getId() + ") não está associado a um perfil de colaborador no banco.");
                    return; // Impede a criação do PDI
                }
            }
        } catch (SQLException e) {
            mensagemErro.setText("Erro ao buscar o colaborador associado: " + e.getMessage());
            e.printStackTrace();
            return; // Impede a criação do PDI
        } catch (NumberFormatException e) {
            mensagemErro.setText("Erro: ID do usuário inválido ("+ usuarioEncontrado.getId() +").");
            e.printStackTrace();
            return;
        }
        // --- Fim do Bloco Adicionado ---

        // Continua com a criação do PDI, mas usando o ID do colaborador encontrado
        Date dataFechamento = Date.from(dataFechamentoField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Usa o colaboradorIdParaPdi (que é o ID do colaborador) ao criar o PDI
        PDI novoPdi = new PDI(colaboradorIdParaPdi, statusComboBox.getValue(), new Date(), dataFechamento); // <-- ID CORRETO USADO AQUI
        PdiDAO pdiDao = new PdiDAO();

        try {
            PDI pdiCriado = pdiDao.adicionar(novoPdi);
            if (pdiCriado != null) {
                // Você pode mostrar o nome do usuário aqui, já que o nome do colaborador não foi carregado
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI criado com sucesso para o usuário: " + usuarioEncontrado.getNome());
                salvo = true;
                dialogStage.close();
            } else {
                mensagemErro.setText("Falha ao salvar o PDI no banco de dados.");
            }
        } catch (RuntimeException e) {
            mensagemErro.setText("Erro ao criar PDI: " + e.getMessage());
            e.printStackTrace();
        }


//        // Converte LocalDate para Date
//        Date dataFechamento = Date.from(dataFechamentoField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Cria o novo PDI usando o ID do usuário encontrado
//        PDI novoPdi = new PDI(usuarioEncontrado.getId(), statusComboBox.getValue(), new Date(), dataFechamento);
//        PdiDAO pdiDao = new PdiDAO();
//        pdiDao.adicionar(novoPdi);
//
//        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI criado com sucesso para o usuário: " + usuarioEncontrado.getNome());
//
//        salvo = true;
//        dialogStage.close();
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}