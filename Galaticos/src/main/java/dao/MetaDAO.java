package dao;

import factory.ConnectionFactory;
import modelo.Meta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDAO {

    public void adicionar(Meta meta) {
        String sql = "INSERT INTO metas (objetivo_id, descricao, status, data_conclusao) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, meta.getObjetivoId());
            stmt.setString(2, meta.getDescricao());
            stmt.setString(3, meta.getStatus());

            if (meta.getDataConclusao() != null) {
                stmt.setDate(4, new java.sql.Date(meta.getDataConclusao().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    meta.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar meta: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public Meta buscarPorId(int id) {
        String sql = "SELECT * FROM metas WHERE id = ?";
        Meta meta = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    meta = new Meta();
                    meta.setId(rs.getInt("id"));
                    meta.setObjetivoId(rs.getInt("objetivo_id"));
                    meta.setDescricao(rs.getString("descricao"));
                    meta.setStatus(rs.getString("status"));
                    meta.setDataConclusao(rs.getDate("data_conclusao"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar meta: " + e.getMessage());
            e.printStackTrace();
        }

        return meta;
    }

    // --- Listar todas ---
    public List<Meta> listarTodas() {
        List<Meta> metas = new ArrayList<>();
        String sql = "SELECT * FROM metas";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Meta meta = new Meta();
                meta.setId(rs.getInt("id"));
                meta.setObjetivoId(rs.getInt("objetivo_id"));
                meta.setDescricao(rs.getString("descricao"));
                meta.setStatus(rs.getString("status"));
                meta.setDataConclusao(rs.getDate("data_conclusao"));

                metas.add(meta);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar metas: " + e.getMessage());
            e.printStackTrace();
        }

        return metas;
    }

    // --- Atualizar ---
    public void atualizar(Meta meta) {
        String sql = "UPDATE metas SET objetivo_id = ?, descricao = ?, status = ?, data_conclusao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, meta.getObjetivoId());
            stmt.setString(2, meta.getDescricao());
            stmt.setString(3, meta.getStatus());

            if (meta.getDataConclusao() != null) {
                stmt.setDate(4, new java.sql.Date(meta.getDataConclusao().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, meta.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar meta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Remover ---
    public void remover(int id) {
        String sql = "DELETE FROM metas WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao remover meta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
