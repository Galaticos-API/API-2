// Local do arquivo: C:\Galaticos\src\main\java\modelo\PDI.java

package modelo;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe que representa o Plano de Desenvolvimento Individual (PDI) de um funcionário.
 */
public class PDI {

    // --- Atributos ---
    // Correspondem aos campos da tabela PDI no diagrama.
    private String id;
    private String colaboradorId;
    private String status;
    private Date dataCriacao;
    private Date dataFechamento;
    private float pontuacaoGeral;
    private int ano;

    // --- Relacionamentos ---
    // Um PDI tem uma lista de Objetivos e uma lista de Documentos.
    // A anotação 1..* no diagrama significa "um ou mais".
    private List<Objetivo> objetivos;
    private List<Documento> documentos;
    private String nomeColaborador;


    private String pattern = "dd-MM-yyyy";
    private SimpleDateFormat formatter = new SimpleDateFormat(pattern);

    // --- Construtores ---

    /**
     * Construtor padrão (vazio).
     * É uma boa prática ter um construtor vazio.
     */
    public PDI() {
        // Inicializa as listas para evitar NullPointerException
        this.objetivos = new ArrayList<>();
        this.documentos = new ArrayList<>();
    }

    /**
     * Construtor com todos os atributos para facilitar a criação de objetos já preenchidos.
     */
    public PDI(String colaboradorId, int ano, String status, Date dataCriacao, Date dataFechamento) {
        this.colaboradorId = colaboradorId;
        this.ano = ano;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataFechamento = dataFechamento;
    }


    // --- Getters e Setters ---
    // Métodos para acessar e modificar os atributos da classe.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColaboradorId() {
        return colaboradorId;
    }

    public void setColaboradorId(String colaboradorId) {
        this.colaboradorId = colaboradorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataCriacao() {
        return formatter.format(this.dataCriacao);
    }

    public Date getDataCriacaoDate() {
        return this.dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getDataFechamento() {
        return formatter.format(this.dataFechamento);
    }

    public Date getDataFechamentoDate() {
        return this.dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public float getPontuacaoGeral() {
        return pontuacaoGeral;
    }

    public void setPontuacaoGeral(float pontuacaoGeral) {
        this.pontuacaoGeral = pontuacaoGeral;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public List<Objetivo> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<Objetivo> objetivos) {
        this.objetivos = objetivos;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public void setNomeColaborador(String nomeColaborador) {
        this.nomeColaborador = nomeColaborador;
    }
}