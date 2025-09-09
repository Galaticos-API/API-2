package com.youtan.pdi;

import java.util.Date;

public class Documento {
    private int id;
    private int pdiId;
    private String nomeArquivo;
    private String caminhoArquivo;
    private Date dataUpload;
    private String tipoDocumento;

    // Construtor
    public Documento(int id, int pdiId, String nomeArquivo, String caminhoArquivo, Date dataUpload, String tipoDocumento) {
        this.id = id;
        this.pdiId = pdiId;
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = caminhoArquivo;
        this.dataUpload = dataUpload;
        this.tipoDocumento = tipoDocumento;
    }

    // Getters e Setters
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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public Date getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(Date dataUpload) {
        this.dataUpload = dataUpload;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}