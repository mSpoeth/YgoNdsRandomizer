package randomizer;

import randomizer.nds.utils.RomUnwrapper;
import randomizer.randomizerUtils.FileTools;
import randomizer.randomizerUtils.exceptions.RandomizerException;
import randomizer.randomizerUtils.randomizer.PackRandomizer;
import randomizer.randomizerUtils.randomizer.StructureDeckRandomizer;
import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class YgoRomScrambler {

    private boolean finishedRandomizing = false;
    private File extractedDataFolder = null;
    private File finishedRom = null;

    public void randomizeRom(File rom, YgoRandomizerSettings settings) {
        String ndsToolPath = "./src/main/resources/executables/ndstool";
        RomUnwrapper romUnwrapper = new RomUnwrapper(ndsToolPath);

        try {
            // unwrap rom
            extractedDataFolder = romUnwrapper.unwrapRom(rom);

            // Find pack data .pac
            File bin2Pac = findFileInExtraction("bin2.pac");
            File deckPac = findFileInExtraction("deck.pac");

            // Load randomizer with settings
            PackRandomizer packRandomizer = new PackRandomizer(settings);
            StructureDeckRandomizer sdRandomizer = new StructureDeckRandomizer(settings);

            // create new pac data according to settings
            packRandomizer.randomize(bin2Pac);
            sdRandomizer.randomize(deckPac);

            // re-wrap rom
            finishedRom = romUnwrapper.wrapRom(extractedDataFolder);
            finishedRandomizing = true;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public File writeToFile(File file) throws IOException {
        if (!finishedRandomizing) {
            throw new RandomizerException("Could not save to file: Randomization not finished");
        }

        FileTools.replaceFile(finishedRom, file);
        Files.setAttribute(file.toPath(), "dos:hidden", false);

        return file;
    }

    private File findFileInExtraction(String fileName) {
        // Greedy attempt to just guess
        File foundFile = new File(extractedDataFolder.getPath() + "/data/Data_arc_pac/" + fileName);

        if (!foundFile.exists()) {
            List<File> files = FileTools.findAllFiles(fileName, extractedDataFolder);
            if (files.size() > 0) {
                    foundFile = files.get(0);
            } else {
                return null;
            }
        }
        return foundFile;
    }


}
