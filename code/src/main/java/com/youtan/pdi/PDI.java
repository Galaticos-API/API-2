package com.youtan.pdi;

import java.util.Date;

public class PDI {
    private int id;
    private int funcionarioId;
    private int ano;
    private String status;
    private Date dataCriacao;
    private Date dataFechamento;
    private float pontuacaoGeral;

    // Construtor
    public PDI(int id, int funcionarioId, int ano, String status, Date dataCriacao, Date dataFechamento, float pontuacaoGeral) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.ano = ano;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataFechamento = dataFechamento;
        this.pontuacaoGeral = pontuacaoGeral;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(int funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public float getPontuacaoGeral() {
        return pontuacaoGeral;
    }

    public void setPontuacaoGeral(float pontuacaoGeral) {
        this.pontuacaoGeral = pontuacaoGeral;
    }
}