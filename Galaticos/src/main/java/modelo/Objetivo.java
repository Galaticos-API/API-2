package modelo;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date; // Mantido, mas java.time.LocalDate é geralmente preferido para o modelo
import java.text.SimpleDateFormat;

public class Objetivo {

    // --- Properties (Para uso no JavaFX TableView) ---
    private final IntegerProperty id;
    private final StringProperty pdiId;
    private final StringProperty descricao;
    private final ObjectProperty<Date> prazo; // ObjectProperty para tipos de objeto como Date
    private final StringProperty status;
    private final StringProperty comentarios;
    private final FloatProperty peso;
    private final FloatProperty pontuacao;

    private final String pattern = "dd-MM-yyyy";
    private final SimpleDateFormat formatter = new SimpleDateFormat(pattern);


    // --- Construtores ---
    public Objetivo() {
        this.id = new SimpleIntegerProperty();
        this.pdiId = new SimpleStringProperty();
        this.descricao = new SimpleStringProperty();
        this.prazo = new SimpleObjectProperty<>();
        this.status = new SimpleStringProperty();
        this.comentarios = new SimpleStringProperty();
        this.peso = new SimpleFloatProperty();
        this.pontuacao = new SimpleFloatProperty();
    }

    public Objetivo(int id, String pdiId, String descricao, Date prazo, String status, String comentarios, float peso, float pontuacao) {
        this.id = new SimpleIntegerProperty(id);
        this.pdiId = new SimpleStringProperty(pdiId);
        this.descricao = new SimpleStringProperty(descricao);
        this.prazo = new SimpleObjectProperty<>(prazo);
        this.status = new SimpleStringProperty(status);
        this.comentarios = new SimpleStringProperty(comentarios);
        this.peso = new SimpleFloatProperty(peso);
        this.pontuacao = new SimpleFloatProperty(pontuacao);
    }


    // --- Getters e Setters Padrão (necessários para o Apache POI e acesso genérico) ---

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getPdiId() { return pdiId.get(); }
    public void setPdiId(String pdiId) { this.pdiId.set(pdiId); }

    public String getDescricao() { return descricao.get(); }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public Date getPrazo() { return prazo.get(); }
    public String getPrazoString() { return getPrazo() != null ? formatter.format(getPrazo()) : ""; } // Proteção contra nulo
    public void setPrazo(Date prazo) { this.prazo.set(prazo); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public String getComentarios() { return comentarios.get(); }
    public void setComentarios(String comentarios) { this.comentarios.set(comentarios); }

    public float getPeso() { return peso.get(); }
    public void setPeso(float peso) { this.peso.set(peso); }

    public float getPontuacao() { return pontuacao.get(); }
    public void setPontuacao(float pontuacao) { this.pontuacao.set(pontuacao); }

    // --- Methods Properties (Necessários para TableView.setCellValueFactory) ---

    public IntegerProperty idProperty() { return id; }
    public StringProperty pdiIdProperty() { return pdiId; }
    public StringProperty descricaoProperty() { return descricao; }
    public ObjectProperty<Date> prazoProperty() { return prazo; }
    public StringProperty statusProperty() { return status; }
    public StringProperty comentariosProperty() { return comentarios; }
    public FloatProperty pesoProperty() { return peso; }
    public FloatProperty pontuacaoProperty() { return pontuacao; }
}