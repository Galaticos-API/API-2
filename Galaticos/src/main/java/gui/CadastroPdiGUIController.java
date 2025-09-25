package gui;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Objetivo;
import modelo.PDI;
import util.SceneManager;

import java.time.ZoneId;
import java.util.Date;

public class CadastroPdiGUIController {

    // --- Componentes do PDI ---
    @FXML
    private TextField colaboradorIdField;
    @FXML
    private TextField anoField;
    @FXML
    private ChoiceBox<String> statusPdiChoiceBox;

    // --- Componentes do Objetivo ---
    @FXML
    private TextArea descricaoObjetivoArea;
    @FXML
    private DatePicker prazoObjetivoPicker;
    @FXML
    private ChoiceBox<String> statusObjetivoChoiceBox;
    @FXML
    private TextField pesoObjetivoField;
    @FXML
    private TextArea comentariosObjetivoArea;

    // --- Botões de Ação ---
    @FXML
    private Button cadastrarPdiBtn;
    @FXML
    private Button voltarBtn;

    // DAOs para interagir com o banco de dados
    private final PdiDAO pdiDAO = new PdiDAO();
    private final ObjetivoDAO objetivoDAO = new ObjetivoDAO();

    @FXML
    public void initialize() {
        // Configura as opções para os ChoiceBoxes
        statusPdiChoiceBox.setItems(FXCollections.observableArrayList("Em Andamento", "Concluído", "Cancelado"));
        statusPdiChoiceBox.setValue("Em Andamento"); // Valor padrão

        statusObjetivoChoiceBox.setItems(FXCollections.observableArrayList("Não Iniciado", "Em Progresso", "Concluído"));
        statusObjetivoChoiceBox.setValue("Não Iniciado"); // Valor padrão
    }

    @FXML
    void clickCadastrarPdi(ActionEvent event) {
        // --- Validação dos Campos ---
        if (colaboradorIdField.getText().isBlank() || anoField.getText().isBlank() ||
                descricaoObjetivoArea.getText().isBlank() || prazoObjetivoPicker.getValue() == null ||
                pesoObjetivoField.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        try {
            // --- Coleta de Dados do PDI ---
            int colaboradorId = Integer.parseInt(colaboradorIdField.getText().trim());
            int ano = Integer.parseInt(anoField.getText().trim());
            String statusPdi = statusPdiChoiceBox.getValue();
            // Pega a data tanto para o PDI quanto para o Objetivo
            Date prazoObj = Date.from(prazoObjetivoPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());


            // --- Criação e Persistência do PDI ---
            PDI novoPdi = new PDI();
            novoPdi.setColaboradorId(colaboradorId);
            novoPdi.setAno(ano);
            novoPdi.setStatus(statusPdi);
            novoPdi.setPontuacaoGeral(0.0f); // Pontuação inicial

            // **** ALTERAÇÃO PRINCIPAL AQUI ****
            // Atribui a data do DatePicker à data de fechamento do PDI
            novoPdi.setDataFechamento(prazoObj);

            // Salva o PDI e obtém o objeto de volta com o ID
            PDI pdiSalvo = pdiDAO.adicionar(novoPdi);

            if (pdiSalvo == null || pdiSalvo.getId() == 0) {
                showAlert(Alert.AlertType.ERROR, "Erro no Cadastro", "Não foi possível salvar o PDI no banco de dados.");
                return;
            }

            // --- Coleta de Dados do Objetivo ---
            String descricaoObj = descricaoObjetivoArea.getText().trim();
            String statusObj = statusObjetivoChoiceBox.getValue();
            float pesoObj = Float.parseFloat(pesoObjetivoField.getText().trim());
            String comentariosObj = comentariosObjetivoArea.getText().trim();

            // --- Criação e Persistência do Objetivo ---
            Objetivo novoObjetivo = new Objetivo();
            novoObjetivo.setPdiId(pdiSalvo.getId()); // Associa o objetivo ao PDI recém-criado
            novoObjetivo.setDescricao(descricaoObj);
            novoObjetivo.setPrazo(prazoObj); // Usa a mesma data para o prazo do objetivo
            novoObjetivo.setStatus(statusObj);
            novoObjetivo.setPeso(pesoObj);
            novoObjetivo.setComentarios(comentariosObj);
            novoObjetivo.setPontuacao(0.0f); // Pontuação inicial

            objetivoDAO.adicionar(novoObjetivo);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI e objetivo inicial cadastrados com sucesso!");
            limparCampos();

            // Opcional: navegar para outra tela após o sucesso
            // SceneManager.mudarCena("DashboardGUI", "Painel Principal");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Os campos 'ID Colaborador', 'Ano' e 'Peso' devem ser números válidos.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro ao salvar os dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        colaboradorIdField.clear();
        anoField.clear();
        descricaoObjetivoArea.clear();
        prazoObjetivoPicker.setValue(null);
        pesoObjetivoField.clear();
        comentariosObjetivoArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void clickVoltar(ActionEvent event) {
        // Mude "DashboardGUI" para a tela principal ou anterior do seu sistema
        SceneManager.mudarCena("DashboardGUI", "Painel Principal");
    }
}