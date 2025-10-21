package modelo;

import java.util.Date;

public class ObjetivoComPDI extends Objetivo {

    private int pdiIdOriginal; // Renomeado para evitar conflito com getId() herdado
    private int usuarioId;
    private String nomeUsuario;

    // Construtor vazio
    public ObjetivoComPDI() {
        super();
    }

    // Getters e Setters
    public int getPdiIdOriginal() {
        return pdiIdOriginal;
    }

    public void setPdiIdOriginal(int pdiIdOriginal) {
        this.pdiIdOriginal = pdiIdOriginal;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    // Opcional: Construtor para facilitar a criação no DAO
    public ObjetivoComPDI(int id, int pdiId, String descricao, Date prazo, String status,
                          String comentarios, float peso, float pontuacao,
                          int pdiIdOriginal, int usuarioId, String nomeUsuario) {
        super(id, pdiId, descricao, prazo, status, comentarios, peso, pontuacao); // Chama construtor da superclasse
        this.pdiIdOriginal = pdiIdOriginal; // Define o ID do PDI específico desta classe
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
    }
}