package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import randomizer.YgoRomScrambler;
import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;

/**
 * The main application. Starts UI
 */
public class Main extends Application {

    private boolean isCommandLineOnly = false;

    private Stage primaryStage;
    private final ExtensionFilter ndsFilter = new ExtensionFilter("NDS Roms (*.nds)",
            "*.nds");
    private final ExtensionFilter jsonFilter = new ExtensionFilter("Settings Files (*.yrconf)",
            "*.yrconf");

    private File lastChosenDirectory = null;

    private File rom = null;
    private File outputFile = null;
    private File settingsExportFile = null;
    private YgoRandomizerSettings settings = new YgoRandomizerSettings();

    /**
     * Constructor for command line handling. Whenever constructor is called, no UI
     * is ran and all functions must work through public methods only.
     */
    Main(boolean isCommandLineOnly) {
       this.isCommandLineOnly = isCommandLineOnly;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Run with -h for available CLI functionality help");
            launch();
        } else {
            CommandLineHandler.handleArguments(args);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Main.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setMainReference(this);

        primaryStage.setTitle("Yu-Gi-Oh Randomizer");
        primaryStage.setScene(new Scene(root));

        primaryStage.sizeToScene();

        primaryStage.setResizable(false);

        primaryStage.show();
    }

    /**
     * Select rom using FileChooser.
     */
    void selectRom() {
        // select rom
        File romFile = showSelectDialogue(ndsFilter);

        if (romFile != null) {
            rom = romFile;
        }
    }

    /**
     * Randomize rom if loaded.
     */
    void randomizeRom() {
        try {
            if (rom == null) {
                return;
            }

            // randomize
            YgoRomScrambler scrambler = new YgoRomScrambler();
            scrambler.randomizeRom(rom, settings);

            // save file
            if (!isCommandLineOnly) outputFile = showSaveDialogue(ndsFilter);

            if (outputFile != null) {
                scrambler.writeToFile(outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load settings using FileChooser.
     */
    void loadSettings() {
        try {
            // load settings if any
            File settingsFile = showSelectDialogue(jsonFilter);
            if (settingsFile != null) {
                settings = YgoRandomizerSettings.loadFromFile(settingsFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save settings using FileChooser if not in command line mode.
     */
    void saveSettings() {
        if (!isCommandLineOnly) {
            settingsExportFile = showSaveDialogue(jsonFilter);
        }

        try {
            settings.saveToFile(settingsExportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File showSelectDialogue(ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        if (lastChosenDirectory != null) fileChooser.setInitialDirectory(lastChosenDirectory);
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            lastChosenDirectory = file.getParentFile();
        }
        return file;
    }

    private File showSaveDialogue(ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        if (lastChosenDirectory != null) fileChooser.setInitialDirectory(lastChosenDirectory);
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            lastChosenDirectory = file.getParentFile();
        }

        return file;
    }

    /**
     * Gets settings.
     *
     * @return the settings
     */
    YgoRandomizerSettings getSettings() {
        return settings;
    }

    /**
     * Gets rom input and rom output.
     *
     * @param input The rom that will be unwrapped
     * @param output The file the repacked rom will be saved into
     */
    void setRomInputAndOutput(File input, File output) {
        rom = input;
        outputFile = output;
    }

    /**
     * Setter for settings.
     *
     * @param settings The new settings
     */
    public void setSettings(YgoRandomizerSettings settings) {
        this.settings = settings;
    }

    /**
     * Setter for the file settings will be exported to.
     *
     * @param settingsExportFile The export file
     */
    public void setSettingsExportFile(File settingsExportFile) {
        this.settingsExportFile = settingsExportFile;
    }
}
