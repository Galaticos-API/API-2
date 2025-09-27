package dao;

import factory.ConnectionFactory;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    Connection conn = ConnectionFactory.getConnection();

    public Usuario adicionar(Usuario usuario) throws SQLException {
        if (emailExiste(usuario.getEmail())) {
            throw new SQLException("O email '" + usuario.getEmail() + "' já está cadastrado. Tente outro.");
        }

        String sql = "INSERT INTO usuario (nome, email, senha, tipo_usuario, status) VALUES(?, ?, ?, ?, ?)";

        // A conexão não é fechada aqui, pois é controlada externamente pela transação.
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());
            pstmt.executeUpdate();

            // Recupera o ID gerado e o atribui ao objeto
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
        System.out.println("Usuario " + usuario.getNome() + " preparado para inserção na transação.");
        return usuario;
    }

    public List<Usuario> lerTodos() throws SQLException {
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipo_usuario(rs.getString("tipo_usuario"));
                usuario.setStatus(rs.getString("status"));

                Timestamp timestamp = rs.getTimestamp("data_criacao");
                if (timestamp != null) {
                    usuario.setData_criacao(timestamp.toLocalDateTime());
                }

                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario WHERE id = ?";
        Usuario usuario = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
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

    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ? LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, tipo_usuario = ?, status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());
            pstmt.setInt(6, usuario.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}