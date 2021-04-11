package randomizer.randomizerUtils.randomizer;

import randomizer.randomizerUtils.pacFileHandling.PacWrapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The randomizer implementation that handles pack randomization.
 */
public class PackRandomizer extends Randomizer{

    /**
     * Instantiates a new Pack randomizer.
     *
     * @param settings the settings
     */
    public PackRandomizer(YgoRandomizerSettings settings) {
        super(settings);
    }

    @Override
    public File randomize(File toBeRandomized) throws IOException {
        String packFileName = "card_pack.bin";

        PacWrapper pacWrapper = new PacWrapper(toBeRandomized);
        byte[] bytes = pacWrapper.getFileBytes(packFileName);

        switch (settings.getPackSetting()) {
            case RandomizeWithRarity: {
                swapAllCards(bytes, false);
                break;
            }
            case RandomizeCompletely: {
                swapAllCards(bytes, true);
                break;
            }
        }

        pacWrapper.overrideFileData(packFileName, bytes);

        pacWrapper.repack();

        return toBeRandomized;
    }

    private void swapAllCards(byte[] toBeRandomized, boolean shuffleRarities) {
        int firstSpeedPack = 0x39;

        LinkedList<Byte> mainPacks = new LinkedList<>();
        LinkedList<Byte> bonusPacks = new LinkedList<>();

        LinkedList<Byte> speedPacks = new LinkedList<>();

        LinkedList<Byte> mainRarities = new LinkedList<>();
        LinkedList<Byte> bonusRarities = new LinkedList<>();

        // Read all packs and rarities
        for (int i = 0; i  < toBeRandomized.length; i += 8) {
            int pack = (toBeRandomized[i + 3] & 0xFF);
            int bonusPack = (toBeRandomized[i + 4] & 0xFF);

            int rarity = (toBeRandomized[i] & 0xFF);
            int bonusRarity = (toBeRandomized[i + 1] & 0xFF);

            if (pack != 0x00) {
                if (pack < firstSpeedPack) {
                    mainPacks.add((byte)pack);
                } else {
                    speedPacks.add((byte) pack);
                }

                if (bonusPack != 0x00) {
                    bonusPacks.add((byte) bonusPack);
                }

                if (shuffleRarities) {
                    if (rarity != 0x00) {
                        mainRarities.add((byte) rarity);
                    }

                    if (bonusRarity != 0x00) {
                        bonusRarities.add((byte) bonusRarity);
                    }
                }
            }
        }

        // Shuffle packs and rarities
        Collections.shuffle(mainPacks, random);
        Collections.shuffle(speedPacks, random);
        Collections.shuffle(bonusPacks, random);
        if (shuffleRarities) {
            Collections.shuffle(mainRarities, random);
            Collections.shuffle(bonusRarities, random);
        }

        // Paste shuffled packs and rarities back in
        for (int i = 0; i  < toBeRandomized.length; i += 8) {
            int pack = (toBeRandomized[i + 3] & 0xFF);
            int bonusPack = (toBeRandomized[i + 4] & 0xFF);

            int rarity = (toBeRandomized[i] & 0xFF);
            int bonusRarity = (toBeRandomized[i + 1] & 0xFF);

            if (pack != 0x00) {
                if (pack < firstSpeedPack) {
                    toBeRandomized[i + 3] = mainPacks.pop();
                } else {
                    toBeRandomized[i + 3] = speedPacks.pop();
                }

                if (bonusPack != 0x00) {
                    toBeRandomized[i + 4] = bonusPacks.pop();
                }

                if (shuffleRarities) {
                    if (rarity != 0x00) {
                        toBeRandomized[i] = mainRarities.pop();
                    }

                    if (bonusRarity != 0x00) {
                        toBeRandomized[i + 1]  = bonusRarities.pop();
                    }
                }
            }
        }
    }
}
