package com.comp2042;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameOverPanel extends BorderPane {
    
    private Button restartButton;

    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        restartButton = new Button("RESTART");
        restartButton.getStyleClass().add("ipad-dark-grey");
        
        VBox vbox = new VBox(20);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.getChildren().addAll(gameOverLabel, restartButton);
        
        setCenter(vbox);
    }
    
    public void setOnRestartClicked(EventHandler<ActionEvent> handler) {
        restartButton.setOnAction(handler);
    }

}