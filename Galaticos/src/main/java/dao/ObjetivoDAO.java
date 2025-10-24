package dao;

import factory.ConnectionFactory;
import modelo.Objetivo;
import modelo.ObjetivoComPDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Objetivo.
 * Responsável por toda a comunicação com o banco de dados referente aos objetivo.
 * <p>
 * IMPORTANTE: Esta classe assume que a tabela no banco de dados se chama 'objetivo'
 * e possui as seguintes colunas: id, pdi_id, descricao, prazo, status,
 * comentarios, peso, pontuacao.
 */
public class ObjetivoDAO {

    /**
     * Insere um novo objetivo no banco de dados.
     *
     * @param objetivo O objeto a ser salvo.
     */
    public void adicionar(Objetivo objetivo) {
        String sql = "INSERT INTO objetivo (pdi_id, descricao, prazo, status, comentarios, peso, pontuacao) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, objetivo.getPdiId());
            stmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                stmt.setString(3, objetivo.getPrazo());
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
     *
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
     *
     * @return Uma lista de todos os objetos Objetivo.
     */
    public static List<Objetivo> lerTodos() {
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
     * Busca todos os objetivos associados a um PDI específico.
     *
     * @param pdiId O ID do PDI cujos objetivos devem ser encontrados.
     * @return Uma lista de objetos Objetivo pertencentes ao PDI, ou uma lista vazia se nenhum for encontrado.
     */
    public List<Objetivo> buscarPorPdiId(String pdiId) {
        List<Objetivo> objetivosDoPdi = new ArrayList<>();
        String sql = "SELECT * FROM objetivo WHERE pdi_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pdiId);

            try (ResultSet rs = stmt.executeQuery()) {
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

                    objetivosDoPdi.add(objetivo);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar objetivos por ID do PDI.", e);
        }
        return objetivosDoPdi;
    }

    /**
     * Atualiza os dados de um objetivo existente no banco de dados.
     *
     * @param objetivo O objeto com os dados atualizados.
     */
    public void atualizar(Objetivo objetivo) {
        String sql = "UPDATE objetivo SET pdi_id = ?, descricao = ?, prazo = ?, status = ?, comentarios = ?, peso = ?, pontuacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, objetivo.getPdiId());
            stmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                stmt.setString(3, objetivo.getPrazo());
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
     *
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

    public List<ObjetivoComPDI> listarTodosComPDI() {
        List<ObjetivoComPDI> listaCompleta = new ArrayList<>();
        // Query com JOINs para buscar dados das 3 tabelas
        String sql = "SELECT o.*, p.id as pdi_original_id, p.usuario_id, u.nome as nome_usuario " +
                "FROM objetivo o " +
                "JOIN pdi p ON o.pdi_id = p.id " +
                "JOIN usuario u ON p.usuario_id = u.id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ObjetivoComPDI obj = new ObjetivoComPDI();

                // Popula os campos herdados de Objetivo
                obj.setId(rs.getInt("o.id"));
                obj.setPdiId(rs.getInt("o.pdi_id")); // ID do PDI ao qual o objetivo pertence diretamente
                obj.setDescricao(rs.getString("o.descricao"));
                obj.setPrazo(rs.getDate("o.prazo")); // Retorna java.sql.Date
                obj.setStatus(rs.getString("o.status"));
                obj.setComentarios(rs.getString("o.comentarios"));
                obj.setPeso(rs.getFloat("o.peso"));
                obj.setPontuacao(rs.getFloat("o.pontuacao"));

                // Popula os campos específicos de ObjetivoComPDI
                obj.setPdiIdOriginal(rs.getInt("pdi_original_id")); // ID do PDI vindo da tabela PDI
                obj.setUsuarioId(rs.getInt("p.usuario_id"));
                obj.setNomeUsuario(rs.getString("nome_usuario"));

                listaCompleta.add(obj);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os objetivos com informações do PDI.", e);
        }

        return listaCompleta;
    }
}