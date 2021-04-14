package randomizer.randomizerUtils.randomizer;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Randomizer super class
 */
public abstract class Randomizer {

    public enum GameEdition {
        Undefined,
        Wc2008,
        Wc2009,
        Wc2010,
        Wc2011
    }

    /**
     * The Settings that determine how randomization is done
     */
    protected final YgoRandomizerSettings settings;

    private GameEdition gameEdition;

    /**
     * The Random for consistent seeds
     */
    protected final Random random;

    /**
     * Instantiates a new Randomizer.
     *
     * @param settings the settings
     * @param gameEdition the edition enum of the game that will be randomized;
     */
    Randomizer(YgoRandomizerSettings settings, GameEdition gameEdition) {
        this.settings = settings;
        this.gameEdition = gameEdition;
        random = new Random(settings.getSeedAsLong());
    }

    /**
     * Randomize a file according to the settings.
     *
     * @param toBeRandomized the file to randomize
     * @throws IOException the io exception
     */
    public abstract void randomize(File toBeRandomized) throws IOException;

    /**
     * Get the pack count that can be randomized, depending on the loaded game edition
     *
     * @return The amount of packs
     */
    int getPackCount() {
        switch (gameEdition) {
            case Wc2008:
                return 39;

            case Wc2009:
                return 37;

            case Wc2010:
                return 49;

            case Wc2011:
                return 59;
        }

        return 0;
    }
}
