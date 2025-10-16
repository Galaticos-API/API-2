package dao;

import factory.ConnectionFactory;
import modelo.Avaliacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {
    public void adicionar(Avaliacao avaliacao) {
        String sql = "INSERT INTO avaliacao (objetivo_id, avaliador_id, nota, comentario, data_avaliacao) VALUES (?, ?, ?, ?, ?)";
    }
}
