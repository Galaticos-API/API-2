package modelo;

public class Setor {
    private String id;
    private String nome;
    private String descricao;

    // Construtor vazio
    public Setor() {
    }

    // Construtor com campos (exceto id)
    public Setor(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Opcional: toString() para facilitar a depuração e exibição em ComboBoxes
    @Override
    public String toString() {
        return nome; // Exibe apenas o nome por padrão
    }
}