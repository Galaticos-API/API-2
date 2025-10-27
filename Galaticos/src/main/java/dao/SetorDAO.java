package dao;

import factory.ConnectionFactory; // A sua classe para obter conexão
import modelo.Setor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SetorDAO {

    /**
     * Adiciona um novo setor ao banco de dados.
     * Retorna o objeto Setor com o ID gerado.
     *
     * @param setor O objeto Setor a ser salvo (sem o ID).
     * @return O objeto Setor com o ID populado, ou null se a inserção falhar.
     */
    public Setor adicionar(Setor setor) {
        String sql = "INSERT INTO setor (nome, descricao) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, setor.getNome());
            pstmt.setString(2, setor.getDescricao());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        setor.setId(generatedKeys.getString(1));
                        return setor; // Retorna o objeto completo
                    }
                }
            }
        } catch (SQLException e) {
            // Trata erro de nome duplicado (UNIQUE KEY `nome_UNIQUE`)
            if (e.getSQLState().equals("23000")) { // Código SQLState para violação de constraint UNIQUE
                throw new RuntimeException("Erro ao adicionar setor: Já existe um setor com o nome '" + setor.getNome() + "'.", e);
            }
            throw new RuntimeException("Erro ao adicionar setor no banco de dados.", e);
        }
        return null; // Retorna null se não conseguir obter o ID gerado
    }

    /**
     * Busca um setor pelo seu ID.
     *
     * @param id O ID do setor a ser buscado.
     * @return O objeto Setor encontrado, ou null se não existir.
     */
    public Setor buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao FROM setor WHERE id = ?";
        Setor setor = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    setor = new Setor();
                    setor.setId(rs.getString("id"));
                    setor.setNome(rs.getString("nome"));
                    setor.setDescricao(rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar setor por ID.", e);
        }
        return setor;
    }

    /**
     * Lista todos os setores cadastrados no banco de dados.
     *
     * @return Uma lista de todos os objetos Setor.
     */
    public List<Setor> listarTodos() {
        String sql = "SELECT id, nome, descricao FROM setor ORDER BY nome ASC"; // Ordena por nome
        List<Setor> setores = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Setor setor = new Setor();
                setor.setId(rs.getString("id"));
                setor.setNome(rs.getString("nome"));
                setor.setDescricao(rs.getString("descricao"));
                setores.add(setor);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os setores.", e);
        }
        return setores;
    }

    /**
     * Atualiza os dados de um setor existente no banco de dados.
     *
     * @param setor O objeto Setor com as informações atualizadas (deve incluir o ID).
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizar(Setor setor) {
        String sql = "UPDATE setor SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, setor.getNome());
            pstmt.setString(2, setor.getDescricao());
            pstmt.setString(3, setor.getId()); // Condição WHERE

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Trata erro de nome duplicado
            if (e.getSQLState().equals("23000")) {
                throw new RuntimeException("Erro ao atualizar setor: Já existe outro setor com o nome '" + setor.getNome() + "'.", e);
            }
            throw new RuntimeException("Erro ao atualizar o setor.", e);
        }
    }

    /**
     * Remove um setor do banco de dados pelo seu ID.
     * Cuidado: A exclusão pode falhar se houver usuários associados a este setor
     * (dependendo da configuração ON DELETE da chave estrangeira na tabela usuario).
     *
     * @param id O ID do setor a ser removido.
     * @return true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM setor WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            // Trata erro de chave estrangeira (se ON DELETE for RESTRICT na tabela usuario)
            if (e.getSQLState().startsWith("23")) { // Códigos 23xxx geralmente indicam violação de integridade
                throw new RuntimeException("Erro ao remover setor: Existem usuários associados a este setor. Remova ou reatribua os usuários primeiro.", e);
            }
            throw new RuntimeException("Erro ao remover o setor.", e);
        }
    }
}