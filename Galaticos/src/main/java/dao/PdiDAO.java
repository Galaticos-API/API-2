package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PdiDAO {


    public void adicionar(PDI pdi) {
        String sql = "INSERT INTO pdi (funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdi.getFuncionarioId());
            stmt.setInt(2, pdi.getAno());
            stmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                stmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                stmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setFloat(6, pdi.getPontuacaoGeral());

            stmt.executeUpdate();
            System.out.println("PDI cadastrado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PDI> lerTodos() {
        String sql = "SELECT id, funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi";
        List<PDI> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

    public PDI buscarPorId(int id) {
        String sql = "SELECT id, funcionario_id, ano, status, data_criacao, data_fechamento, pontuacao_geral FROM pdi WHERE id = ?";
        PDI pdi = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
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

    public boolean atualizar(PDI pdi) {
        String sql = "UPDATE pdi SET funcionario_id = ?, ano = ?, status = ?, data_criacao = ?, data_fechamento = ?, pontuacao_geral = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdi.getFuncionarioId());
            stmt.setInt(2, pdi.getAno());
            stmt.setString(3, pdi.getStatus());

            if (pdi.getDataCriacao() != null) {
                stmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (pdi.getDataFechamento() != null) {
                stmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setFloat(6, pdi.getPontuacaoGeral());
            stmt.setInt(7, pdi.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM pdi WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}