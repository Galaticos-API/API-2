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
import javafx.scene.layout.BorderPane; // Importar
import javafx.scene.layout.GridPane; // Importar
import javafx.scene.layout.VBox; // Importar
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
    private BorderPane filterContainer; // Container dos filtros
    @FXML
    private ComboBox<String> comboAno;
    @FXML
    private ComboBox<Setor> comboSetor;
    @FXML
    private ComboBox<Usuario> comboGestor;
    @FXML
    private Button btnLimparFiltros;

    // --- Contêineres ---
    @FXML
    private GridPane kpiGrid;
    @FXML
    private GridPane chartsGrid1;
    @FXML
    private GridPane chartsGrid2;
    @FXML
    private GridPane bottomGrid;

    // --- Títulos dos KPIs (para Colaborador) ---
    @FXML
    private Label kpiTitle1;
    @FXML
    private Label kpiTitle2;
    @FXML
    private Label kpiTitle3;
    @FXML
    private Label kpiTitle4;
    @FXML
    private Label kpiTitle5;
    @FXML
    private VBox kpiBox6;

    // --- KPIs ---
    @FXML
    private Label lblTotalPdisAtivos;
    @FXML
    private Label lblUsuariosAtivos;
    @FXML
    private Label lblMediaConclusao;
    @FXML
    private Label lblObjetivosPendentes;
    @FXML
    private Label lblMediaObjetivosPDI;
    @FXML
    private Label lblTaxaConclusaoObjetivos;

    // --- Gráficos ---
    @FXML
    private LineChart<String, Number> lineChartPdisPorAno;
    @FXML
    private BarChart<String, Number> barChartProgressoSetor;
    @FXML
    private PieChart pieChartObjetivoStatus;
    @FXML
    private PieChart pieChartObjetivoTipo;
    @FXML
    private BarChart<String, Number> barChartUsuariosPorSetor;
    @FXML
    private PieChart pieChartPDIStatus;

    // --- VBox dos Gráficos (para esconder) ---
    @FXML
    private VBox chartBoxPdisPorAno;
    @FXML
    private VBox chartBoxProgressoSetor;
    @FXML
    private VBox chartBoxUsuariosPorSetor;
    @FXML
    private VBox chartBoxObjetivoTipo;
    @FXML
    private VBox chartBoxObjetivoStatus;
    @FXML
    private VBox chartBoxPDIStatus;

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

    // ... (DAOs e Caches) ...
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

    /**
     * Ponto de entrada principal. Chamado pelo MainController.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarTelaPorPerfil();
    }

    /**
     * Carrega os caches e direciona para a configuração de tela correta.
     */
    private void configurarTelaPorPerfil() {
        // 1. Carrega todos os dados no cache
        try {
            cacheTodosOsPdis = pdiDAO.lerTodos();
            cacheTodosOsUsuarios = usuarioDAO.lerTodos();
            cacheTodosOsObjetivos = objetivoDAO.lerTodos();
            cacheTodosOsSetores = setorDAO.listarTodos();
            cacheGestores = cacheTodosOsUsuarios.stream()
                    .filter(u -> "Gestor de Area".equals(u.getTipo_usuario()) || "Gestor Geral".equals(u.getTipo_usuario()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            Util.mostrarAlerta(javafx.scene.control.Alert.AlertType.ERROR, "Erro Crítico", "Não foi possível carregar os dados do banco: " + e.getMessage());
            return;
        }

        // 2. Direciona para o setup correto
        String tipoUsuario = usuarioLogado.getTipo_usuario();
        switch (tipoUsuario) {
            case "RH":
            case "Gestor Geral":
                setupDashboardRhGeral();
                break;
            case "Gestor de Area":
                setupDashboardGestorArea();
                break;
            case "Colaborador":
                setupDashboardColaborador();
                break;
            default:
                // Esconder tudo se o tipo for desconhecido
                setAllComponentsVisibility(false);
                lblTitulo.setText("Dashboard");
                break;
        }
    }

    /**
     * Configura a visão completa para RH e Gestor Geral.
     */
    private void setupDashboardRhGeral() {
        if ("Gestor Geral".equals(usuarioLogado.getTipo_usuario())) {
            lblTitulo.setText("Dashboard (Visão Gestor Geral)");
        } else {
            lblTitulo.setText("Dashboard (Visão RH)");
        }

        setAllComponentsVisibility(true); // Garante que tudo está visível

        configurarTabelaRanking();
        populateFilters();
        setupFiltroListeners();
        atualizarDashboard(); // Atualiza com dados (filtrados ou não)
        carregarAlertas();
    }

    /**
     * Configura a visão filtrada para o Gestor de Área.
     */
    private void setupDashboardGestorArea() {
        String nomeSetor = cacheTodosOsSetores.stream()
                .filter(s -> s.getId().equals(usuarioLogado.getSetor_id()))
                .map(Setor::getNome)
                .findFirst()
                .orElse("Meu Setor");
        lblTitulo.setText("Dashboard do Setor: " + nomeSetor);

        // 1. Esconde componentes globais
        setAllComponentsVisibility(true); // Começa com tudo visível
        filterContainer.setVisible(false); // Esconde a barra de filtro global
        filterContainer.setManaged(false);
        chartBoxProgressoSetor.setVisible(false); // Esconde "Progresso por Setor" (não faz sentido)
        chartBoxProgressoSetor.setManaged(false);
        bottomGrid.setVisible(false); // Esconde Ranking de Setores e Alertas Globais
        bottomGrid.setManaged(false);
        // Opcional: Esconder "Usuários por Setor"
        // chartBoxUsuariosPorSetor.setVisible(false);
        // chartBoxUsuariosPorSetor.setManaged(false);

        // 2. Carrega dados filtrados para o setor do gestor
        int setorId = Integer.parseInt(usuarioLogado.getSetor_id());

        List<Usuario> usuariosDoSetor = cacheTodosOsUsuarios.stream()
                .filter(u -> Integer.parseInt(u.getSetor_id()) == setorId)
                .collect(Collectors.toList());

        List<String> idsUsuariosDoSetor = usuariosDoSetor.stream()
                .map(Usuario::getId)
                .collect(Collectors.toList());

        List<PDI> pdisDoSetor = cacheTodosOsPdis.stream()
                .filter(pdi -> idsUsuariosDoSetor.contains(pdi.getColaboradorId()))
                .collect(Collectors.toList());

        List<String> idsPdisDoSetor = pdisDoSetor.stream().map(PDI::getId).collect(Collectors.toList());

        List<Objetivo> objetivosDoSetor = cacheTodosOsObjetivos.stream()
                .filter(obj -> idsPdisDoSetor.contains(obj.getPdiId()))
                .collect(Collectors.toList());

        // 3. Atualiza os componentes com os dados filtrados
        atualizarKPIs(pdisDoSetor, usuariosDoSetor, objetivosDoSetor);
        atualizarGraficoPdisPorAno(pdisDoSetor); // Histórico do setor
        atualizarGraficoObjetivoStatus(objetivosDoSetor); // Status dos objetivos do setor
        atualizarGraficoObjetivoFoco(objetivosDoSetor); // Foco dos objetivos do setor
        atualizarGraficoPDIStatus(pdisDoSetor); // Status dos PDIs do setor
        atualizarGraficoUsuariosPorSetor(usuariosDoSetor); // Gráfico de usuários (agora filtrado)
    }

    /**
     * Configura a visão simplificada para o Colaborador.
     */
    private void setupDashboardColaborador() {
        lblTitulo.setText("Meu Dashboard PDI");

        // 1. Esconde todos os containers complexos
        filterContainer.setVisible(false);
        filterContainer.setManaged(false);
        chartsGrid1.setVisible(false);
        chartsGrid1.setManaged(false);
        chartsGrid2.setVisible(false);
        chartsGrid2.setManaged(false);
        bottomGrid.setVisible(false);
        bottomGrid.setManaged(false);

        // 2. Garante que o grid de KPIs esteja visível
        kpiGrid.setVisible(true);
        kpiGrid.setManaged(true);

        // 3. Busca o PDI e Objetivos *apenas* deste usuário
        PDI meuPDI = cacheTodosOsPdis.stream()
                .filter(pdi -> pdi.getColaboradorId().equals(usuarioLogado.getId()))
                .findFirst()
                .orElse(null);

        if (meuPDI == null) {
            // Se não tem PDI, esconde os KPIs e mostra uma mensagem (opcional)
            kpiGrid.setVisible(false);
            kpiGrid.setManaged(false);
            lblTitulo.setText("Meu Dashboard PDI (Nenhum PDI cadastrado)");
            return;
        }

        List<Objetivo> meusObjetivos = cacheTodosOsObjetivos.stream()
                .filter(obj -> obj.getPdiId().equals(meuPDI.getId()))
                .collect(Collectors.toList());

        // 4. Re-propósito dos KPIs para o Colaborador
        kpiTitle1.setText("Status do Meu PDI");
        lblTotalPdisAtivos.setText(meuPDI.getStatus());

        kpiTitle2.setText("Meu Progresso Geral");
        lblUsuariosAtivos.setText(String.format("%.1f%%", meuPDI.getPontuacaoGeral() * 100));

        kpiTitle3.setText("Total de Objetivos");
        lblObjetivosPendentes.setText(String.valueOf(meusObjetivos.size()));

        long concluidos = meusObjetivos.stream().filter(o -> "Concluído".equals(o.getStatus())).count();
        long pendentes = meusObjetivos.size() - concluidos;

        kpiTitle4.setText("Objetivos Pendentes");
        lblMediaConclusao.setText(String.valueOf(pendentes));

        kpiTitle5.setText("Objetivos Concluídos");
        lblMediaObjetivosPDI.setText(String.valueOf(concluidos));

        // Esconde o 6º KPI
        kpiBox6.setVisible(false);
        kpiBox6.setManaged(false);

        // 5. Mostrar apenas o gráfico de status dos *seus* objetivos
        chartsGrid2.setVisible(true);
        chartsGrid2.setManaged(true); // Mostra o grid
        // Esconde os outros 2 gráficos nesse grid
        chartBoxObjetivoTipo.setVisible(false);
        chartBoxObjetivoTipo.setManaged(false);
        chartBoxPDIStatus.setVisible(false);
        chartBoxPDIStatus.setManaged(false);
        // Garante que o gráfico de status de objetivos esteja visível
        chartBoxObjetivoStatus.setVisible(true);
        chartBoxObjetivoStatus.setManaged(true);
        // Atualiza o gráfico com os dados do colaborador
        atualizarGraficoObjetivoStatus(meusObjetivos);
    }

    /**
     * Método auxiliar para ligar ou desligar todos os containers.
     */
    private void setAllComponentsVisibility(boolean isVisible) {
        filterContainer.setVisible(isVisible);
        filterContainer.setManaged(isVisible);
        kpiGrid.setVisible(isVisible);
        kpiGrid.setManaged(isVisible);
        chartsGrid1.setVisible(isVisible);
        chartsGrid1.setManaged(isVisible);
        chartsGrid2.setVisible(isVisible);
        chartsGrid2.setManaged(isVisible);
        bottomGrid.setVisible(isVisible);
        bottomGrid.setManaged(isVisible);

        // Garante que os filhos (gráficos) também estejam visíveis
        if (isVisible) {
            chartBoxPdisPorAno.setVisible(true);
            chartBoxPdisPorAno.setManaged(true);
            chartBoxProgressoSetor.setVisible(true);
            chartBoxProgressoSetor.setManaged(true);
            chartBoxUsuariosPorSetor.setVisible(true);
            chartBoxUsuariosPorSetor.setManaged(true);
            chartBoxObjetivoTipo.setVisible(true);
            chartBoxObjetivoTipo.setManaged(true);
            chartBoxObjetivoStatus.setVisible(true);
            chartBoxObjetivoStatus.setManaged(true);
            chartBoxPDIStatus.setVisible(true);
            chartBoxPDIStatus.setManaged(true);
            kpiBox6.setVisible(true);
            kpiBox6.setManaged(true);
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
        List<String> anos = cacheTodosOsPdis.stream()
                .map(pdi -> pdi.getDataCriacao().substring(6, 10))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        comboAno.setItems(FXCollections.observableArrayList(anos));

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

    private void atualizarDashboard() {
        if (cacheTodosOsPdis == null) return;

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

        ObservableList<RankingData> ranking = FXCollections.observableArrayList();
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

            ranking.add(new RankingData(nomeSetor, (int) liderados, totalPDIs, progressoMedio));
        }

        tableRanking.setItems(ranking);
        tableRanking.refresh();
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