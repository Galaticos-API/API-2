package modelo;

import java.time.LocalDate;

// Esta classe representa a PDI que será exibida na tela
public class SinglePdi {
    private Integer id;
    private Integer colaboradorId;
    private String status;
    private LocalDate dataCriacao;
    private LocalDate dataFechamento;
    private Double pontuacaoGeral;

    // Construtor
    public SinglePdi(Integer id, Integer colaboradorId, String status, LocalDate dataCriacao, LocalDate dataFechamento, Double pontuacaoGeral) {
        this.id = id;
        this.colaboradorId = colaboradorId;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataFechamento = dataFechamento;
        this.pontuacaoGeral = pontuacaoGeral;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getColaboradorId() { return colaboradorId; }
    public String getStatus() { return status; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataFechamento() { return dataFechamento; }
    public Double getPontuacaoGeral() { return pontuacaoGeral; }

    // Setters (Caso necessário deixei pronto já)
    // public void setId(Integer id) { this.id = id; }
}