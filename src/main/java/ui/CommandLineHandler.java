package ui;

import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;

class CommandLineHandler {

    enum FlagsEnum {
        RomInput,
        Output,
        SettingsImport,
        SettingsExport,
        Seed,
        PackChoice,
        StructureChoice
    }

    private static boolean[] setFlags;
    private static Object[] flagValues;

    static void handleArguments(String[] args) {
        readArguments(args);
        Main mainInstance = new Main(true);

        // Handle rom input
        File rom = new File("./" + getFlagValue(FlagsEnum.RomInput));
        if (!rom.exists()) {
            System.out.println("Rom file unavailable at: " + rom.getAbsolutePath());
            System.exit(-1);
        }

        // Handle file output
        File output;
        if (checkFlag(FlagsEnum.Output)) {
            output = new File("./" + getFlagValue(FlagsEnum.Output));
        } else {
            output = new File("./" + "output.nds");
        }

        mainInstance.setRomInputAndOutput(rom, output);

        // Handle settings import
        if (checkFlag(FlagsEnum.SettingsImport)) {
            File settings = new File((String) getFlagValue(FlagsEnum.SettingsImport));
            try {
                mainInstance.setSettings(YgoRandomizerSettings.loadFromFile(settings));
            } catch (IOException e) {
                System.out.println("Couldn't load settings. Check if the given path is correct: " +
                        settings.getAbsolutePath());
            }
        }

        // Handle pack choice
        if (checkFlag(FlagsEnum.PackChoice)) {
            int pack = (Integer) getFlagValue(FlagsEnum.PackChoice);
            int packMax = YgoRandomizerSettings.PackRandomization.values().length;

            if (pack < 0 || pack > packMax) {
                System.out.println("Pack needs to be within [0, " + packMax + "]");
                System.exit(-1);
            } else {
                mainInstance.getSettings().setPackRandomization(pack);
            }
        }

        // Handle structure deck input
        if (checkFlag(FlagsEnum.StructureChoice)) {
            int structure = (Integer) getFlagValue(FlagsEnum.StructureChoice);
            int structureMax = YgoRandomizerSettings.StructureDeckRandomization.values().length;

            if (structure < 0 || structure > structureMax) {
                System.out.println("Structure Deck needs to be within [0, " + structureMax + "]");
                System.exit(-1);
            } else {
                mainInstance.getSettings().setPackRandomization(structure);
            }
        }

        // Handle seed input
        if (checkFlag(FlagsEnum.Seed)) {
            mainInstance.getSettings().setSeed((String) getFlagValue(FlagsEnum.Seed));
        }

        // Handle settings export
        if (checkFlag(FlagsEnum.SettingsExport)) {
            try {
                File settingsExport = new File("./" + getFlagValue(FlagsEnum.SettingsExport));
                if (!settingsExport.exists()) {
                    if (!settingsExport.createNewFile()) {
                        System.out.println("Couldn't create settings export.");
                    }
                }
                mainInstance.setSettingsExportFile(settingsExport);
                mainInstance.saveSettings();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mainInstance.randomizeRom();

        System.exit(0);
    }

    private static void readArguments(String[] args) {
        setFlags = new boolean[FlagsEnum.values().length];
        flagValues = new Object[FlagsEnum.values().length];

        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    break;

                case "-r":
                    setFlag(FlagsEnum.RomInput, args[++i]);
                    break;

                case "-o":
                    setFlag(FlagsEnum.Output, args[++i]);
                    break;

                case "-s":
                    setFlag(FlagsEnum.SettingsImport, args[++i]);
                    break;

                case "-p":
                    setFlagAsInt(FlagsEnum.PackChoice, args[++i]);
                    break;

                case "-sd":
                    setFlagAsInt(FlagsEnum.StructureChoice, args[++i]);
                    break;

                case "-se":
                    setFlag(FlagsEnum.Seed, args[++i]);
                    break;

                case "-es":
                    setFlag(FlagsEnum.SettingsExport, true);
                    break;
            }
        }

        if (!setFlags[FlagsEnum.RomInput.ordinal()]) {
            System.out.println("Path to rom is not optional.");
            System.exit(-1);
        }
    }

    private static boolean checkFlag(FlagsEnum flag) {
        return setFlags[flag.ordinal()];
    }

    private static Object getFlagValue(FlagsEnum flag) {
        return flagValues[flag.ordinal()];
    }

    private static void setFlag(FlagsEnum flag, Object value) {
        setFlags[flag.ordinal()] = true;
        flagValues[flag.ordinal()] = value;
    }

    private static void setFlagAsInt(FlagsEnum flag, String value) {
        try {
            int flagValue = Integer.parseInt(value);
            setFlag(flag, flagValue);
        } catch (NumberFormatException e) {
            System.out.println("Pack and Structure Deck must be an integer input.");
            System.exit(-1);
        }
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("-h  | Show help. Like this right here.");

        System.out.println("=====");
        System.out.println("-r <Path to rom> | Set the path to the rom.");
        System.out.println("-o <Filename> | Optional. Set the name the output will have. " +
                "Warning: Will override any file with the given name!");

        System.out.println("-es <Filename> | Optional. Will save the used settings to the file" +
                "Warning: Will override any file with the given name!");

        System.out.println("-s <Path to settings> | Optional. Load prior settings. " +
                "Overridden by other commands");

        System.out.println("-p [0 - 2] | Optional. Set pack randomization.");
        System.out.println("-sd [0 - 1] | Optional. Set structure deck randomization.");
        System.out.println("-se <Seed> | Optional. Set seed.");
        System.out.println("=====");

        System.exit(0);
    }
}
