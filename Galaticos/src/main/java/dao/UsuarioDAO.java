package dao;

import factory.ConnectionFactory;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection conn;

    public UsuarioDAO() {
        this.conn = ConnectionFactory.getConnection();
    }

    public Usuario adicionar(Usuario usuario) throws SQLException {
        if (emailExiste(usuario.getEmail())) {
            throw new SQLException("O email '" + usuario.getEmail() + "' já está cadastrado. Tente outro.");
        }

        // SQL atualizado sem o campo 'cargo'
        String sql = "INSERT INTO usuario (nome, email, senha, tipo_usuario, status) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getString(1));
                }
            }
        }
        System.out.println("Usuario " + usuario.getNome() + " preparado para inserção na transação.");
        return usuario;
    }

    public List<Usuario> lerTodos() throws SQLException {
        // SQL atualizado sem o campo 'cargo'
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getString("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipo_usuario(rs.getString("tipo_usuario"));
                usuario.setStatus(rs.getString("status"));
                Timestamp timestamp = rs.getTimestamp("data_criacao");
                if (timestamp != null) {
                    usuario.setData_criacao(timestamp.toLocalDateTime());
                }

                System.out.println(usuario);

                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    public Usuario buscarPorId(String id) throws SQLException {
        // SQL atualizado sem o campo 'cargo'
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario WHERE id = ?";
        Usuario usuario = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getString("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setTipo_usuario(rs.getString("tipo_usuario"));
                    usuario.setStatus(rs.getString("status"));
                    Timestamp timestamp = rs.getTimestamp("data_criacao");
                    if (timestamp != null) {
                        usuario.setData_criacao(timestamp.toLocalDateTime());
                    }

                }
            }
        }
        return usuario;
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        // SQL atualizado sem o campo 'cargo'
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, tipo_usuario = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());

            pstmt.setString(6, usuario.getId()); // O índice foi ajustado
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ? LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean deletar(String id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha); // Lembre-se que em produção isso deve usar HASHING!

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getString("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setStatus(rs.getString("status"));
                    usuario.setTipo_usuario(rs.getString("tipo_usuario"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário.", e);
        }
        return null; // Retorna null se não encontrar a combinação de email e senha
    }
}