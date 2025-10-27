package modelo;

import dao.SetorDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String tipo_usuario;
    private String status;
    private LocalDateTime data_criacao;
    private String setor_id;

    // Novos atributos para corresponder ao DAO
    private LocalDate data_nascimento;
    private String genero;
    private String cpf;

    // Construtor vazio
    public Usuario() {
    }

    // Construtor completo com todos os campos (exceto id e data_criacao, que são gerados pelo banco)
    public Usuario(String nome, String email, String senha, String tipo_usuario, String status, LocalDate data_nascimento, String genero, String cpf, String setor_id) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo_usuario = tipo_usuario;
        this.status = status;
        this.data_nascimento = data_nascimento;
        this.genero = genero;
        this.cpf = cpf;
        this.setor_id = setor_id;
    }


    // --- Getters e Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    // --- Getters e Setters para os novos campos ---

    public LocalDate getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(LocalDate data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getGenero() {
        return genero;
    }


    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Setor getSetor() {
        SetorDAO setorDAO = new SetorDAO();
        if (this.setor_id != null && !this.setor_id.equals("")) {
            return setorDAO.buscarPorId(Integer.parseInt(this.setor_id));
        } else {
            return new Setor();
        }

    }
    public String getSetor_id(){
        return this.setor_id;
    }

    public String getSetorNome() {
        SetorDAO setorDAO = new SetorDAO();
        Setor setor = setorDAO.buscarPorId(Integer.parseInt(this.setor_id));
        return setor != null ? setor.getNome() : "";
    }

    public void setSetor_id(String setor_id) {
        this.setor_id = setor_id;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", tipo_usuario='" + tipo_usuario + '\'' +
                ", status='" + status + '\'' +
                ", data_criacao=" + data_criacao +
                ", setor_id='" + setor_id + '\'' +
                ", data_nascimento=" + data_nascimento +
                ", genero='" + genero + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}