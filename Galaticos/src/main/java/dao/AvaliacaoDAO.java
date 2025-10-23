package dao;

import factory.ConnectionFactory;
import modelo.Avaliacao;

import java.sql.*;

public class AvaliacaoDAO {
    public void adicionar(Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacao (id_objetivo, id_avaliador, nota, comentario, status_objetivo, data_avaliacao, criado_em) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, avaliacao.getObjetivoId());
            pstmt.setInt(2, avaliacao.getAvaliadorId());
            pstmt.setDouble(3, avaliacao.getNota());
            pstmt.setString(4, avaliacao.getComentario());
            pstmt.setString(5, avaliacao.getStatus_objetivo());

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(6, agora);
            pstmt.setTimestamp(7, agora);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    avaliacao.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar avaliação no banco de dados.", e);
        }
    }
}