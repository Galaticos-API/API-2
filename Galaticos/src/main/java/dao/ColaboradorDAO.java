package dao;

import factory.ConnectionFactory;
import modelo.Usuario;
import modelo.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO {

    private Connection connection;

    public ColaboradorDAO() {
        this.connection = ConnectionFactory.getConnection();
    }

    public void adicionar(Colaborador colaborador) {
        String sql = "INSERT INTO colaborador (nome, cpf, data_nascimento, cargo, experiencia, observacoes, gerente_id, usuario_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(colaborador.getDataNascimento()));
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getExperiencia());
            stmt.setString(6, colaborador.getObservacoes());

            if (colaborador.getGerente() != null && colaborador.getGerente().getId() != null) {
                stmt.setLong(7, colaborador.getGerente().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            if (colaborador.getUsuario() != null && colaborador.getUsuario().getId() != 0) {
                stmt.setLong(8, colaborador.getUsuario().getId());
            } else {
                throw new SQLException("O ID do usuário não pode ser nulo.");
            }

            stmt.execute();

            // Recupera o ID gerado pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    colaborador.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar colaborador: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza os dados de um colaborador existente.
     */
    public void atualizar(Colaborador colaborador) {
        String sql = "UPDATE colaborador SET nome = ?, cpf = ?, data_nascimento = ?, cargo = ?, " + "experiencia = ?, observacoes = ?, gerente_id = ?, usuario_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(colaborador.getDataNascimento()));
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getExperiencia());
            stmt.setString(6, colaborador.getObservacoes());

            if (colaborador.getGerente() != null && colaborador.getGerente().getId() != null) {
                stmt.setLong(7, colaborador.getGerente().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setLong(8, colaborador.getUsuario().getId());
            stmt.setLong(9, colaborador.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar colaborador: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        String sql = "DELETE FROM colaborador WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir colaborador: " + e.getMessage(), e);
        }
    }

    public Colaborador buscarPorId(Long id) {
        String sql = "SELECT * FROM colaborador WHERE id = ?";
        Colaborador colaborador = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    colaborador = mapearResultSetParaColaborador(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colaborador por ID: " + e.getMessage(), e);
        }
        return colaborador;
    }

    public List<Colaborador> listarTodos() {
        String sql = "SELECT * FROM colaborador";
        List<Colaborador> colaboradores = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Colaborador colaborador = mapearResultSetParaColaborador(rs);
                colaboradores.add(colaborador);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar colaboradores: " + e.getMessage(), e);
        }
        return colaboradores;
    }


    private Colaborador mapearResultSetParaColaborador(ResultSet rs) throws SQLException {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(rs.getLong("id"));
        colaborador.setNome(rs.getString("nome"));
        colaborador.setCpf(rs.getString("cpf"));
        colaborador.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        colaborador.setCargo(rs.getString("cargo"));
        colaborador.setExperiencia(rs.getString("experiencia"));
        colaborador.setObservacoes(rs.getString("observacoes"));

        // Para carregar o usuário, precisaríamos de um UsuarioDAO
        int usuarioId = rs.getInt("usuario_id");
        if (!rs.wasNull()) {
            Usuario usuario = new Usuario(); // Em um caso real: new UsuarioDAO().buscarPorId(usuarioId);
            usuario.setId(usuarioId);
            colaborador.setUsuario(usuario);
        }

        // Para carregar o gerente, usaríamos o próprio DAO recursivamente
        long gerenteId = rs.getLong("gerente_id");
        if (!rs.wasNull()) {
            Colaborador gerente = new Colaborador(); // Em um caso real: this.buscarPorId(gerenteId);
            gerente.setId(gerenteId);
            colaborador.setGerente(gerente);
        }

        return colaborador;
    }
}