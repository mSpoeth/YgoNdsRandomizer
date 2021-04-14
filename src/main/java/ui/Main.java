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
    private final ExtensionFilter ndsFilter = new ExtensionFilter("NDS Roms (*.nds)", "*.nds");
    private final ExtensionFilter jsonFilter = new ExtensionFilter("Settings Files (*.yrconf)",
            "*.yrconf");

    private File lastChosenDirectory = null;

    private File rom = null;
    private YgoRandomizerSettings settings = new YgoRandomizerSettings();
    private File outputFile = null;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            launch();
        } else {
            handleArguments(args);
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
     * Save settings using FileChooser.
     */
    void saveSettings() {
        try {
            settings.saveToFile(showSaveDialogue(jsonFilter));
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

    private static void handleArguments(String[] args) {
        String romPath = null;
        String outputPath = null;
        String settingsPath = null;

        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    break;
                case "-r":
                    romPath = args[++i];

                    break;
                case "-o":
                    outputPath = args[++i];

                    break;
                case "-s":
                    settingsPath = args[++i];
                    break;
            }
        }

        if (romPath == null) {
            System.out.println("Path to rom is not optional.");
            return;
        }

        File rom = new File("./" + romPath);
        File settings = new File("./" + settingsPath);

        File output;
        if (outputPath != null) {
            output = new File("./" + outputPath);
        } else {
            output = new File("./" + "output.nds");
        }

        Main mainInstance = new Main();
        mainInstance.isCommandLineOnly = true;

        try {
            mainInstance.rom = rom;
            mainInstance.outputFile = output;

            if (settingsPath != null) {
                mainInstance.settings = YgoRandomizerSettings.loadFromFile(settings);
            }

            mainInstance.randomizeRom();

        } catch (IOException e) {
            System.out.println("Couldn't load settings from " + settings.getAbsolutePath() + ".");
        }
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("-h \t| Show help. Like this right here.");

        System.out.println("\n- - -");
        System.out.println("-r <Path to rom>\t| Set the path to the rom.");
        System.out.println("-o <Filename>\t| Optional. Set the name the output will have. " +
                "Warning: Will override any file with the given name!");
        System.out.println("-s <Path to settings>\t| Optional. Load prior settings.");
    }
}
