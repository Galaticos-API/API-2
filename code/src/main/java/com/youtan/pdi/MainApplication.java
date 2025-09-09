package com.youtan.pdi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML da tela de login
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Login.fxml"));

        // Cria a cena com base no arquivo FXML
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        // Configura e exibe a janela
        stage.setTitle("Sistema PDI - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}