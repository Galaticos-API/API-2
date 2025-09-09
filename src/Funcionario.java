package com.youtan.pdi;

import java.util.Date;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha; // Em um sistema real, use um hash
    private String tipoUsuario; // Ex: "RH", "Gerente", "Funcionario"
    private Date dataCriacao;

    // Construtor
    public Usuario(int id, String nome, String email, String senha, String tipoUsuario, Date dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters (métodos de acesso)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}