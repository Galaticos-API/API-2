package dao;

import factory.ConnectionFactory;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void adicionar(Usuario usuario) {
        String sql = "INSERT INTO cliente (nome) VALUES(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.executeUpdate();

            System.out.println("Cliente " + usuario.getNome() + " cadastrado com sucesso!");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> lerTodos() {
        String sql = "SELECT codigo, nome FROM cliente";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setNome(rs.getString("nome"));
                usuario.setCodigo(rs.getInt("codigo"));
                usuarios.add(usuario);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }

    public Usuario buscarPorId(int codigo) {
        String sql = "SELECT codigo, nome FROM cliente WHERE codigo = ?";
        Usuario usuario = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);

            try (ResultSet rs = pstmt.executeQuery();) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setCodigo(rs.getInt("codigo"));
                    usuario.setNome(rs.getString("nome"));
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuario;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE cliente SET nome = ? WHERE codigo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setInt(2, usuario.getCodigo());

            return pstmt.executeUpdate() > 0;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deletar(int codigo) {
        String sql = "DELETE FROM cliente WHERE codigo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);

            return pstmt.executeUpdate() > 0;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}