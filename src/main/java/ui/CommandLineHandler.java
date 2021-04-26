package ui;

import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;

class CommandLineHandler {

    static void handleArguments(String[] args) {
        String romPath = null;
        String outputPath = null;
        String settingsPath = null;

        String packString = "";
        String structureDeckString = "";
        String seed = null;

        int pack = 0;
        int structure = 0;

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

                case "-p":
                    packString = args[++i];
                    break;

                case "-sd":
                    structureDeckString = args[++i];
                    break;

                case "-se":
                    seed = args[++i];
                    break;
            }
        }

        if (romPath == null) {
            System.out.println("Path to rom is not optional.");
            System.exit(-1);
        }

        File rom = new File("./" + romPath);
        File settings = new File("./" + settingsPath);

        File output;
        if (outputPath != null) {
            output = new File("./" + outputPath);
        } else {
            output = new File("./" + "output.nds");
        }

        // Parse pack & structure deck count
        try {
            if (!packString.isEmpty()) pack = Integer.parseInt(packString);
            if (!structureDeckString.isEmpty()) structure = Integer.parseInt(structureDeckString);
        } catch (NumberFormatException e) {
            System.out.println("Pack and Structure Deck must be an integer input.");
        }

        Main mainInstance = new Main(true);

        try {
            mainInstance.setRomInputAndOutput(rom, output);

            if (settingsPath != null) {
                mainInstance.setSettings(YgoRandomizerSettings.loadFromFile(settings));
            }

            if (!packString.isEmpty()) {
                if (pack < 0 || pack > YgoRandomizerSettings.PackRandomization.values().length) {
                    System.out.println("Pack needs to be within [0, 2]");
                } else {
                    mainInstance.getSettings().setPackRandomization(pack);
                }
            }

            if (!structureDeckString.isEmpty()) {
                if (structure < 0 || structure > YgoRandomizerSettings.PackRandomization.values().length) {
                    System.out.println("Structure Deck needs to be within [0, 1]");
                } else {
                    mainInstance.getSettings().setStructureDeckRandomization(structure);
                }
            }

            if (seed != null) mainInstance.getSettings().setSeed(seed);

            mainInstance.randomizeRom();

        } catch (IOException e) {
            System.out.println("Couldn't load settings from " + settings.getAbsolutePath() + ".");
        }
        System.exit(0);
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("-h  | Show help. Like this right here.");

        System.out.println("- - -");
        System.out.println("-r <Path to rom> | Set the path to the rom.");
        System.out.println("-o <Filename> | Optional. Set the name the output will have. " +
                "Warning: Will override any file with the given name!");
        System.out.println("-s <Path to settings> | Optional. Load prior settings. " +
                "Overridden by other commands");
        System.out.println("-p [0 - 2] | Optional. Set pack randomization.");
        System.out.println("-sd [0 - 1] | Optional. Set structure deck randomization.");
        System.out.println("-se <Seed> | Optional. Set seed.");

        System.exit(0);
    }
}
