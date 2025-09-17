// Local do arquivo: C:\Galaticos\src\main\java\modelo\Meta.java

package modelo;

import java.util.Date;

/**
 * Classe (proposta) que representa uma Meta ou tarefa específica para atingir um Objetivo.
 */
public class Meta {

    // --- Atributos ---
    private int id;
    private int objetivoId; // Chave estrangeira para o Objetivo
    private String descricao;
    private String status; // Ex: "A fazer", "Em andamento", "Concluída"
    private Date dataConclusao;


    // --- Construtores ---
    public Meta() {
    }

    public Meta(int id, int objetivoId, String descricao, String status, Date dataConclusao) {
        this.id = id;
        this.objetivoId = objetivoId;
        this.descricao = descricao;
        this.status = status;
        this.dataConclusao = dataConclusao;
    }

    // --- Getters e Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjetivoId() {
        return objetivoId;
    }

    public void setObjetivoId(int objetivoId) {
        this.objetivoId = objetivoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}