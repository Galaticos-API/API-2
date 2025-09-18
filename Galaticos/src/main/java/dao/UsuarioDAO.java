package dao;

import factory.ConnectionFactory;
import modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void adicionar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome) VALUES(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNome());
            pstmt.executeUpdate();

            System.out.println("Cliente " + cliente.getNome() + " cadastrado com sucesso!");

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Cliente> lerTodos() {
        String sql = "SELECT codigo, nome FROM cliente";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNome(rs.getString("nome"));
                cliente.setCodigo(rs.getInt("codigo"));
                clientes.add(cliente);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clientes;
    }

    public Cliente buscarPorId(int codigo) {
        String sql = "SELECT codigo, nome FROM cliente WHERE codigo = ?";
        Cliente cliente = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);

            try (ResultSet rs = pstmt.executeQuery();) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setCodigo(rs.getInt("codigo"));
                    cliente.setNome(rs.getString("nome"));
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cliente;
    }

    public boolean atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ? WHERE codigo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNome());
            pstmt.setInt(2, cliente.getCodigo());

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