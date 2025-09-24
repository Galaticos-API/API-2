package dao;

import factory.ConnectionFactory;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void adicionar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha, tipo_usuario, status) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());

            pstmt.executeUpdate();

            System.out.println("Usuario " + usuario.getNome() + " cadastrado com sucesso!");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> lerTodos() {
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
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
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nome, email, senha, tipo_usuario, status, data_criacao FROM usuario WHERE id = ?";
        Usuario usuario = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery();) {
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
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuario;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, tipo_usuario = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTipo_usuario());
            pstmt.setString(5, usuario.getStatus());

            pstmt.setInt(6, usuario.getId());

            return pstmt.executeUpdate() > 0;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}