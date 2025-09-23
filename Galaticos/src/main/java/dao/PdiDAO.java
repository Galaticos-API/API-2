package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PdiDAO {

    // CREATE
    public void adicionar(PDI pdi) {
        String sql = "INSERT INTO pdi (funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pdi.getFuncionarioId());
            pstmt.setInt(2, pdi.getAno());
            pstmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                pstmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                pstmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setFloat(6, pdi.getPontuacaoGeral());

            pstmt.executeUpdate();
            System.out.println("PDI cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // READ ALL
    public List<PDI> lerTodos() {
        String sql = "SELECT id, funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PDI pdi = new PDI();
                pdi.setId(rs.getInt("id"));
                pdi.setFuncionarioId(rs.getInt("funcionario_id"));
                pdi.setAno(rs.getInt("ano"));
                pdi.setStatus(rs.getString("status"));

                Date criacao = rs.getDate("data_criacao");
                if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                Date fechamento = rs.getDate("data_fechamento");
                if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));

                lista.add(pdi);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    // READ ID
    public PDI buscarPorId(int id) {
        String sql = "SELECT id, funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE id = ?";
        PDI pdi = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    pdi = new PDI();
                    pdi.setId(rs.getInt("id"));
                    pdi.setFuncionarioId(rs.getInt("funcionario_id"));
                    pdi.setAno(rs.getInt("ano"));
                    pdi.setStatus(rs.getString("status"));

                    Date criacao = rs.getDate("data_criacao");
                    if (criacao != null) pdi.setDataCriacao(new java.util.Date(criacao.getTime()));

                    Date fechamento = rs.getDate("data_fechamento");
                    if (fechamento != null) pdi.setDataFechamento(new java.util.Date(fechamento.getTime()));

                    pdi.setPontuacaoGeral(rs.getFloat("pontuacao_geral"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pdi;
    }

    // UPDATE
    public boolean atualizar(PDI pdi) {
        String sql = "UPDATE pdi SET funcionario_id = ?, ano = ?, status = ?, data_criacao = ?, data_fechamento = ?, pontuacao_geral = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pdi.getFuncionarioId());
            pstmt.setInt(2, pdi.getAno());
            pstmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                pstmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                pstmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setFloat(6, pdi.getPontuacaoGeral());
            pstmt.setInt(7, pdi.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    public boolean deletar(int id) {
        String sql = "DELETE FROM pdi WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}