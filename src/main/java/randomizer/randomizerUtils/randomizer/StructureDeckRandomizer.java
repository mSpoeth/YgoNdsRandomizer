package randomizer.randomizerUtils.randomizer;

import randomizer.randomizerUtils.pacFileHandling.PacWrapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The randomizer implementation that handles structure deck randomization.
 */
public class StructureDeckRandomizer extends Randomizer{

    /**
     * Instantiates a new Structure deck randomizer.
     *
     * @param settings the settings
     * @param gameEdition the edition enum of the game that will be randomized;
     */
    public StructureDeckRandomizer(YgoRandomizerSettings settings, GameEdition gameEdition) {
        super(settings, gameEdition);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void randomize(File toBeRandomized) throws IOException {

        PacWrapper pacWrapper = new PacWrapper(toBeRandomized);

        List<String> fileNames = pacWrapper.getFileNames();
        fileNames.removeIf(s -> !s.startsWith("sd"));

        byte[][] bytes = fillStructureDeckFiles(pacWrapper, fileNames);

        // Switch instead of if, in case I want other SD options
        switch (settings.getStructureDeckSetting()) {
            case Randomize: {
                swapAllCards(bytes);
                break;
            }
        }

        for (int i = 0; i < bytes.length; ++i) {
            String fileName = fileNames.get(i);
            pacWrapper.overrideFileData(fileName, bytes[i]);
        }

        pacWrapper.repack();
    }

    private void swapAllCards(byte[][] toBeRandomized) {
        LinkedList<byte[]> cards = new LinkedList<>();

        for (int i = 0; i < toBeRandomized.length; ++i) {

            for (int j = 0x0A; j < toBeRandomized[i].length; j += 2) {
                if ((toBeRandomized[i][j] & 0x30) == 0x00 && (toBeRandomized[i][j + 1] & 0xFF) == 0x00) {
                    break;
                }

                if ((toBeRandomized[i][j] & 0xFF) != 0x00 && (toBeRandomized[i][j + 1] & 0xFF) != 0x00) {
                    cards.add(new byte[] {toBeRandomized[i][j], toBeRandomized[i][j + 1]});
                }
            }
        }

        Collections.shuffle(cards, random);

        for (int i = 0; i < toBeRandomized.length; ++i) {
            for (int j = 0x0A; j < toBeRandomized[i].length;j += 2) {
                if ((toBeRandomized[i][j] & 0x30) == 0x00 && (toBeRandomized[i][j + 1] & 0xFF) == 0x00) {
                    break;
                }

                if ((toBeRandomized[i][j] & 0xFF) != 0x00 && (toBeRandomized[i][j + 1] & 0xFF) != 0x00) {
                    byte[] bytes = cards.pop();
                    toBeRandomized[i][j] = (byte) (bytes[0] & 0xFF);
                    toBeRandomized[i][j + 1] = (byte) (bytes[1] & 0xFF);
                }
            }
        }
    }

    private byte[][] fillStructureDeckFiles(PacWrapper pacWrapper, List<String> fileNames) {
        byte[][] bytes = new byte[fileNames.size()][];

        for (int i = 0; i < fileNames.size(); ++i) {
            bytes[i] = pacWrapper.getFileBytes(fileNames.get(i));
        }

        return bytes;
    }
}
