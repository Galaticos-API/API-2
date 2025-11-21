package gui.menu;

import dao.ObjetivoDAO;
import dao.PdiDAO;
import dao.UsuarioDAO;
import dao.SetorDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane; // IMPORT NECESSÁRIO
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // IMPORT NECESSÁRIO
import modelo.Objetivo;
import modelo.ObjetivoComPDI;
import modelo.PDI;
import modelo.Setor;
import modelo.Usuario;
import util.Util;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;

public class DashboardController {

    @FXML
    private Label lblTitulo;

    @FXML
    private HBox filterBar;
    @FXML
    private ComboBox<String> comboAno;
    @FXML
    private ComboBox<Setor> comboSetor;
    @FXML
    private ComboBox<Usuario> comboGestor;
    @FXML
    private Button btnLimparFiltros;

    // --- KPIs ---
    @FXML
    private Label lblTotalPdisAtivos;
    @FXML
    private Label lblUsuariosAtivos;
    @FXML
    private Label lblMediaConclusao; // Original
    @FXML
    private Label lblObjetivosPendentes;
    @FXML
    private Label lblMediaObjetivosPDI; // [GAP 1]
    @FXML
    private Label lblTaxaConclusaoObjetivos; // [NOVO]

    // --- Gráficos ---
    @FXML
    private LineChart<String, Number> lineChartPdisPorAno; // Original
    @FXML
    private BarChart<String, Number> barChartProgressoSetor; // Original
    @FXML
    private PieChart pieChartObjetivoStatus; // Original
    @FXML
    private PieChart pieChartObjetivoTipo; // Original
    @FXML
    private BarChart<String, Number> barChartUsuariosPorSetor; // [GAP 5]
    @FXML
    private PieChart pieChartPDIStatus; // [GAP 4]
    @FXML
    public VBox chartBoxPDIsPorStatus;

    // --- Tabela e Alertas ---
    @FXML
    private TableView<RankingData> tableRanking;
    @FXML
    private TableColumn<RankingData, String> colRankNome;
    @FXML
    private TableColumn<RankingData, Integer> colRankLiderados;
    @FXML
    private TableColumn<RankingData, Integer> colRankPDIs;
    @FXML
    private TableColumn<RankingData, String> colRankProgresso;
    @FXML
    private ListView<String> listAlertas;

    @FXML
    private GridPane chartsGrid1;
    @FXML
    private GridPane chartsGrid2;
    @FXML
    private VBox chartBoxPdisPorAno;
    @FXML
    private VBox chartBoxProgressoSetor;
    @FXML
    private VBox chartBoxUsuariosPorSetor;

    @FXML
    private VBox chartBoxAreasDeAtencao;
    @FXML
    private ListView<String> listAreasDeAtencao;


    private PdiDAO pdiDAO = new PdiDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObjetivoDAO objetivoDAO = new ObjetivoDAO();
    private SetorDAO setorDAO = new SetorDAO();

    private List<PDI> cacheTodosOsPdis;
    private List<Usuario> cacheTodosOsUsuarios;
    private List<Objetivo> cacheTodosOsObjetivos;
    private List<Setor> cacheTodosOsSetores;
    private List<Usuario> cacheGestores;

    private Usuario usuarioLogado;

    private ObservableList<RankingData> rankingDataList = FXCollections.observableArrayList();

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;

        if ("Gestor Geral".equals(usuario.getTipo_usuario()) || "Gestor de Area".equals(usuario.getTipo_usuario())) {
            lblTitulo.setText("Dashboard (Visão de Gestores)");

            chartsGrid2.setVisible(false);
            chartsGrid2.setManaged(false);

            chartBoxPdisPorAno.setVisible(false);
            chartBoxPdisPorAno.setManaged(false);
            chartBoxUsuariosPorSetor.setVisible(false);
            chartBoxUsuariosPorSetor.setManaged(false);

            GridPane.setColumnIndex(chartBoxProgressoSetor, 0);

            chartBoxProgressoSetor.setVisible(true);
            chartBoxProgressoSetor.setManaged(true);
            chartBoxAreasDeAtencao.setVisible(true);
            chartBoxAreasDeAtencao.setManaged(true);

        } else if ("RH".equals(usuario.getTipo_usuario())) {
            lblTitulo.setText("Dashboard (Visão de RH)");

            chartsGrid2.setVisible(true);
            chartsGrid2.setManaged(true);
            chartBoxPdisPorAno.setVisible(true);
            chartBoxPdisPorAno.setManaged(true);
            chartBoxUsuariosPorSetor.setVisible(true);
            chartBoxUsuariosPorSetor.setManaged(true);
            chartBoxProgressoSetor.setVisible(true);
            chartBoxProgressoSetor.setManaged(true);

            chartBoxAreasDeAtencao.setVisible(false);
            chartBoxAreasDeAtencao.setManaged(false);
        } else if ("Colaborador".equals(usuario.getTipo_usuario())) {
            lblTitulo.setText("Dashboard (Visão de colaborador)");

            chartsGrid2.setVisible(true);
            chartsGrid2.setManaged(true);
            chartBoxPdisPorAno.setVisible(true);
            chartBoxPdisPorAno.setManaged(true);
            chartBoxUsuariosPorSetor.setVisible(true);
            chartBoxUsuariosPorSetor.setManaged(true);
            chartBoxProgressoSetor.setVisible(true);
            chartBoxProgressoSetor.setManaged(true);

            chartBoxAreasDeAtencao.setVisible(false);
            chartBoxAreasDeAtencao.setManaged(false);
            //chartBoxPdisPorAno.setVisible(false);
            //chartBoxPdisPorAno.setManaged(false);
            //chartBoxPDIsPorStatus.setVisible(false);
            //chartBoxPDIsPorStatus.setManaged(false);
        }


        configurarTabelaRanking();
        carregarDadosIniciais();
        setupFiltroListeners();
        atualizarDashboard();

        carregarAlertas();
        if ("Gestor Geral".equals(usuario.getTipo_usuario())) {
            carregarAreasDeAtencao();
        }
    }

    private void carregarDadosIniciais() {
        try {
            cacheTodosOsPdis = pdiDAO.lerTodos();
            cacheTodosOsUsuarios = usuarioDAO.lerTodos();
            cacheTodosOsObjetivos = objetivoDAO.lerTodos();
            cacheTodosOsSetores = setorDAO.listarTodos();
            cacheGestores = cacheTodosOsUsuarios.stream()
                    .filter(u -> "Gestor de Area".equals(u.getTipo_usuario()) || "Gestor Geral".equals(u.getTipo_usuario()))
                    .collect(Collectors.toList());

            populateFilters();

        } catch (SQLException e) {
            e.printStackTrace();
            Util.mostrarAlerta(javafx.scene.control.Alert.AlertType.ERROR, "Erro Crítico", "Não foi possível carregar os dados do banco: " + e.getMessage());
        }
    }

    private void setupFiltroListeners() {
        Runnable filtroAction = this::atualizarDashboard;

        comboAno.setOnAction(e -> filtroAction.run());
        comboSetor.setOnAction(e -> filtroAction.run());
        comboGestor.setOnAction(e -> filtroAction.run());

        btnLimparFiltros.setOnAction(e -> {
            comboAno.getSelectionModel().clearSelection();
            comboSetor.getSelectionModel().clearSelection();
            comboGestor.getSelectionModel().clearSelection();

            Platform.runLater(() -> {
                resetPromptText(comboAno, "Filtrar por Ano");
                resetPromptText(comboSetor, "Filtrar por Setor");
                resetPromptText(comboGestor, "Filtrar por Gestor");

                btnLimparFiltros.requestFocus();
            });

            atualizarDashboard();
        });
    }

    @SuppressWarnings("unchecked")
    private <T> void resetPromptText(ComboBox<T> combo, String prompt) {
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(prompt);
                } else {
                    if (item instanceof Usuario) {
                        setText(((Usuario) item).getNome());
                    } else if (item instanceof Setor) {
                        setText(((Setor) item).getNome());
                    } else {
                        setText(item.toString());
                    }
                }
            }
        });
        combo.setPromptText(prompt);
    }

    private void populateFilters() {
        if (cacheTodosOsPdis != null) {
            List<String> anos = cacheTodosOsPdis.stream()
                    .map(pdi -> pdi.getDataCriacao().substring(6, 10))
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
            comboAno.setItems(FXCollections.observableArrayList(anos));
        }

        if (cacheTodosOsSetores != null) {
            comboSetor.setItems(FXCollections.observableArrayList(cacheTodosOsSetores));
            comboSetor.setConverter(new javafx.util.StringConverter<Setor>() {
                @Override
                public String toString(Setor s) {
                    return s == null ? null : s.getNome();
                }

                @Override
                public Setor fromString(String s) {
                    return null;
                }
            });
        }

        if (cacheGestores != null) {
            comboGestor.setItems(FXCollections.observableArrayList(cacheGestores));
            comboGestor.setConverter(new javafx.util.StringConverter<Usuario>() {
                @Override
                public String toString(Usuario u) {
                    return u == null ? null : u.getNome();
                }

                @Override
                public Usuario fromString(String s) {
                    return null;
                }
            });
        }
    }

    private void atualizarDashboard() {
        if (cacheTodosOsPdis == null) {
            System.err.println("DashboardController: Caches estão nulos. Abortando atualização.");
            return;
        }

        String anoSelecionado = comboAno.getValue();
        Setor setorSelecionado = comboSetor.getValue();

        List<Usuario> usuariosFiltrados = cacheTodosOsUsuarios.stream()
                .filter(u -> (setorSelecionado == null || u.getSetor_id().equals(setorSelecionado.getId())))
                .collect(Collectors.toList());

        List<PDI> pdisFiltrados = cacheTodosOsPdis.stream()
                .filter(pdi -> (anoSelecionado == null || pdi.getDataCriacao().endsWith(anoSelecionado)))
                .filter(pdi -> usuariosFiltrados.stream().anyMatch(u -> u.getId().equals(pdi.getColaboradorId())))
                .collect(Collectors.toList());

        List<String> pdisFiltradosIds = pdisFiltrados.stream().map(PDI::getId).collect(Collectors.toList());

        List<Objetivo> objetivosFiltrados = cacheTodosOsObjetivos.stream()
                .filter(obj -> pdisFiltradosIds.contains(obj.getPdiId()))
                .collect(Collectors.toList());

        // Atualiza KPIs
        atualizarKPIs(pdisFiltrados, usuariosFiltrados, objetivosFiltrados);

        // Atualiza Gráficos (alguns com dados filtrados, outros com cache total)
        atualizarGraficoObjetivoStatus(objetivosFiltrados);
        atualizarGraficoObjetivoFoco(objetivosFiltrados);
        atualizarGraficoPdisPorAno(cacheTodosOsPdis); // Histórico usa cache total
        atualizarGraficoProgressoSetor(cacheTodosOsPdis, cacheTodosOsUsuarios); // Comparação global usa cache
        atualizarTabelaRanking(cacheTodosOsPdis, cacheTodosOsUsuarios); // Ranking global usa cache

        // [GAPS] Atualiza os novos gráficos com dados filtrados
        atualizarGraficoPDIStatus(pdisFiltrados);
        atualizarGraficoUsuariosPorSetor(usuariosFiltrados);

        // Atualiza o novo KPI do Gestor Geral se ele estiver logado
        if ("Gestor Geral".equals(usuarioLogado.getTipo_usuario())) {
            carregarAreasDeAtencao();
        }
    }

    private void atualizarKPIs(List<PDI> pdis, List<Usuario> usuarios, List<Objetivo> objetivos) {
        // KPI 1: total de PDIs ativos
        long pdisAtivos = pdis.stream()
                .filter(pdi -> "Em Andamento".equals(pdi.getStatus()))
                .count();
        lblTotalPdisAtivos.setText(String.valueOf(pdisAtivos));

        // KPI 2: usuários ativos
        long usuariosAtivos = usuarios.stream()
                .filter(u -> "Ativo".equals(u.getStatus()))
                .count();
        lblUsuariosAtivos.setText(String.valueOf(usuariosAtivos));

        // KPI 3: total de objetivos pendentes
        long objetivosPendentes = objetivos.stream()
                .filter(o -> !"Concluído".equals(o.getStatus()))
                .count();
        lblObjetivosPendentes.setText(String.valueOf(objetivosPendentes));

        // KPI 4: média de conclusão (geral) - Original
        double mediaConclusao = pdis.stream()
                .mapToDouble(PDI::getPontuacaoGeral)
                .average()
                .orElse(0.0);
        lblMediaConclusao.setText(String.format("%.1f%%", mediaConclusao * 100));

        // KPI 5: [GAP 1] Média de Objetivos por PDI
        double totalObjetivos = (double) objetivos.size();
        double totalPDIs = (double) pdis.size();
        double mediaObjPDI = (totalPDIs == 0) ? 0 : (totalObjetivos / totalPDIs);
        lblMediaObjetivosPDI.setText(String.format("%.1f", mediaObjPDI));

        // KPI 6: [NOVO] Taxa de Conclusão de Objetivos
        double objetivosConcluidos = (double) objetivos.stream()
                .filter(o -> "Concluído".equals(o.getStatus()))
                .count();

        double taxaConclusao = (totalObjetivos == 0) ? 0 : (objetivosConcluidos / totalObjetivos);
        lblTaxaConclusaoObjetivos.setText(String.format("%.1f%%", taxaConclusao * 100));
    }

    // (Original)
    private void atualizarGraficoPdisPorAno(List<PDI> pdis) {
        Map<String, Long> contagemPorAno = pdis.stream()
                .collect(Collectors.groupingBy(
                        pdi -> pdi.getDataCriacao().substring(6, 10),
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("PDIs Criados");

        contagemPorAno.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        lineChartPdisPorAno.getData().clear();
        lineChartPdisPorAno.getData().add(series);
    }

    // (Original)
    private void atualizarGraficoObjetivoStatus(List<Objetivo> objetivos) {
        Map<String, Long> contagemStatus = objetivos.stream()
                .collect(Collectors.groupingBy(
                        o -> (o.getStatus() == null || o.getStatus().isEmpty()) ? "N/D" : o.getStatus(),
                        Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        contagemStatus.forEach((status, count) -> {
            pieChartData.add(new PieChart.Data(status + " (" + count + ")", count));
        });

        pieChartObjetivoStatus.setData(pieChartData);
    }

    // (Original)
    private void atualizarGraficoObjetivoFoco(List<Objetivo> objetivos) {
        Map<String, Long> contagemTipo = objetivos.stream()
                .collect(Collectors.groupingBy(
                        o -> (o.getComentarios() == null || o.getComentarios().isEmpty()) ? "Não definido" : o.getComentarios(),
                        Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        contagemTipo.forEach((tipo, count) -> {
            pieChartData.add(new PieChart.Data(tipo + " (" + count + ")", count));
        });

        pieChartObjetivoTipo.setData(pieChartData);
    }

    // [GAP 4] GRÁFICO DE PDIS POR STATUS (ADICIONADO)
    private void atualizarGraficoPDIStatus(List<PDI> pdis) {
        Map<String, Long> contagemStatus = pdis.stream()
                .collect(Collectors.groupingBy(
                        pdi -> (pdi.getStatus() == null || pdi.getStatus().isEmpty()) ? "N/D" : pdi.getStatus(),
                        Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        contagemStatus.forEach((status, count) -> {
            pieChartData.add(new PieChart.Data(status + " (" + count + ")", count));
        });

        pieChartPDIStatus.setData(pieChartData);
    }

    // [GAP 5] GRÁFICO DE USUÁRIOS POR SETOR (ADICIONADO)
    private void atualizarGraficoUsuariosPorSetor(List<Usuario> usuarios) {
        // Usa o cache de setores para obter os nomes
        Map<String, String> mapaSetores = cacheTodosOsSetores.stream()
                .collect(Collectors.toMap(Setor::getId, Setor::getNome));

        // Agrupa os usuários (já filtrados) pelo nome do setor
        Map<String, Long> contagemPorSetor = usuarios.stream()
                .collect(Collectors.groupingBy(
                        u -> mapaSetores.getOrDefault(u.getSetor_id(), "Sem Setor"),
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nº de Usuários");

        // Adiciona os dados ao gráfico de barras
        contagemPorSetor.forEach((nomeSetor, count) -> {
            series.getData().add(new XYChart.Data<>(nomeSetor, count));
        });

        barChartUsuariosPorSetor.getData().clear();
        barChartUsuariosPorSetor.getData().add(series);
    }


    // (Original)
    private void atualizarGraficoProgressoSetor(List<PDI> pdis, List<Usuario> usuarios) {
        Map<String, String> mapaSetores = cacheTodosOsSetores.stream()
                .collect(Collectors.toMap(Setor::getId, Setor::getNome));

        Map<String, String> mapaUsuarioSetor = usuarios.stream()
                .filter(u -> u.getSetor_id() != null && !u.getSetor_id().isEmpty())
                .collect(Collectors.toMap(Usuario::getId, Usuario::getSetor_id, (id1, id2) -> id1));

        Map<String, Double> mediaPorSetor = pdis.stream()
                .filter(pdi -> mapaUsuarioSetor.containsKey(pdi.getColaboradorId()))
                .collect(Collectors.groupingBy(
                        pdi -> mapaSetores.getOrDefault(mapaUsuarioSetor.get(pdi.getColaboradorId()), "Outros"),
                        Collectors.averagingDouble(pdi -> pdi.getPontuacaoGeral() * 100)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Progresso Médio");

        mediaPorSetor.forEach((nomeSetor, media) -> {
            series.getData().add(new XYChart.Data<>(nomeSetor, media));
        });

        barChartProgressoSetor.getData().clear();
        barChartProgressoSetor.getData().add(series);
    }

    private void configurarTabelaRanking() {
        colRankNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colRankLiderados.setCellValueFactory(new PropertyValueFactory<>("liderados"));
        colRankPDIs.setCellValueFactory(new PropertyValueFactory<>("totalPDIs"));
        colRankProgresso.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.format("%.1f%%", cellData.getValue().getProgressoMedio())
        ));
    }

    private void atualizarTabelaRanking(List<PDI> pdis, List<Usuario> usuarios) {
        Map<String, String> mapaSetores = cacheTodosOsSetores.stream()
                .collect(Collectors.toMap(Setor::getId, Setor::getNome));

        Map<String, String> mapaUsuarioSetor = usuarios.stream()
                .filter(u -> u.getSetor_id() != null && !u.getSetor_id().isEmpty())
                .collect(Collectors.toMap(Usuario::getId, Usuario::getSetor_id));

        Map<String, List<PDI>> pdisPorSetor = pdis.stream()
                .filter(pdi -> mapaUsuarioSetor.containsKey(pdi.getColaboradorId()))
                .collect(Collectors.groupingBy(pdi -> mapaUsuarioSetor.get(pdi.getColaboradorId())));

        rankingDataList.clear(); // Limpa a lista antes de preencher
        for (Setor setor : cacheTodosOsSetores) {
            String setorId = setor.getId();
            String nomeSetor = setor.getNome();

            long liderados = usuarios.stream()
                    .filter(u -> setorId.equals(u.getSetor_id()) && "Colaborador".equals(u.getTipo_usuario()))
                    .count();

            List<PDI> pdisDoSetor = pdisPorSetor.getOrDefault(setorId, List.of());

            int totalPDIs = pdisDoSetor.size();

            double progressoMedio = pdisDoSetor.stream()
                    .mapToDouble(PDI::getPontuacaoGeral)
                    .average()
                    .orElse(0.0) * 100;

            rankingDataList.add(new RankingData(nomeSetor, (int) liderados, totalPDIs, progressoMedio));
        }

        tableRanking.setItems(rankingDataList);
        tableRanking.refresh();
    }

    private void carregarAreasDeAtencao() {
        final double LIMITE_DESEMPENHO = 50.0;

        List<String> areasDeAtencao = rankingDataList.stream()
                .filter(r -> r.getProgressoMedio() < LIMITE_DESEMPENHO)
                .map(r -> String.format("%s (Progresso: %.1f%%)", r.getNome(), r.getProgressoMedio()))
                .collect(Collectors.toList());

        if (areasDeAtencao.isEmpty()) {
            listAreasDeAtencao.setPlaceholder(new Label("Nenhuma área com baixo desempenho."));
            listAreasDeAtencao.getItems().clear();
        } else {
            listAreasDeAtencao.setItems(FXCollections.observableArrayList(areasDeAtencao));
            listAreasDeAtencao.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #D35400; -fx-font-weight: bold;");
                    }
                }
            });
        }
    }


    private void carregarAlertas() {
        try {
            List<ObjetivoComPDI> vencidos = objetivoDAO.buscarVencidos();
            if (vencidos.isEmpty()) {
                listAlertas.setPlaceholder(new Label("Nenhuma meta vencida."));
                listAlertas.getItems().clear();
                return;
            }

            List<String> alertas = vencidos.stream()
                    .map(obj -> String.format("%s (Colaborador: %s) - Venceu em: %s",
                            obj.getDescricao(),
                            obj.getNomeUsuario(),
                            obj.getPrazoString()))
                    .collect(Collectors.toList());

            listAlertas.setItems(FXCollections.observableArrayList(alertas));

            listAlertas.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #D35400; -fx-font-weight: bold;"); // Laranja escuro
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            listAlertas.setPlaceholder(new Label("Erro ao carregar alertas."));
        }
    }

    public static class RankingData {
        private final SimpleStringProperty nome;
        private final int liderados;
        private final int totalPDIs;
        private final double progressoMedio;

        public RankingData(String nome, int liderados, int totalPDIs, double progressoMedio) {
            this.nome = new SimpleStringProperty(nome);
            this.liderados = liderados;
            this.totalPDIs = totalPDIs;
            this.progressoMedio = progressoMedio;
        }

        public String getNome() {
            return nome.get();
        }

        public int getLiderados() {
            return liderados;
        }

        public int getTotalPDIs() {
            return totalPDIs;
        }

        public double getProgressoMedio() {
            return progressoMedio;
        }
    }
}