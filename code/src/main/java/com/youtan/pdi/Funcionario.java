package com.youtan.pdi;

public class Funcionario {
    private int id;
    private String nome;
    private String matricula;
    private String cargo;
    private String departamento;
    private int gerenteId;
    private int usuarioId;

    // Construtor
    public Funcionario(int id, String nome, String matricula, String cargo, String departamento, int gerenteId, int usuarioId) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.cargo = cargo;
        this.departamento = departamento;
        this.gerenteId = gerenteId;
        this.usuarioId = usuarioId;
    }

    // Getters e Setters
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(int gerenteId) {
        this.gerenteId = gerenteId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}