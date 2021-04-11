package romTools;

import org.junit.Assert;
import org.junit.Test;
import randomizer.randomizerUtils.FileTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileToolTest {

    private final File rom = new File("./src/test/resources/rom/testRom.nds");

    @Test
    public void testFileToString() {
        try {
            File romClone = new File(rom.getParent() + "/clonedRom.nds");
            Assert.assertTrue("Couldn't create new file", romClone.createNewFile());

            String romData = FileTools.getFileToString(rom);
            FileTools.writeStringToFile(romClone, romData);

            byte[] originalBytes = Files.readAllBytes(rom.toPath());
            byte[] cloneBytes = Files.readAllBytes(romClone.toPath());

            Assert.assertTrue("Couldn't delete new file", romClone.delete());

            Assert.assertArrayEquals(originalBytes, cloneBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
