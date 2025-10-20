package gui.modal;

import dao.PdiDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.PDI;
import modelo.Usuario;

import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CadastroPdiModalController implements Initializable {

    // --- Componentes FXML Atualizados ---
    @FXML
    private ComboBox<Usuario> comboUsuario; // Substituído
    @FXML
    private DatePicker dataFechamentoField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Text mensagemErro;

    // --- Variáveis de Controle ---
    private Stage dialogStage;
    private boolean salvo = false;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PdiDAO pdiDao = new PdiDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Popula os ComboBoxes na inicialização
        statusComboBox.getItems().addAll("Em Andamento", "Arquivado");
        try {
            carregarUsuarios();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca todos os usuários no banco e os carrega no ComboBox.
     */
    private void carregarUsuarios() throws SQLException {
        // Busca os usuários
        List<Usuario> usuarios = usuarioDAO.lerTodos();
        comboUsuario.setItems(FXCollections.observableArrayList(usuarios));

        // Configura como o nome do usuário deve ser exibido no ComboBox
        comboUsuario.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return (usuario == null) ? "Selecione um usuário" : usuario.getNome();
            }

            @Override
            public Usuario fromString(String string) {
                // Não é necessário para um ComboBox não editável
                return null;
            }
        });

        // (Opcional) Configura a lista suspensa para mostrar mais detalhes
        comboUsuario.setCellFactory(new javafx.util.Callback<ListView<Usuario>, ListCell<Usuario>>() {
            @Override
            public ListCell<Usuario> call(ListView<Usuario> param) {
                return new ListCell<Usuario>() {
                    @Override
                    protected void updateItem(Usuario item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getNome() + " (" + item.getTipo_usuario() + ")");
                        }
                    }
                };
            }
        });
    }

    /**
     * Chamado ao clicar no botão "Criar PDI".
     * Valida os campos e salva o novo PDI no banco.
     */
    @FXML
    private void handleCriarPdi() {
        // 1. Obter os valores dos campos
        Usuario usuarioSelecionado = comboUsuario.getValue();
        String status = statusComboBox.getValue();

        // 2. Validar os dados de entrada
        if (!isInputValid(usuarioSelecionado, status, dataFechamentoField.getValue())) {
            return;
        }

        mensagemErro.setText(""); // Limpa erros

        try {
            // 3. Converter a data
            Date dataFechamento = Date.from(dataFechamentoField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 4. Criar o objeto PDI
            // (Assumindo que PDI agora aceita o ID do usuário diretamente)

            System.out.println(usuarioSelecionado.getId());
            PDI novoPdi = new PDI(usuarioSelecionado.getId(), status, new Date(), dataFechamento);

            // 5. Salvar no banco de dados
            PDI pdiCriado = pdiDao.adicionar(novoPdi);

            if (pdiCriado != null) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI criado com sucesso para: " + usuarioSelecionado.getNome());
                salvo = true;
                dialogStage.close();
            } else {
                mensagemErro.setText("Falha ao salvar o PDI no banco de dados.");
            }
        } catch (RuntimeException e) {
            mensagemErro.setText("Erro ao criar PDI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida os campos do formulário.
     */
    private boolean isInputValid(Usuario usuario, String status, java.time.LocalDate dataFechamento) {
        if (usuario == null) {
            mensagemErro.setText("Por favor, selecione um usuário.");
            return false;
        }
        if (status == null) {
            mensagemErro.setText("Por favor, selecione um status inicial.");
            return false;
        }
        if (dataFechamento == null) {
            mensagemErro.setText("Por favor, selecione uma data de fechamento.");
            return false;
        }
        return true;
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