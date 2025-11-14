package gui;

import javafx.beans.property.SimpleStringProperty; // Import adicionado para SimpleStringProperty
import javafx.beans.value.ObservableValue;
import modelo.Objetivo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
// Importação de java.util.Date não é mais necessária para a coluna
// import java.util.Date;

// Imports do Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportacaoController {

    // --- ELEMENTOS FXML ---
    @FXML
    private TableView<Objetivo> tabelaObjetivos;

    // Colunas (Tipos revisados)
    @FXML private TableColumn<Objetivo, Integer> colunaId;
    @FXML private TableColumn<Objetivo, String> colunaPdiId;
    @FXML private TableColumn<Objetivo, String> colunaDescricao;

    // ** CORREÇÃO AQUI: Mudado para String para evitar conflitos de Date e usar formatação pronta **
    @FXML private TableColumn<Objetivo, String> colunaPrazo;

    @FXML private TableColumn<Objetivo, String> colunaStatus;
    @FXML private TableColumn<Objetivo, String> colunaComentarios;
    @FXML private TableColumn<Objetivo, Float> colunaPeso;
    @FXML private TableColumn<Objetivo, Float> colunaPontuacao;

    @FXML
    private Button btnExportar; // O botão que o usuário clicará

    // ----------------------------------------------------------------------

    @FXML
    public void initialize() {
        // 1. Configurar as Colunas (Binding com as Properties da classe Objetivo)

        colunaId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colunaPdiId.setCellValueFactory(cellData -> cellData.getValue().pdiIdProperty());
        colunaDescricao.setCellValueFactory(cellData -> cellData.getValue().descricaoProperty());

        // ** CORREÇÃO AQUI: Usando getPrazoString() e SimpleStringProperty **
        colunaPrazo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrazoString()));
        // A linha anterior com casting (ObservableValue<Date>) foi removida/corrigida.

        colunaStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colunaComentarios.setCellValueFactory(cellData -> cellData.getValue().comentariosProperty());
        colunaPeso.setCellValueFactory(cellData -> cellData.getValue().pesoProperty().asObject());
        colunaPontuacao.setCellValueFactory(cellData -> cellData.getValue().pontuacaoProperty().asObject());

        // 2. Ligar o botão de exportar ao método
        btnExportar.setOnAction(event -> exportarParaExcel());
    }

    // ----------------------------------------------------------------------

    /**
     * Lógica principal para exportar os dados da TableView para um arquivo Excel (XLSX).
     */
    private void exportarParaExcel() {
        // Obter os dados atuais da TableView
        ObservableList<Objetivo> dados = tabelaObjetivos.getItems();

        if (dados.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não há dados para exportar.");
            return;
        }

        // 1. Configurar FileChooser para escolher o local de salvamento
        Stage stage = (Stage) btnExportar.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Objetivos");
        fileChooser.setInitialFileName("Relatorio_Objetivos.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // 2. Criar e preencher o Workbook
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Dados de Objetivos");

                escreverCabecalho(sheet);
                escreverDados(sheet, dados);

                // 3. Salvar o arquivo
                workbook.write(fileOut);

                // 4. Auto-ajustar a largura das colunas e fechar
                autoSizeColunas(sheet);
                workbook.close();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados exportados com sucesso para:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ----------------------------------------------------------------------

    private void escreverCabecalho(Sheet sheet) {
        String[] headers = {"ID", "PDI ID", "DESCRIÇÃO", "PRAZO", "STATUS", "COMENTÁRIOS", "PESO", "PONTUAÇÃO"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void escreverDados(Sheet sheet, ObservableList<Objetivo> dados) {
        int rowNum = 1; // Começa na linha 1 (abaixo do cabeçalho)

        for (Objetivo objetivo : dados) {
            Row row = sheet.createRow(rowNum++);

            // Preenchendo as células na ordem das colunas
            row.createCell(0).setCellValue(objetivo.getId());
            row.createCell(1).setCellValue(objetivo.getPdiId());
            row.createCell(2).setCellValue(objetivo.getDescricao());
            // Usando getPrazoString() que faz a formatação da data
            row.createCell(3).setCellValue(objetivo.getPrazoString());
            row.createCell(4).setCellValue(objetivo.getStatus());
            row.createCell(5).setCellValue(objetivo.getComentarios());
            row.createCell(6).setCellValue(objetivo.getPeso());
            row.createCell(7).setCellValue(objetivo.getPontuacao());

            // Note: Se você precisa de formatação específica para Peso/Pontuacao no Excel
            // você precisará usar CellStyle do Apache POI, mas para o valor simples, float funciona.
        }
    }

    private void autoSizeColunas(Sheet sheet) {
        // Ajusta automaticamente a largura das colunas
        if (sheet.getRow(0) != null) {
            int numCols = sheet.getRow(0).getLastCellNum();
            for(int i = 0; i < numCols; i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}