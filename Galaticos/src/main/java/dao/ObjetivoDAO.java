package dao;

import factory.ConnectionFactory;
import modelo.Objetivo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetivoDAO {


    public void adicionar(Objetivo objetivo) {
        String sql = "INSERT INTO objetivo (pdi_id, descricao, prazo, status, comentarios, peso, pontuacao) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, objetivo.getPdiId());
            pstmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                pstmt.setDate(3, new java.sql.Date(objetivo.getPrazo().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            pstmt.setString(4, objetivo.getStatus());
            pstmt.setString(5, objetivo.getComentarios());
            pstmt.setFloat(6, objetivo.getPeso());
            pstmt.setFloat(7, objetivo.getPontuacao());

            pstmt.executeUpdate();
            System.out.println("Objetivo cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Objetivo> lerTodos() {
        String sql = "SELECT id, pdi_id, descricao, prazo, status, comentarios, peso, pontuacao FROM objetivo";
        List<Objetivo> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Objetivo objetivo = new Objetivo();
                objetivo.setId(rs.getInt("id"));
                objetivo.setPdiId(rs.getInt("pdi_id"));
                objetivo.setDescricao(rs.getString("descricao"));

                Date prazo = rs.getDate("prazo");
                if (prazo != null) {
                    objetivo.setPrazo(new java.util.Date(prazo.getTime()));
                }

                objetivo.setStatus(rs.getString("status"));
                objetivo.setComentarios(rs.getString("comentarios"));
                objetivo.setPeso(rs.getFloat("peso"));
                objetivo.setPontuacao(rs.getFloat("pontuacao"));

                lista.add(objetivo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public Objetivo buscarPorId(int id) {
        String sql = "SELECT id, pdi_id, descricao, prazo, status, comentarios, peso, pontuacao FROM objetivo WHERE id = ?";
        Objetivo objetivo = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    objetivo = new Objetivo();
                    objetivo.setId(rs.getInt("id"));
                    objetivo.setPdiId(rs.getInt("pdi_id"));
                    objetivo.setDescricao(rs.getString("descricao"));

                    Date prazo = rs.getDate("prazo");
                    if (prazo != null) {
                        objetivo.setPrazo(new java.util.Date(prazo.getTime()));
                    }

                    objetivo.setStatus(rs.getString("status"));
                    objetivo.setComentarios(rs.getString("comentarios"));
                    objetivo.setPeso(rs.getFloat("peso"));
                    objetivo.setPontuacao(rs.getFloat("pontuacao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return objetivo;
    }

    public boolean atualizar(Objetivo objetivo) {
        String sql = "UPDATE objetivo SET pdi_id = ?, descricao = ?, prazo = ?, status = ?, comentarios = ?, peso = ?, pontuacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, objetivo.getPdiId());
            pstmt.setString(2, objetivo.getDescricao());

            if (objetivo.getPrazo() != null) {
                pstmt.setDate(3, new java.sql.Date(objetivo.getPrazo().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            pstmt.setString(4, objetivo.getStatus());
            pstmt.setString(5, objetivo.getComentarios());
            pstmt.setFloat(6, objetivo.getPeso());
            pstmt.setFloat(7, objetivo.getPontuacao());
            pstmt.setInt(8, objetivo.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM objetivo WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
