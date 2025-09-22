package util;

import javafx.stage.Stage;

public class StageManager {
    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static Stage getStage() {
        return stage;
    }
}
