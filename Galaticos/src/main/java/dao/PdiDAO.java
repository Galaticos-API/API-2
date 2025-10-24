package dao;

import factory.ConnectionFactory;
import modelo.ObjetivoComPDI;
import modelo.PDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PdiDAO {

    public PDI adicionar(PDI pdi) {
        // ALTERAÇÃO AQUI: troque 'usuario_id' por 'usuario_id'

        if (this.buscarPorColaborador(pdi.getColaboradorId()) != null) {
            return null;
        }

        String sql = "INSERT INTO pdi (usuario_id, status, data_criacao, data_fechamento, pontuacao_geral) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println("ID do usuario cadastrado:" + pdi.getColaboradorId());
            pstmt.setString(1, pdi.getColaboradorId());
            pstmt.setString(2, pdi.getStatus());

            if (pdi.getDataCriacao() == null) {
                pdi.setDataCriacao(new java.util.Date());
            }
            pstmt.setString(3, pdi.getDataCriacao());

            if (pdi.getDataFechamento() != null) {
                pstmt.setString(4, pdi.getDataFechamento());
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setFloat(5, 0);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pdi.setId(generatedKeys.getString(1));
                        System.out.println("PDI cadastrado com sucesso com o ID: " + pdi.getId());
                        return pdi;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar PDI no banco de dados.", e);
        }
        return null;
    }

    // ---- MÉTODOS ORIGINAIS (SEM ALTERAÇÃO) ----

    /**
     * Lê todos os registros de PDI da tabela.
     *
     * @return Uma lista de todos os PDIs.
     */
    public List<PDI> lerTodos() {
        String sql = "SELECT pdi.*, usuario.nome AS nome_colaborador " +
                "FROM pdi " +
                "JOIN usuario ON pdi.usuario_id = usuario.id";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PDI pdi = new PDI();
                pdi.setId(rs.getString("id"));
                pdi.setColaboradorId(rs.getString("usuario_id"));
                pdi.setStatus(rs.getString("status"));

                Date criacao = rs.getDate("data_criacao");
                if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                Date fechamento = rs.getDate("data_fechamento");
                if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));

                pdi.setNomeColaborador(rs.getString("nome_colaborador"));

                lista.add(pdi);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    /**
     * Busca um PDI específico pelo seu ID.
     *
     * @param id O ID do PDI a ser buscado.
     * @return O objeto PDI encontrado, ou null se não existir.
     */
    public PDI buscarPorId(String id) {
        String sql = "SELECT id, usuario_id, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE id = ?";
        PDI pdi = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    pdi = new PDI();
                    pdi.setId(rs.getString("id"));
                    pdi.setColaboradorId(rs.getString("usuario_id"));
                    pdi.setStatus(rs.getString("status"));

                    Date criacao = rs.getDate("data_criacao");
                    if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                    Date fechamento = rs.getDate("data_fechamento");
                    if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                    pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));
                    return pdi;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pdi;
    }


    public PDI buscarPorColaborador(String colaboradorId) {
        String sql = "SELECT id, usuario_id, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE usuario_id =?";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, colaboradorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PDI pdi = new PDI();
                    pdi.setId(rs.getString("id"));
                    pdi.setColaboradorId(rs.getString("usuario_id"));
                    pdi.setStatus(rs.getString("status"));

                    Date criacao = rs.getDate("data_criacao");
                    if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                    Date fechamento = rs.getDate("data_fechamento");
                    if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                    pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));

                    return pdi;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDIs por colaborador.", e);
        }
        return null;
    }


    /**
     * Atualiza os dados de um PDI existente no banco de dados.
     *
     * @param pdi O objeto PDI com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizar(PDI pdi) {
        String sql = "UPDATE pdi SET usuario_id = ?, ano = ?, status = ?, data_criacao = ?, data_fechamento = ?, pontuacao_geral = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pdi.getColaboradorId());
            pstmt.setString(2, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                pstmt.setString(3, pdi.getDataCriacao());
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                pstmt.setString(4, pdi.getDataFechamento());
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setFloat(5, pdi.getPontuacaoGeral());
            pstmt.setString(6, pdi.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deleta um PDI do banco de dados com base no seu ID.
     *
     * @param id O ID do PDI a ser deletado.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletar(String id) {
        String sqlObjetivos = "DELETE FROM objetivo WHERE pdi_id = ?";
        String sqlPdi = "DELETE FROM pdi WHERE id = ?";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtObjetivos = conn.prepareStatement(sqlObjetivos)) {
                pstmtObjetivos.setString(1, id);
                pstmtObjetivos.executeUpdate();
            }

            try (PreparedStatement pstmtPdi = conn.prepareStatement(sqlPdi)) {
                pstmtPdi.setString(1, id);
                int affectedRows = pstmtPdi.executeUpdate();

                conn.commit();
                return affectedRows > 0;
            }

        } catch (SQLException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao reverter a transação após falha na deleção.", ex);
                }
            }
            throw new RuntimeException("Erro ao deletar PDI e seus objetivos.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}