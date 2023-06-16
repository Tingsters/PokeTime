package org.devoxx4kids.poketime;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Created by Cassandra on 9/17/2017.
 */
public class BattleSceneController {
    @FXML
    public Label pokemonName;

    @FXML
    public ImageView pokemonPicture;

    @FXML
    public ImageView trainerPokemon;

    @FXML
    public ListView actionMenu;

    @FXML
    public TextField playerHealth;

    @FXML
    public TextField enemyHealth;

    @FXML
    public ImageView battleBackground;

    @FXML
    public void initialize() {
        actionMenu.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                Platform.runLater(() -> {
                    actionMenu.getSelectionModel().clearSelection();
                });
                Main.flee();
            }
        });
        ColorAdjust colorAdjustment = new ColorAdjust();
        battleBackground.setEffect(colorAdjustment);
        Main.pixelatedClock.isNight.addListener((o, ov, nv) -> {
            new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(colorAdjustment.brightnessProperty(), nv ? -0.6 : 0))).play();
        });
    }
}
