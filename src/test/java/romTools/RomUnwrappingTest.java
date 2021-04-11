package romTools;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import randomizer.nds.utils.RomUnwrapper;
import randomizer.randomizerUtils.FileTools;
import randomizer.randomizerUtils.pacFileHandling.PacWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class RomUnwrappingTest {

    private final File ndsTool = new File("./src/main/resources/executables/ndstool");
    private final File rom = new File("./src/test/resources/rom/testRom.nds");

    private final File controlFile = new File("./src/test/resources/rom/controlFiles/card_name_e.bin");
    private final File modifiedFile = new File("./src/test/resources/rom/controlFiles/card_name_e_modified.bin");

    @Test
    public void unwrapRom() {
        RomUnwrapper romUnwrapper = new RomUnwrapper(ndsTool.getPath());

        try {
            // unwrap rom
            System.out.print("Unwrapping " + rom.getName());
            File extractedDataFolder = romUnwrapper.unwrapRom(rom);
            System.out.print(" to folder " + extractedDataFolder.getName() + ".\n");

            Assert.assertNotNull(extractedDataFolder);
            Assert.assertNotNull(extractedDataFolder.list());

            // re-wrap rom
            System.out.print("Wrapping " + extractedDataFolder.getName());
            File finishedRom = romUnwrapper.wrapRom(extractedDataFolder);
            System.out.print(" folder to " + finishedRom.getName() + ".\n");

            Assert.assertNotNull(finishedRom);
            Assert.assertTrue(finishedRom.exists());
            Assert.assertFalse(extractedDataFolder.exists());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unwrapPac() {
        RomUnwrapper romUnwrapper = new RomUnwrapper(ndsTool.getPath());

        try {
            // unwrap rom
            System.out.print("Unwrapping " + rom.getName());
            File extractedDataFolder = romUnwrapper.unwrapRom(rom);
            System.out.print(" to folder " + extractedDataFolder.getName() + ".\n");

            // .pac stuff
            File pac = new File(extractedDataFolder.getPath() + "/data/Data_arc_pac/bin2.pac");
            byte[] originalBytes = Files.readAllBytes(pac.toPath());

            System.out.println("Unwrapping " + pac.getName());
            PacWrapper pacWrapper = new PacWrapper(pac);

            System.out.println("Reading out file names");
            List<String> fileNames = pacWrapper.getFileNames();
            Assert.assertEquals(99, fileNames.size());
            Assert.assertTrue(fileNames.contains("card_name_e.bin"));

            System.out.println("Checking extracted file integrity");

            byte[] externallyExtractedPacFile = Files.readAllBytes(controlFile.toPath());
            byte[] extractedPacFile = pacWrapper.getFileBytes("card_name_e.bin");

            Assert.assertArrayEquals(externallyExtractedPacFile, extractedPacFile);

            byte[] repackedBytes = Files.readAllBytes(pac.toPath());
            Assert.assertArrayEquals(originalBytes, repackedBytes);

            System.out.println("Inserting new file data");
            byte[] modifiedBytes = Files.readAllBytes(modifiedFile.toPath());
            pacWrapper.overrideFileData("card_name_e.bin", modifiedBytes);
            pacWrapper.repack();

            repackedBytes = Files.readAllBytes(pac.toPath());
            Assert.assertFalse(Arrays.equals(originalBytes, repackedBytes));

            // re-wrap rom
            System.out.print("Wrapping " + extractedDataFolder.getName());
            File finishedRom = romUnwrapper.wrapRom(extractedDataFolder);
            System.out.print(" folder to " + finishedRom.getName() + ".\n");

            FileTools.deleteFolder(extractedDataFolder);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void cleanUp() {
        Assert.assertTrue("Put a rom in /rom called \"testRom.nds\". " +
                "A rom cannot legally be distributed on github.", rom.exists());

        // delete saves from prior testing
        boolean save = new File("./src/test/resources/rom/testRom.sav").delete();
        save = save || new File("./src/test/resources/rom/.wrappedRom.nds").delete();
        save = save || new File("./src/test/resources/rom/.wrappedRom.sav").delete();

        if (save) {
            System.out.println("Files cleaned.\n");
        }
    }
}
