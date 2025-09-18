// Local do arquivo: C:\Galaticos\src\main\java\modelo\Documento.java

package modelo;

import java.util.Date;

/**
 * Classe que representa um documento associado a um PDI.
 * Pode ser um certificado, um relatório, um feedback, etc.
 */
public class Documento {

    // --- Atributos ---
    private int id;
    private String nome;
    private String tipo; // Ex: "PDF", "Certificado", "Feedback"
    private String caminhoArquivo; // Ou um byte[] para armazenar o arquivo no banco
    private Date dataUpload;

    // --- Construtor ---
    public Documento() {
        // Construtor vazio
    }

    public Documento(int id, String nome, String tipo, String caminhoArquivo, Date dataUpload) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.caminhoArquivo = caminhoArquivo;
        this.dataUpload = dataUpload;
    }

    // --- Getters e Setters ---
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
}