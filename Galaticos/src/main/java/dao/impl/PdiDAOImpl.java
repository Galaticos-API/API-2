package dao.impl;

import dao.PdiDAO;
import modelo.PDI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PdiDAOImpl implements PdiDAO {

    private final Connection connection;

    public PdiDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(PDI pdi) {
        // SQL baseado nos atributos da sua classe PDI.java e diagrama_classes.png
        String sql = "INSERT INTO pdi (funcionarioId, ano, status, dataCriacao, dataFechamento, pontuacaoGeral) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pdi.getFuncionarioId());
            stmt.setInt(2, pdi.getAno());
            stmt.setString(3, pdi.getStatus());
            stmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));

            if (pdi.getDataFechamento() != null) {
                stmt.setDate(5, new java.sql.Date(pdi.getDataFechamento().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setFloat(6, pdi.getPontuacaoGeral());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pdi.setId(rs.getInt(1)); // Atualiza o objeto com o ID gerado pelo banco
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir PDI: " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(PDI pdi) {

    }

    @Override
    public void atualizar(PDI pdi) {
        String sql = "UPDATE pdi SET funcionarioId = ?, ano = ?, status = ?, dataCriacao = ?, dataFechamento = ?, pontuacaoGeral = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pdi.getFuncionarioId());
            stmt.setInt(2, pdi.getAno());
            stmt.setString(3, pdi.getStatus());
            stmt.setDate(4, new java.sql.Date(pdi.getDataCriacao().getTime()));
            stmt.setDate(5, pdi.getDataFechamento() != null ? new java.sql.Date(pdi.getDataFechamento().getTime()) : null);
            stmt.setFloat(6, pdi.getPontuacaoGeral());
            stmt.setInt(7, pdi.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar PDI: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletarPorId(int id) {
        String sql = "DELETE FROM pdi WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar PDI: " + e.getMessage(), e);
        }
    }

    @Override
    public PDI buscaPorId(int id) {
        return null;
    }

    @Override
    public PDI buscarPorId(int id) {
        String sql = "SELECT * FROM pdi WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instanciarPDI(rs);
                }
                return null; // Retorna nulo se não encontrar
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDI por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PDI> buscarTodosPorFuncionario(int funcionarioId) {
        String sql = "SELECT * FROM pdi WHERE funcionarioId = ? ORDER BY ano DESC";
        List<PDI> lista = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, funcionarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarPDI(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDIs por funcionário: " + e.getMessage(), e);
        }
    }

    private PDI instanciarPDI(ResultSet rs) throws SQLException {
        PDI pdi = new PDI();
        pdi.setId(rs.getInt("id"));
        pdi.setFuncionarioId(rs.getInt("funcionarioId"));
        pdi.setAno(rs.getInt("ano"));
        pdi.setStatus(rs.getString("status"));
        pdi.setDataCriacao(rs.getDate("dataCriacao"));
        pdi.setDataFechamento(rs.getDate("dataFechamento"));
        pdi.setPontuacaoGeral(rs.getFloat("pontuacaoGeral"));
        return pdi;
    }
}