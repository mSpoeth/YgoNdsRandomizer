package randomizer;

import randomizer.nds.utils.RomUnwrapper;
import randomizer.randomizerUtils.FileTools;
import randomizer.randomizerUtils.exceptions.RandomizerException;
import randomizer.randomizerUtils.randomizer.PackRandomizer;
import randomizer.randomizerUtils.randomizer.Randomizer;
import randomizer.randomizerUtils.randomizer.StructureDeckRandomizer;
import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class YgoRomScrambler {

    private RomUnwrapper romUnwrapper;

    private boolean finishedRandomizing = false;
    private File extractedDataFolder = null;
    private File finishedRom = null;

    public YgoRomScrambler() {
        String ndsToolPath = "./src/main/resources/executables/ndstool";
        romUnwrapper = new RomUnwrapper(ndsToolPath);
    }

    public void randomizeRom(File rom, YgoRandomizerSettings settings) {
        try {
            // unwrap rom
            System.out.print("\nOpening up the .nds file ");
            extractedDataFolder = romUnwrapper.unwrapRom(rom);
            System.out.print(" - Done\n");

            // Find pack data .pac
            System.out.print("\nUnpacking the data ");
            File bin2Pac = findFileInExtraction("bin2.pac");
            File deckPac = findFileInExtraction("deck.pac");
            System.out.print(" - Done\n");

            // Load randomizer with settings and randomize
            System.out.print("\nLoading up the randomizers ");
            Randomizer.GameEdition romEdition = detectGameEdition(rom);

            PackRandomizer packRandomizer = new PackRandomizer(settings, romEdition);
            StructureDeckRandomizer sdRandomizer = new StructureDeckRandomizer(settings, romEdition);

            packRandomizer.randomize(bin2Pac);
            sdRandomizer.randomize(deckPac);
            System.out.print(" - Done\n");

            // re-wrap rom
            System.out.print("\nRe-wrapping the rom back up ");
            finishedRom = romUnwrapper.wrapRom(extractedDataFolder);
            finishedRandomizing = true;
            System.out.print(" - Done\n");

        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong! Maybe one of the roms you were trying " +
                    "to randomize was open elsewhere or moved somewhere else. Please check " +
                    "your files and try again. If it won't work still, please open an issue " +
                    "on the Github page.\n");
            e.printStackTrace();
        }
    }

    public File writeToFile(File file) throws IOException {
        if (!finishedRandomizing) {
            throw new RandomizerException("Could not save to file: Randomization not finished");
        }

        FileTools.replaceFile(finishedRom, file);

        return file;
    }

    private File findFileInExtraction(String fileName) {
        // Greedy attempt to just guess
        File foundFile = new File(extractedDataFolder.getPath()
                + "/data/Data_arc_pac/" + fileName);

        if (!foundFile.exists()) {
            List<File> files = FileTools.findAllFiles(fileName, extractedDataFolder);
            if (files.size() > 0) {
                foundFile = files.get(0);
            } else {
                System.out.println("Invalid rom structure for " + fileName
                        + ". Valid games are WC2008 - 2011.");
                System.out.println("If the game is one of the supported ones and this message " +
                        "is shown, please open up an issue on the Github page.");
                return null;
            }
        }
        return foundFile;
    }


    private Randomizer.GameEdition detectGameEdition(File rom) throws IOException {
        // Read the bytes that define the title
        byte[] titleBytes = Arrays.copyOfRange(Files.readAllBytes(rom.toPath()), 0, 0x0C);
        String titleString = FileTools.getBytesToString(titleBytes).trim().toLowerCase();

        if (titleString.startsWith("yu")) {
            if (titleString.endsWith("11")) {
                return Randomizer.GameEdition.Wc2011;
            } else if (titleString.endsWith("10")) {
                return Randomizer.GameEdition.Wc2010;
            } else if (titleString.endsWith("9")) {
                return Randomizer.GameEdition.Wc2009;
            } else if (titleString.endsWith("8")) {
                return Randomizer.GameEdition.Wc2008;
            }
        }

        System.out.println("Game not detected or not supported. Valid games are WC2008 - 2011.");
        System.out.println("If the game is one of the supported ones and this message is shown, " +
                "please open up an issue on the Github page.");

        return Randomizer.GameEdition.Undefined;
    }
}
