package dao;

import factory.ConnectionFactory;
import modelo.Avaliacao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<Avaliacao> buscarPorObjetivoId(int objetivoId) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT a.*, u.nome AS nome_avaliador " +
                "FROM avaliacao a " +
                "JOIN usuario u ON a.id_avaliador = u.id " +
                "WHERE a.id_objetivo = ? " +
                "ORDER BY a.data_avaliacao DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, objetivoId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Avaliacao aval = new Avaliacao();
                    aval.setId(rs.getInt("id"));
                    aval.setObjetivoId(rs.getInt("id_objetivo"));
                    aval.setAvaliadorId(rs.getInt("id_avaliador"));
                    aval.setNota(rs.getDouble("nota"));
                    aval.setComentario(rs.getString("comentario"));
                    aval.setStatus_objetivo(rs.getString("status_objetivo"));

                    Timestamp dataTs = rs.getTimestamp("data_avaliacao");
                    if (dataTs != null) {
                        aval.setDataAvaliacao(dataTs.toLocalDateTime().toLocalDate());
                    }

                    aval.setNomeAvaliador(rs.getString("nome_avaliador"));

                    avaliacoes.add(aval);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar avaliações por ID do objetivo.", e);
        }
        return avaliacoes;
    }
}