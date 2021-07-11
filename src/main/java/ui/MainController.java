package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.*;

/**
 * The controller for the main ui window
 */
public class MainController {
    @FXML
    private Button loadRom;

    @FXML
    private Button loadSettings;

    @FXML
    private CheckBox saveSettings;

    @FXML
    private CheckBox distributeCards;

    @FXML
    private CheckBox introduceSD;

    @FXML
    private Button randomizeButton;

    @FXML
    private ChoiceBox<PackRandomization> packRandomSelection;

    @FXML
    private ChoiceBox<StructureDeckRandomization> structureRandomSelection;

    @FXML
    private TextField seedInput;

    private Main mainReference = null;

    /**
     * Initialization. Called by JavaFx's FxmlLoader
     */
    public void initialize() {
        loadRom.setOnAction(event -> {
            mainReference.selectRom();
            randomizeButton.setDisable(false);
        });
        randomizeButton.setOnAction(event -> randomizeButtonFunction());

        loadSettings.setOnAction(event -> loadAndShowCurrentSettings());

        populateChoiceBoxes();
    }

    /**
     * Sets main reference.
     *
     * @param main Main instance
     */
    public void setMainReference(Main main) {
        this.mainReference = main;
    }

    private void randomizeButtonFunction() {
        mainReference.getSettings().setSeed(seedInput.getText());
        mainReference.getSettings().setPackRandomization(packRandomSelection.getValue());
        mainReference.getSettings().setStructureDeckRandomization(structureRandomSelection.getValue());

        mainReference.getSettings().setDistributeEvenly(distributeCards.isSelected());
        mainReference.getSettings().setReIntroduceSDCards(introduceSD.isSelected());

        mainReference.randomizeRom();

        if (saveSettings.isSelected()) {
            mainReference.saveSettings();
        }
    }

    private void loadAndShowCurrentSettings() {
        mainReference.loadSettings();

        packRandomSelection.setValue(mainReference.getSettings().getPackSetting());
        structureRandomSelection.setValue(mainReference.getSettings().getStructureDeckSetting());

        seedInput.setText(mainReference.getSettings().getSeed());
    }

    private void populateChoiceBoxes() {
        ObservableList<PackRandomization> packOptions =
                FXCollections.observableArrayList(PackRandomization.values());

        ObservableList<StructureDeckRandomization> structureDeckOptions =
                FXCollections.observableArrayList(StructureDeckRandomization.values());

        packRandomSelection.setItems(packOptions);
        packRandomSelection.setValue(PackRandomization.NoChange);
        packRandomSelection.setOnAction(event -> mainReference.getSettings()
                .setPackRandomization(packRandomSelection.getValue()));

        structureRandomSelection.setItems(structureDeckOptions);
        structureRandomSelection.setValue(StructureDeckRandomization.NoChange);
        structureRandomSelection.setOnAction(event -> mainReference.getSettings()
                .setStructureDeckRandomization(structureRandomSelection.getValue()));
    }
}
