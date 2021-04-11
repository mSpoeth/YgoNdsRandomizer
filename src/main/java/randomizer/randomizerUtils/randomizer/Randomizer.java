package randomizer.randomizerUtils.randomizer;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Randomizer super class
 */
public abstract class Randomizer {

    /**
     * The Settings that determine how randomization is done
     */
    protected final YgoRandomizerSettings settings;

    /**
     * The Random for consistent seeds
     */
    protected final Random random;

    /**
     * Instantiates a new Randomizer.
     *
     * @param settings the settings
     */
    Randomizer(YgoRandomizerSettings settings) {
        this.settings = settings;
        random = new Random(settings.getSeedAsLong());
    }

    /**
     * Randomize a file according to the settings.
     *
     * @param toBeRandomized the file to randomize
     * @return the randomized file
     * @throws IOException the io exception
     */
    public abstract File randomize(File toBeRandomized) throws IOException;

}
