package dao;

import modelo.Usuario;
import modelo.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO {

    public void adicionar(Colaborador colaborador, Connection conn) throws SQLException {
        String sql = "INSERT INTO colaborador (nome, cpf, data_nascimento, cargo, experiencia, observacoes, gerente_id, usuario_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            if (colaborador.getDataNascimento() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(colaborador.getDataNascimento()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
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
                throw new SQLException("A associação com o usuário é obrigatória e o ID do usuário não pode ser nulo.");
            }

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    colaborador.setId(rs.getLong(1));
                }
            }
        }
        System.out.println("Colaborador " + colaborador.getNome() + " preparado para inserção na transação.");
    }

    public void atualizar(Colaborador colaborador, Connection conn) throws SQLException {
        String sql = "UPDATE colaborador SET nome = ?, cpf = ?, data_nascimento = ?, cargo = ?, " + "experiencia = ?, observacoes = ?, gerente_id = ?, usuario_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, Date.valueOf(colaborador.getDataNascimento()));
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

        }
    }

    public void deletar(int usuario_id, Connection conn) throws SQLException {
        String sql = "DELETE FROM colaborador WHERE usuario_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuario_id);
            stmt.executeUpdate();
        }
    }

    public Colaborador buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM colaborador WHERE id = ?";
        Colaborador colaborador = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    colaborador = mapearResultSetParaColaborador(rs);
                }
            }
        }
        return colaborador;
    }

    public List<Colaborador> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM colaborador";
        List<Colaborador> colaboradores = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Colaborador colaborador = mapearResultSetParaColaborador(rs);
                colaboradores.add(colaborador);
            }
        }
        return colaboradores;
    }

    private Colaborador mapearResultSetParaColaborador(ResultSet rs) throws SQLException {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(rs.getLong("id"));
        colaborador.setNome(rs.getString("nome"));
        colaborador.setCpf(rs.getString("cpf"));
        Date dataNascimentoSql = rs.getDate("data_nascimento");
        if (dataNascimentoSql != null) {
            colaborador.setDataNascimento(dataNascimentoSql.toLocalDate());
        }
        colaborador.setCargo(rs.getString("cargo"));
        colaborador.setExperiencia(rs.getString("experiencia"));
        colaborador.setObservacoes(rs.getString("observacoes"));

        int usuarioId = rs.getInt("usuario_id");
        if (!rs.wasNull()) {
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            colaborador.setUsuario(usuario);
        }

        long gerenteId = rs.getLong("gerente_id");
        if (!rs.wasNull()) {
            Colaborador gerente = new Colaborador();
            gerente.setId(gerenteId);
            colaborador.setGerente(gerente);
        }
        return colaborador;
    }
}