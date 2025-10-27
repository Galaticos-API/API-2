package dao;

import factory.ConnectionFactory;
import modelo.Documento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DocumentoDAO {
    public void adicionar(Documento documento) {
        String sql = "INSERT INTO documento (pdi_id, nome_arquivo, caminho_arquivo, data_upload, tipo_documento) VALUES (? ,? ,? ,? ,?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, documento.getPdi_id());
            pstmt.setString(2, documento.getNome());
            pstmt.setString(3, documento.getCaminhoArquivo());

            if (documento.getDataUpload() == null) {
                documento.setDataUpload(new Date());
            }
            pstmt.setTimestamp(4, new java.sql.Timestamp(documento.getDataUpload().getTime()));
            pstmt.setString(5, documento.getTipo());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    documento.setId(rs.getInt(1)); // Define o ID no objeto
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar documento no banco de dados.", e);
        }
    }

    public List<Documento> buscarPorPdiId(String pdiId) {
        List<Documento> documentosDoPdi = new ArrayList<>();
        // a coluna no banco é int, mas PDI.getId() é String. Converter ou ajustar dps o SQL/DAO.
        String sql = "SELECT * FROM documento WHERE pdi_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(pdiId)); // Converte String para int

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Documento doc = new Documento();
                    doc.setId(rs.getInt("id"));
                    doc.setPdi_id(rs.getInt("pdi_id"));
                    doc.setNome(rs.getString("nome_arquivo"));
                    doc.setCaminhoArquivo(rs.getString("caminho_arquivo"));
                    doc.setDataUpload(rs.getTimestamp("data_upload"));
                    doc.setTipo(rs.getString("tipo_documento"));

                    documentosDoPdi.add(doc);
                }
            }

        } catch (SQLException | NumberFormatException e) { // Captura erro de conversão também
            throw new RuntimeException("Erro ao buscar documentos por ID do PDI.", e);
        }
        return documentosDoPdi;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM documento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover o documento.", e);
        }
    }
}
