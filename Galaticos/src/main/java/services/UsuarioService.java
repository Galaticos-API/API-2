package services;

import dao.ColaboradorDAO;
import dao.UsuarioDAO;
import factory.ConnectionFactory;
import modelo.Colaborador;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.SQLException;

// Esta classe orquestra a operação de cadastro.
public class UsuarioService {

    public void cadastrarUsuarioEColaborador(Usuario usuario, Colaborador colaborador) {
        Connection conn = null;
        try {
            // 1. Pega uma única conexão para toda a operação
            conn = ConnectionFactory.getConnection();

            // 2. DESLIGA O AUTO-COMMIT -> Inicia a transação
            conn.setAutoCommit(false);

            // 3. Instancia os DAOs
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

            usuarioDAO.adicionar(usuario, conn);
            colaborador.setUsuario(usuario);
            colaboradorDAO.adicionar(colaborador, conn);

            conn.commit();

            System.out.println("Usuário e Colaborador cadastrados com sucesso em uma transação.");

        } catch (Exception e) {
            // 7. Se QUALQUER exceção ocorreu, DESFAZ a transação
            System.err.println("Erro na transação. Executando rollback...");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro crítico ao tentar executar rollback: " + ex.getMessage());
                }
            }
            // Lança a exceção para que o Controller possa pegá-la e mostrar o alerta
            throw new RuntimeException("Erro ao cadastrar: " + e.getMessage(), e);

        } finally {
            // 8. No final, sempre fecha a conexão
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                }
            }
        }
    }

    public void deletarUsuarioEColaborador(int usuarioId) {
        Connection conn = null;
        try {
            // 1. Obtém uma conexão com o banco
            conn = ConnectionFactory.getConnection();

            // 2. Inicia a transação desativando o auto-commit
            conn.setAutoCommit(false);

            // 3. Instancia os DAOs
            ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // 4. EXECUTA AS OPERAÇÕES NA ORDEM CORRETA
            // Primeiro, deleta o registro da tabela filha (colaborador)
            colaboradorDAO.deletar(usuarioId, conn);

            // Depois, deleta o registro da tabela pai (usuario)
            boolean usuarioDeletado = usuarioDAO.deletar(usuarioId, conn);

            if (!usuarioDeletado) {
                throw new SQLException("O usuário com ID " + usuarioId + " não foi encontrado e não pôde ser deletado.");
            }

            // 5. Se tudo correu bem, confirma a transação
            conn.commit();
            System.out.println("Transação de exclusão concluída com sucesso.");

        } catch (SQLException e) {
            // 6. Se qualquer erro ocorreu, reverte TODAS as operações
            System.err.println("Erro na transação de exclusão. Executando rollback...");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Erro crítico, logar ou tratar de forma apropriada
                    System.err.println("Falha crítica ao tentar reverter a transação: " + ex.getMessage());
                }
            }
            // Lança a exceção para a camada de apresentação (Controller)
            throw new RuntimeException("Erro ao deletar usuário: " + e.getMessage(), e);

        } finally {
            // 7. Garante que a conexão seja sempre fechada
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                }
            }
        }
    }
}