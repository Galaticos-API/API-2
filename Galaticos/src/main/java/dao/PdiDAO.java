package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PdiDAO {

    /**
     * ---- MÉTODO MODIFICADO ----
     * Insere um novo PDI no banco de dados e retorna o objeto completo com o ID gerado.
     * A modificação foi necessária para que, após criar um PDI, possamos obter seu ID
     * e usá-lo para criar os objetivos associados a ele na mesma transação.
     *
     * @param pdi O objeto PDI a ser salvo.
     * @return O objeto PDI salvo, incluindo o ID gerado pelo banco de dados, ou null se a inserção falhar.
     */
    public PDI adicionar(PDI pdi) {
        // ALTERAÇÃO AQUI: troque 'colaborador_id' por 'colaborador_id'
        String sql = "INSERT INTO pdi (colaborador_id, ano, status, data_criacao, data_fechamento, pontuacao_geral) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // ALTERAÇÃO AQUI: use o getter correto (getColaboradorId)
            pstmt.setInt(1, pdi.getColaboradorId());
            pstmt.setInt(2, pdi.getAno());
            pstmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() == null) {
                pdi.setDataCriacao(new java.util.Date());
            }
            pstmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));

            if (pdi.getDataFechamento() != null) {
                pstmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setFloat(6, pdi.getPontuacaoGeral());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pdi.setId(generatedKeys.getInt(1));
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
     * @return Uma lista de todos os PDIs.
     */
    public List<PDI> lerTodos() {
        String sql = "SELECT id, colaborador_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PDI pdi = new PDI();
                pdi.setId(rs.getInt("id"));
                pdi.setColaboradorId(rs.getInt("colaborador_id"));
                pdi.setAno(rs.getInt("ano"));
                pdi.setStatus(rs.getString("status"));

                Date criacao = rs.getDate("data_criacao");
                if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                Date fechamento = rs.getDate("data_fechamento");
                if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));

                lista.add(pdi);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    /**
     * Busca um PDI específico pelo seu ID.
     * @param id O ID do PDI a ser buscado.
     * @return O objeto PDI encontrado, ou null se não existir.
     */
    public PDI buscarPorId(int id) {
        String sql = "SELECT id, colaborador_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE id = ?";
        PDI pdi = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    pdi = new PDI();
                    pdi.setId(rs.getInt("id"));
                    pdi.setColaboradorId(rs.getInt("colaborador_id"));
                    pdi.setAno(rs.getInt("ano"));
                    pdi.setStatus(rs.getString("status"));

                    Date criacao = rs.getDate("data_criacao");
                    if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                    Date fechamento = rs.getDate("data_fechamento");
                    if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                    pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pdi;
    }


    public List<PDI> buscarPorColaborador(int colaboradorId) {
        String sql = "SELECT id, colaborador_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE colaborador_id =?";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, colaboradorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PDI pdi = new PDI();
                    pdi.setId(rs.getInt("id"));
                    pdi.setColaboradorId(rs.getInt("colaborador_id"));
                    pdi.setAno(rs.getInt("ano"));
                    pdi.setStatus(rs.getString("status"));

                    Date criacao = rs.getDate("data_criacao");
                    if (criacao!= null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                    Date fechamento = rs.getDate("data_fechamento");
                    if (fechamento!= null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                    pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));

                    lista.add(pdi);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDIs por colaborador.", e);
        }
        return lista;
    }


    /**
     * Atualiza os dados de um PDI existente no banco de dados.
     * @param pdi O objeto PDI com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizar(PDI pdi) {
        String sql = "UPDATE pdi SET colaborador_id = ?, ano = ?, status = ?, data_criacao = ?, data_fechamento = ?, pontuacao_geral = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pdi.getColaboradorId());
            pstmt.setInt(2, pdi.getAno());
            pstmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                pstmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                pstmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setFloat(6, pdi.getPontuacaoGeral());
            pstmt.setInt(7, pdi.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deleta um PDI do banco de dados com base no seu ID.
     * @param id O ID do PDI a ser deletado.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletar(int id) {
        String sqlObjetivos = "DELETE FROM objetivo WHERE pdi_id = ?";
        String sqlPdi = "DELETE FROM pdi WHERE id = ?";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtObjetivos = conn.prepareStatement(sqlObjetivos)) {
                pstmtObjetivos.setInt(1, id);
                pstmtObjetivos.executeUpdate();
            }

            try (PreparedStatement pstmtPdi = conn.prepareStatement(sqlPdi)) {
                pstmtPdi.setInt(1, id);
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