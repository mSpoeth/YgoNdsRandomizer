package randomizer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import randomizer.randomizerUtils.randomizer.YgoRandomizerSettings;
import ui.Main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.PackRandomization.RandomizeCompletely;
import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.StructureDeckRandomization.NoChange;
import static randomizer.randomizerUtils.randomizer.YgoRandomizerSettings.loadFromFile;

public class CommandLineTest {
    private final File rom = new File("./src/test/resources/rom/testRom.nds");
    private final File savedSettings
            = new File("./src/test/resources/rom/controlFiles/controlSettings.yrconf");

    @Test
    public void randomizeRom() {
        try {

            String romOutput = "src/test/resources/rom/cliOutput";
            String settingsExport = "src/test/resources/rom/cliSettings";

            List<String> arguments = new LinkedList<>();
            arguments.add("-r");
            arguments.add(rom.getAbsolutePath());

            arguments.add("-o");
            arguments.add(romOutput);

            arguments.add("-es");
            arguments.add(settingsExport);

            arguments.add("-s");
            arguments.add(savedSettings.getAbsolutePath());

            arguments.add("-p");
            arguments.add("2");

            arguments.add("-sd");
            arguments.add("0");

            Main.main(arguments.toArray(new String[0]));

            File settingsFile = new File("./" + settingsExport + ".yrconf");
            YgoRandomizerSettings cliSettings = loadFromFile(settingsFile);

            Assert.assertEquals("testSeed", cliSettings.getSeed());
            Assert.assertEquals(RandomizeCompletely, cliSettings.getPackSetting());
            Assert.assertEquals(NoChange, cliSettings.getStructureDeckSetting());

            File cliRom = new File("./" + romOutput + ".nds");
            Assert.assertTrue(cliRom.exists());
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
        deleted = deleted || new File("./src/test/resources/rom/.cliOutput.nds").delete();
        deleted = deleted || new File("./src/test/resources/rom/.cliSettings.yrconf").delete();

        if (deleted) {
            System.out.println("Files cleaned.\n");
        }
    }
}
