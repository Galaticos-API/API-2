package dao;

import modelo.PDI;
import java.util.List;

public interface PdiDAO {
    void insert(PDI pdi);
    void atualizar(PDI pdi);
    void deletarPorId(int id);
    PDI buscaPorId(int id);

    List<PDI> buscarTodosPorFuncionario(int funcionarioID);
}

