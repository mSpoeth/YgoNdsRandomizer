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
     * @param gameEdition the edition enum of the game that will be randomized;
     */
    public PackRandomizer(YgoRandomizerSettings settings, GameEdition gameEdition) {
        super(settings, gameEdition);
    }

    @Override
    public void randomize(File toBeRandomized) throws IOException {
        String packFileName = "card_pack.bin";

        PacWrapper pacWrapper = new PacWrapper(toBeRandomized);
        byte[] bytes = pacWrapper.getFileBytes(packFileName);

        executeCorrectRandomization(bytes);

        while (!checkPackIntegrity(bytes)) {
            executeCorrectRandomization(bytes);
        }

        pacWrapper.overrideFileData(packFileName, bytes);

        pacWrapper.repack();
    }

    private void executeCorrectRandomization(byte[] toBeRandomized) {
        boolean reIntroduceSDcCards = settings.GetReIntroduceSDCards();
        boolean distributeEvenly = settings.getDistributeCardsEvenly();

        switch (settings.getPackSetting()) {
            case RandomizeWithRarity: {
                swapAllCards(toBeRandomized, false, reIntroduceSDcCards, distributeEvenly);
                break;
            }
            case RandomizeCompletely: {
                swapAllCards(toBeRandomized, true, reIntroduceSDcCards, distributeEvenly);
                break;
            }
        }
    }

    private void swapAllCards(byte[] toBeRandomized, boolean shuffleRarities,
                              boolean reIntroduceCards, boolean distributeEvenly) {

        LinkedList<Byte> mainPacks = new LinkedList<>();
        LinkedList<Byte> bonusPacks = new LinkedList<>();

        LinkedList<Byte> mainRarities = new LinkedList<>();
        LinkedList<Byte> bonusRarities = new LinkedList<>();

        int sdCount = 0x01;

        // Read all packs and rarities
        for (int i = 0; i  < toBeRandomized.length; i += 8) {
            int pack = (toBeRandomized[i + 3] & 0xFF);
            int bonusPack = (toBeRandomized[i + 4] & 0xFF);

            int rarity = (toBeRandomized[i] & 0xFF);
            int bonusRarity = (toBeRandomized[i + 1] & 0xFF);

            if (pack != 0x00) {
                if (reIntroduceCards || pack <= getPackCount()) {
                    if (reIntroduceCards) {
                        mainPacks.add((byte)(((sdCount++ % getPackCount()) + 1) & 0xFF));

                    } else {
                        mainPacks.add((byte)pack);
                    }

                    if (bonusPack != 0x00) {
                        bonusPacks.add((byte) bonusPack);
                    }

                    mainRarities.add((byte) rarity);

                    if (bonusRarity != 0x00) {
                        bonusRarities.add((byte) bonusRarity);
                    }
                }
            }
        }

        if (distributeEvenly) {
            mainPacks = CreateEvenDistribution(mainPacks);
            bonusPacks = CreateEvenDistribution(bonusPacks);
        }


        // Shuffle packs and rarities
        Collections.shuffle(mainPacks, random);
        Collections.shuffle(bonusPacks, random);

        if (shuffleRarities) {
            Collections.shuffle(mainRarities, random);
            Collections.shuffle(bonusRarities, random);
        }

        // Paste shuffled packs and rarities back in
        for (int i = 0; i  < toBeRandomized.length; i += 8) {
            int pack = (toBeRandomized[i + 3] & 0xFF);
            int bonusPack = (toBeRandomized[i + 4] & 0xFF);

            // int rarity = (toBeRandomized[i] & 0xFF);
            int bonusRarity = (toBeRandomized[i + 1] & 0xFF);

            if (pack != 0x00) {
                if (reIntroduceCards || pack <= getPackCount()) {
                    toBeRandomized[i + 3] = mainPacks.pop();

                    if (bonusPack != 0x00) {
                        toBeRandomized[i + 4] = bonusPacks.pop();
                    }

                    if (shuffleRarities) {
                        toBeRandomized[i] = mainRarities.pop();

                        if (bonusRarity != 0x00) {
                            toBeRandomized[i + 1]  = bonusRarities.pop();
                        }
                    }
                }
            }
        }
    }

    // Returns false if a pack has no cards of any required rarity

    private boolean checkPackIntegrity(byte[] randomizedBytes) {

        int[][] checkedPacks = new int[getPackCount()][3];

        // Paste shuffled packs and rarities back in
        for (int i = 0; i  < randomizedBytes.length; i += 8) {
            int pack = (randomizedBytes[i + 3] & 0xFF);
            int bonusPack = (randomizedBytes[i + 4] & 0xFF);

            int rarity = (randomizedBytes[i] & 0xFF);
            int bonusRarity = (randomizedBytes[i + 1] & 0xFF);

            if (pack != 0x00 || bonusPack != 0x00) {
                if (pack <= getPackCount() && rarity != 0x00) {
                    checkedPacks[pack - 1][rarity - 2] += 1;
                }

                if (bonusPack <= getPackCount() && bonusPack != pack) {
                    if (bonusRarity != 0x00) {
                        checkedPacks[bonusPack - 1][bonusRarity - 2] += 1;
                    }
                }
            }
        }

        for (int[] pack : checkedPacks) {
            for (int rarity : pack) {
                if (rarity < 1) {
                    // If any pack has a missing rarity, return false
                    return false;
                }
            }
        }

        return true;
    }

    private LinkedList<Byte> CreateEvenDistribution(LinkedList<Byte> bytes) {
        int originalLength = bytes.size();

        HashMap<Byte, Integer> occurrenceMap = new HashMap<>();

        for (Byte b : bytes) {
            if (!occurrenceMap.containsKey(b)) {
                occurrenceMap.put(b, 0);
            }
            occurrenceMap.put(b, occurrenceMap.get(b) + 1); // increment
        }

        int sum = 0;
        for (Integer i : occurrenceMap.values()) {
            sum += i;
        }


        int average = sum / occurrenceMap.keySet().size();

        LinkedList<Byte> evenlyDistributedList = new LinkedList<>();


        for (Byte b : occurrenceMap.keySet()) {
            for (int i = 0; i < average; ++i) {
                evenlyDistributedList.add(b);
            }
        }

        assert evenlyDistributedList.size() <= originalLength;

        int index = 0;
        while (evenlyDistributedList.size() < originalLength) {
            evenlyDistributedList.add((byte) (index & 0xFF));
            index = (index++ % getPackCount()) + 1;
        }

        while (evenlyDistributedList.size() > originalLength) {
            evenlyDistributedList.removeFirst();
        }

        return evenlyDistributedList;
    }
}