package randomizer;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import randomizer.YgoRomScrambler;
import randomizer.randomizerUtils.exceptions.RandomizerException;
import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.*;
import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.PackRandomization.*;
import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.StructureDeckRandomization.NoChange;
import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.StructureDeckRandomization.Randomize;

public class RandomizerTest {

    private final File rom = new File("./src/test/resources/rom/testRom.nds");
    private final File savedSettings
            = new File("./src/test/resources/rom/controlFiles/controlSettings.yrconf");

    @Test
    public void randomizeRom() {
        try {
            YgoRomScrambler scrambler = new YgoRomScrambler();

            YgoRandomizerSettings settings = YgoRandomizerSettings.loadFromFile(savedSettings);

            Assert.assertEquals("testSeed", settings.getSeed());
            Assert.assertEquals(RandomizeWithRarity, settings.getPackSetting());
            Assert.assertEquals(Randomize, settings.getStructureDeckSetting());

            String testSaveBytes = new String(Files.readAllBytes(savedSettings.toPath()), UTF_8);


            settings = YgoRandomizerSettings.getFromJSON(settings.toJSON());
            Assert.assertNotNull(settings);
            Assert.assertEquals("testSeed", settings.getSeed());
            Assert.assertEquals(RandomizeWithRarity, settings.getPackSetting());
            Assert.assertEquals(Randomize, settings.getStructureDeckSetting());

            try {
                scrambler.writeToFile(null);
            } catch (Exception e) {
                Assert.assertTrue(e instanceof RandomizerException);
            }

            scrambler.randomizeRom(rom, settings);

            scrambler.writeToFile(new File("./src/test/resources/rom/randomizedRom.nds"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void cleanUp() {
        Assert.assertTrue("Put a rom in /rom called \"testRom.nds\". " +
                "A rom cannot legally be distributed on github.", rom.exists());

        // delete saves from prior testing
        boolean deleted = new File("./src/test/resources/rom/testRom.sav").delete();
        deleted = deleted || new File("./src/test/resources/rom/.wrappedRom.nds").delete();
        deleted = deleted || new File("./src/test/resources/rom/.wrappedRom.sav").delete();

        if (deleted) {
            System.out.println("Files cleaned.\n");
        }
    }
}
