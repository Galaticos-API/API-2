package gui.modal;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import modelo.Avaliacao;
import modelo.Objetivo;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvaliacoesObjetivoModalController {

    @FXML
    private Text txtTituloObjetivo;

    @FXML
    private ListView<Avaliacao> listViewAvaliacoes;

    private Stage dialogStage;
    private final DateTimeFormatter FORMATADOR_DATA_AVALIACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Define os dados a serem exibidos no modal.
     * @param objetivo O objetivo cujas avaliações serão mostradas.
     * @param avaliacoes A lista de avaliações para este objetivo.
     */
    public void setDados(Objetivo objetivo, List<Avaliacao> avaliacoes) {
        if (objetivo != null) {
            txtTituloObjetivo.setText("Avaliações: \"" + objetivo.getDescricao() + "\"");
        }

        if (avaliacoes != null && !avaliacoes.isEmpty()) {
            listViewAvaliacoes.setItems(FXCollections.observableArrayList(avaliacoes));
            configurarCellFactory();
        } else {
            // Opcional: Mostrar uma mensagem dentro do ListView se não houver avaliações
            listViewAvaliacoes.setPlaceholder(new Text("Nenhuma avaliação registrada."));
        }
    }

    /**
     * Configura como cada item (Avaliacao) será exibido no ListView.
     */
    private void configurarCellFactory() {
        listViewAvaliacoes.setCellFactory(lv -> new ListCell<Avaliacao>() {
            private VBox layout = new VBox(5); // Espaçamento entre linhas
            private Text avaliadorDataText = new Text();
            private Text notaText = new Text();
            private Text comentarioText = new Text();

            {
                // Adiciona estilos (podem ser definidos no CSS também)
                avaliadorDataText.getStyleClass().add("avaliacao-item-header");
                notaText.getStyleClass().add("avaliacao-item-nota");
                comentarioText.getStyleClass().add("avaliacao-item-comentario");
                comentarioText.setWrappingWidth(350); // Quebra de linha para comentários longos

                layout.getChildren().addAll(avaliadorDataText, notaText, comentarioText);
                layout.getStyleClass().add("avaliacao-item-layout"); // Classe para o VBox
            }

            @Override
            protected void updateItem(Avaliacao avaliacao, boolean empty) {
                super.updateItem(avaliacao, empty);
                if (empty || avaliacao == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String dataFormatada = avaliacao.getDataAvaliacao() != null ?
                            avaliacao.getDataAvaliacao().format(FORMATADOR_DATA_AVALIACAO) : "N/A";
                    String nomeAvaliador = avaliacao.getNomeAvaliador() != null ?
                            avaliacao.getNomeAvaliador() : "Desconhecido";

                    avaliadorDataText.setText("Avaliador: " + nomeAvaliador + "   |   Data: " + dataFormatada);
                    notaText.setText("Nota: " + avaliacao.getNota());
                    comentarioText.setText("Comentário: " + (avaliacao.getComentario() != null ? avaliacao.getComentario() : "-"));

                    setGraphic(layout);
                }
            }
        });
    }

    @FXML
    private void handleFechar() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}