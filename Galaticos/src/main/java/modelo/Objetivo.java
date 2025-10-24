package modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Objetivo {

    // --- Atributos ---
    private int id;
    private int pdiId; // Chave estrangeira para saber a qual PDI este objetivo pertence
    private String descricao;
    private Date prazo;
    private String status;
    private String comentarios;
    private float peso;
    private float pontuacao;

    private String pattern = "dd-MM-yyyy";
    private SimpleDateFormat formatter = new SimpleDateFormat(pattern);


    // --- Construtores ---
    public Objetivo() {
    }

    public Objetivo(int id, int pdiId, String descricao, Date prazo, String status, String comentarios, float peso, float pontuacao) {
        this.id = id;
        this.pdiId = pdiId;
        this.descricao = descricao;
        this.prazo = prazo;
        this.status = status;
        this.comentarios = comentarios;
        this.peso = peso;
        this.pontuacao = pontuacao;
    }


    // --- Getters e Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPdiId() {
        return pdiId;
    }

    public void setPdiId(int pdiId) {
        this.pdiId = pdiId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrazo() {
        return formatter.format(this.prazo);
    }

    public void setPrazo(Date prazo) {
        this.prazo = prazo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public float getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(float pontuacao) {
        this.pontuacao = pontuacao;
    }
}