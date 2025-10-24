package modelo;

import java.time.LocalDate;

public class Avaliacao {
    private int id;
    private int objetivoId;
    private int avaliadorId;
    private double nota;
    private String comentario;
    private LocalDate dataAvaliacao;
    private String status_objetivo;

    public Avaliacao() {
    }

    public Avaliacao(int objetivoId, int avaliadorId, double nota, String comentario, LocalDate dataAvaliacao, String status_objetivo) {
        this.objetivoId = objetivoId;
        this.avaliadorId = avaliadorId;
        this.nota = nota;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
        this.status_objetivo = status_objetivo;
    }

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

    public int getAvaliadorId() {
        return avaliadorId;
    }

    public void setAvaliadorId(int avaliadorId) {
        this.avaliadorId = avaliadorId;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getStatus_objetivo() {
        return status_objetivo;
    }

    public void setStatus_objetivo(String status_objetivo) {
        this.status_objetivo = status_objetivo;
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "id=" + id +
                ", objetivoId=" + objetivoId +
                ", nota=" + nota +
                ", dataAvaliacao=" + dataAvaliacao +
                ", status_objetivo='" + status_objetivo + '\'' +
                '}';
    }
}
