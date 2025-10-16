package gui.menu;

import dao.PdiDAO; // Importe seus DAOs
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import modelo.PDI;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML
    private Label lblTotalPdis;
    @FXML
    private Label lblUsuariosAtivos;
    @FXML
    private Label lblMediaConclusao;
    @FXML
    private PieChart pieChartStatusPdi;
    @FXML
    private BarChart<String, Number> barChartProgresso;

    private PdiDAO pdiDAO = new PdiDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() throws SQLException {
        // Este método é chamado automaticamente ao carregar o FXML
        carregarDadosDashboard();
    }

    private void carregarDadosDashboard() throws SQLException {
        // 1. Carregar dados dos DAOs
        List<PDI> todosOsPdis = pdiDAO.lerTodos();
        List<Usuario> todosOsUsuarios = usuarioDAO.lerTodos();

        // 2. Popular os widgets de estatísticas
        long pdisAtivos = todosOsPdis.stream().filter(pdi -> "Em Andamento".equals(pdi.getStatus())).count();
        lblTotalPdis.setText(String.valueOf(pdisAtivos));

        long usuariosAtivos = todosOsUsuarios.stream().filter(u -> "Ativo".equals(u.getStatus())).count();
        lblUsuariosAtivos.setText(String.valueOf(usuariosAtivos));

        double mediaConclusao = todosOsPdis.stream().mapToDouble(PDI::getPontuacaoGeral).average().orElse(0.0);
        lblMediaConclusao.setText(String.format("%.1f%%", mediaConclusao * 100));

        // 3. Popular o Gráfico de Pizza (Status dos PDIs)
        Map<String, Long> contagemStatus = todosOsPdis.stream()
                .collect(Collectors.groupingBy(PDI::getStatus, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        contagemStatus.forEach((status, count) -> pieChartData.add(new PieChart.Data(status, count)));
        pieChartStatusPdi.setData(pieChartData);

        // 4. Popular o Gráfico de Barras (Exemplo: Contagem de PDIs por Cargo)
        Map<String, Long> pdisPorCargo = todosOsUsuarios.stream()
                .filter(u -> todosOsPdis.stream().anyMatch(pdi -> pdi.getColaboradorId() == u.getId()))
                .collect(Collectors.groupingBy(Usuario::getTipo_usuario, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("PDIs por Cargo");
        pdisPorCargo.forEach((cargo, count) -> series.getData().add(new XYChart.Data<>(cargo, count)));
        barChartProgresso.getData().add(series);
    }
}