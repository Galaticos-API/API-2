package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Colaborador {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String cargo;
    private String experiencia;
    private String observacoes;
    private Colaborador gerente; // Representa a coluna 'gerente_id'
    private Usuario usuario;     // Representa a coluna 'usuario_id'

    // Construtor padrão
    public Colaborador() {
    }

    public Colaborador(String nome, String cpf, LocalDate dataNascimento, String cargo, String experiencia, String observacoes, Usuario usuario) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.cargo = cargo;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
        this.usuario = usuario;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Colaborador getGerente() {
        return gerente;
    }

    public void setGerente(Colaborador gerente) {
        this.gerente = gerente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Colaborador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cargo='" + cargo + '\'' +
                '}';
    }
}
