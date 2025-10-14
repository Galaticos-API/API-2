package gui;

import gui.menu.UsuariosController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public Label logadoComo;
    public Label funcaoUsuario;
    private Usuario usuarioLogado;

    @FXML
    private AnchorPane contentArea; // A área central que será atualizada

    @FXML
    private Button btnPerfil;
    @FXML
    private Button btnPDI;
    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnDashboard;
    private List<Button> navigationButtons;

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        logadoComo.setText("Logado como: " + usuario.getNome());
        funcaoUsuario.setText("Função: " + usuario.getTipo_usuario());
        initialize();
    }

    @FXML
    public void initialize() {
        // Adiciona os botões à lista para fácil gerenciamento
        navigationButtons = Arrays.asList(btnPerfil, btnPDI, btnUsuarios, btnDashboard);

        if (usuarioLogado != null) {
            // Lógica para esconder botões baseada na permissão
            boolean podeVerUsuarios = "RH".equals(usuarioLogado.getTipo_usuario());
            btnUsuarios.setVisible(podeVerUsuarios);
            btnUsuarios.setManaged(podeVerUsuarios); // Garante que o espaço seja removido

            // Carrega a página inicial e define o botão de perfil como ativo
            handleMenuPerfil(null);
        }
    }

    // Métodos para lidar com os cliques nos menus
    @FXML
    void handleMenuPerfil(ActionEvent event) {
        loadPage("DashboardGUI");
        updateActiveButton(btnPerfil);
    }

    @FXML
    void handleMenuPDI() {
        loadPage("ListaPdiGUI");
        updateActiveButton(btnPDI);
    }

    @FXML
    void handleMenuDashboard() {
        loadPage("DashboardGUI");
        updateActiveButton(btnDashboard);
    }


    @FXML
    void handleMenuUsuarios() {
        loadPage("UsuariosGUI");
        updateActiveButton(btnUsuarios);
    }

    private void updateActiveButton(Button activeButton) {
        for (Button button : navigationButtons) {
            // Remove a classe 'active' de todos os botões
            button.getStyleClass().remove("active");
        }
        // Adiciona a classe 'active' apenas ao botão que foi clicado
        activeButton.getStyleClass().add("active");
    }

    private void loadPage(String fxmlFile) {
        try {
            String resourcePath = "/gui/menu/" + fxmlFile + ".fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Node page = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UsuariosController usuariosController) {
                usuariosController.setUsuario(this.usuarioLogado);
                usuariosController.initialize();
            } else {
                System.out.println("IF nao entrou");
            }
            // Você pode adicionar outros 'else if' para outras telas que precisem do usuário
            // else if (controller instanceof PerfilGUIController) { ... }

            contentArea.getChildren().setAll(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Falha ao carregar a página: " + fxmlFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}