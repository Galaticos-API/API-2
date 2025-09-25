package dao;

import factory.ConnectionFactory;
import modelo.Objetivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Objetivo.
 * Responsável por toda a comunicação com o banco de dados referente aos objetivo.
 *
 * IMPORTANTE: Esta classe assume que a tabela no banco de dados se chama 'objetivo'
 * e possui as seguintes colunas: id, pdi_id, descricao, prazo, status,
 * comentarios, peso, pontuacao.
 */
public class ObjetivoDAO {

    /**
     * Insere um novo objetivo no banco de dados.
     * @param objetivo O objeto a ser salvo.
     */
    public void adicionar(Objetivo objetivo) {
        String sql = "INSERT INTO objetivo (pdi_id, descricao, prazo, status, comentarios, peso, pontuacao) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, objetivo.getPdiId());
            stmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                stmt.setDate(3, new java.sql.Date(objetivo.getPrazo().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, objetivo.getStatus());
            stmt.setString(5, objetivo.getComentarios());
            stmt.setFloat(6, objetivo.getPeso());
            stmt.setFloat(7, objetivo.getPontuacao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objetivo.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar objetivo no banco de dados.", e);
        }
    }

    /**
     * Busca um objetivo pelo seu ID.
     * @param id O ID do objetivo a ser buscado.
     * @return O objeto Objetivo encontrado, ou null se não existir.
     */
    public Objetivo buscarPorId(int id) {
        String sql = "SELECT * FROM objetivo WHERE id = ?";
        Objetivo objetivo = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    objetivo = new Objetivo();
                    objetivo.setId(rs.getInt("id"));
                    objetivo.setPdiId(rs.getInt("pdi_id"));
                    objetivo.setDescricao(rs.getString("descricao"));
                    objetivo.setPrazo(rs.getDate("prazo"));
                    objetivo.setStatus(rs.getString("status"));
                    objetivo.setComentarios(rs.getString("comentarios"));
                    objetivo.setPeso(rs.getFloat("peso"));
                    objetivo.setPontuacao(rs.getFloat("pontuacao"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar objetivo por ID.", e);
        }

        return objetivo;
    }

    /**
     * Lista todos os objetivo cadastrados no banco de dados.
     * @return Uma lista de todos os objetos Objetivo.
     */
    public List<Objetivo> listarTodos() {
        List<Objetivo> objetivoList = new ArrayList<>();
        String sql = "SELECT * FROM objetivo";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Objetivo objetivo = new Objetivo();
                objetivo.setId(rs.getInt("id"));
                objetivo.setPdiId(rs.getInt("pdi_id"));
                objetivo.setDescricao(rs.getString("descricao"));
                objetivo.setPrazo(rs.getDate("prazo"));
                objetivo.setStatus(rs.getString("status"));
                objetivo.setComentarios(rs.getString("comentarios"));
                objetivo.setPeso(rs.getFloat("peso"));
                objetivo.setPontuacao(rs.getFloat("pontuacao"));

                objetivoList.add(objetivo);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os objetivo.", e);
        }

        return objetivoList;
    }

    /**
     * Atualiza os dados de um objetivo existente no banco de dados.
     * @param objetivo O objeto com os dados atualizados.
     */
    public void atualizar(Objetivo objetivo) {
        String sql = "UPDATE objetivo SET pdi_id = ?, descricao = ?, prazo = ?, status = ?, comentarios = ?, peso = ?, pontuacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objetivo.getPdiId());
            stmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                stmt.setDate(3, new java.sql.Date(objetivo.getPrazo().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, objetivo.getStatus());
            stmt.setString(5, objetivo.getComentarios());
            stmt.setFloat(6, objetivo.getPeso());
            stmt.setFloat(7, objetivo.getPontuacao());
            stmt.setInt(8, objetivo.getId()); // Condição WHERE

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o objetivo.", e);
        }
    }

    /**
     * Remove um objetivo do banco de dados pelo seu ID.
     * @param id O ID do objetivo a ser removido.
     */
    public void remover(int id) {
        String sql = "DELETE FROM objetivo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover o objetivo.", e);
        }
    }
}