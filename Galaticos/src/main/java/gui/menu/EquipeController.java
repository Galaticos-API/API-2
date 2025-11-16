package gui.menu;

import dao.PdiDAO;
import dao.UsuarioDAO;
import gui.modal.CadastroPdiModalController;
import gui.modal.EditarPDIModalController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.Usuario;
import util.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipeController {

    @FXML
    private Label tituloPagina;
    @FXML
    private TableView<Usuario> tabelaEquipe;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colTipo;
    @FXML
    private TableColumn<Usuario, String> colPDIStatus;
    @FXML
    private TableColumn<Usuario, PDI> colProgresso;
    @FXML
    private TableColumn<Usuario, Usuario> colAcoes;

    private Usuario usuarioLogado;
    private UsuarioDAO usuarioDAO;
    private PdiDAO pdiDAO;

    private Map<String, PDI> mapaPDIs;

    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDAO();
        this.pdiDAO = new PdiDAO();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        tituloPagina.setText("Equipe (Setor: " + usuarioLogado.getSetorNome() + ")");
        carregarDadosEquipe();
        configurarColunas();
    }

    private void carregarDadosEquipe() {
        try {
            List<PDI> todosPDIs = pdiDAO.lerTodos();
            mapaPDIs = todosPDIs.stream()
                    .collect(Collectors.toMap(PDI::getColaboradorId, pdi -> pdi));

            List<Usuario> todosUsuarios = usuarioDAO.lerTodos();

            String meuSetorId = usuarioLogado.getSetor_id();

            List<Usuario> minhaEquipe = todosUsuarios.stream()
                    .filter(u -> meuSetorId.equals(u.getSetor_id()) &&
                            !u.getId().equals(usuarioLogado.getId()) &&
                            "Colaborador".equals(u.getTipo_usuario()))
                    .collect(Collectors.toList());

            tabelaEquipe.setItems(FXCollections.observableArrayList(minhaEquipe));

        } catch (SQLException | RuntimeException e) {
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar os dados da equipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_usuario"));

        colPDIStatus.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            PDI pdi = mapaPDIs.get(usuario.getId());
            String status = (pdi != null) ? pdi.getStatus() : "Sem PDI";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        colProgresso.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            PDI pdi = mapaPDIs.get(usuario.getId());
            return new SimpleObjectProperty<>(pdi);
        });

        colProgresso.setCellFactory(param -> new TableCell<>() {
            private final HBox hbox = new HBox(5);
            private final ProgressBar progressBar = new ProgressBar();
            private final Label lblProgresso = new Label();

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(progressBar, lblProgresso);
            }

            @Override
            protected void updateItem(PDI pdi, boolean empty) {
                super.updateItem(pdi, empty);
                if (empty || pdi == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(pdi.getPontuacaoGeral());
                    lblProgresso.setText(String.format("%.1f%%", pdi.getPontuacaoGeral() * 100));
                    setGraphic(hbox);
                }
            }
        });

        colAcoes.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue())
        );

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnAcao = new Button();

            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setGraphic(null);
                    return;
                }

                PDI pdi = mapaPDIs.get(usuario.getId());
                if (pdi != null) {
                    btnAcao.setText("Gerenciar PDI");
                    btnAcao.setStyle("-fx-background-color: #2A8E9D; -fx-text-fill: white;");
                    btnAcao.setOnAction(event -> handleGerenciarPDI(pdi));
                } else {
                    btnAcao.setText("Criar PDI");
                    btnAcao.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                    btnAcao.setOnAction(event -> handleCriarPDI(usuario));
                }
                setGraphic(btnAcao);
            }
        });
    }

    private void handleGerenciarPDI(PDI pdiSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/EditarPDIModal.fxml"));
            Parent page = loader.load();
            EditarPDIModalController controller = loader.getController();
            controller.setPDI(pdiSelecionado);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar PDI de " + pdiSelecionado.getNomeColaborador());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabelaEquipe.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            carregarDadosEquipe();

        } catch (IOException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de edição de PDI.");
        }
    }

    private void handleCriarPDI(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/modal/CadastroPdiModal.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Registrar novo PDI para " + usuario.getNome());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabelaEquipe.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            CadastroPdiModalController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            if (controller.isSalvo()) {
                carregarDadosEquipe();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Util.mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Não foi possível abrir a tela de cadastro de PDI.");
        }
    }
}