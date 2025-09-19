package modelo;

import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String nome, email, senha, tipo_usuario, status;
    private LocalDateTime data_criacao;


    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String tipo_usuario, String status) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo_usuario = tipo_usuario;
        this.status = status;
    }

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

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(LocalDateTime data_criacao) {
        this.data_criacao = data_criacao;
    }
}
