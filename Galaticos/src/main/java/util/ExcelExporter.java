package util;

import modelo.PDI;
import modelo.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---------------------------------------------
    // EXPORTA USUÁRIOS
    // ---------------------------------------------
    public static void exportarUsuarios(List<Usuario> usuarios, File destino) throws Exception {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Usuários");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nome");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Tipo");
        header.createCell(4).setCellValue("Status");
        header.createCell(5).setCellValue("Setor ID");
        header.createCell(6).setCellValue("Data Criação");

        int rowNum = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getNome());
            row.createCell(2).setCellValue(u.getEmail());
            row.createCell(3).setCellValue(u.getTipo_usuario());
            row.createCell(4).setCellValue(u.getStatus());
            row.createCell(5).setCellValue(u.getSetor_id());

            if (u.getData_criacao() != null)
                row.createCell(6).setCellValue(u.getData_criacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            else
                row.createCell(6).setCellValue("");
        }

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            wb.write(fos);
        }
        wb.close();
    }

    // ---------------------------------------------
    // EXPORTA PDIs
    // ---------------------------------------------
    public static void exportarPdis(List<PDI> lista, File destino) throws Exception {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("PDIs");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Colaborador ID");
        header.createCell(2).setCellValue("Nome");
        header.createCell(3).setCellValue("Status");
        header.createCell(4).setCellValue("Data Criação");
        header.createCell(5).setCellValue("Data Término");
        header.createCell(6).setCellValue("Pontuação (%)");

        int rowNum = 1;

        for (PDI p : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getColaboradorId());
            row.createCell(2).setCellValue(p.getNomeColaborador());
            row.createCell(3).setCellValue(p.getStatus());

            row.createCell(4).setCellValue(p.getDataCriacao() != null ? p.getDataCriacao() : "");
            row.createCell(5).setCellValue(p.getDataFechamento() != null ? p.getDataFechamento() : "");
            row.createCell(6).setCellValue(p.getPontuacaoGeral());
        }

        try (FileOutputStream fos = new FileOutputStream(destino)) {
            wb.write(fos);
        }
        wb.close();
    }
}

