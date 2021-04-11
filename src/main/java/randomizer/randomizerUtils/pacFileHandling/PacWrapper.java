package randomizer.randomizerUtils.pacFileHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Wrapper class for PacData
 */
public class PacWrapper {
    private final File pac;
    private PacData pacData;

    /**
     * Instantiates a new Pac wrapper and unpacks the pac file
     *
     * @param pac The pac file
     * @throws IOException the io exception
     */
    public PacWrapper(File pac) throws IOException {
        this.pac = pac;
        unPack();
    }

    private void unPack() throws IOException {

        byte[] fileData = Files.readAllBytes(pac.toPath());
        pacData = new PacData(fileData);
    }

    /**
     * Repack the pack file overrides the pac file given in the constructor.
     *
     * @throws IOException the io exception
     */
    public void repack() throws IOException {
        Files.write(pac.toPath(), pacData.repackPac());
    }

    /**
     * Gets all file names in the pac.
     *
     * @return A list of file names
     */
    public List<String> getFileNames() {
        return pacData.getFileNames();
    }

    /**
     * Get file bytes from the given file
     *
     * @param fileName the file name
     * @return the bytes of the file
     */
    public byte[] getFileBytes(String fileName) {
        return pacData.getFileBytes(fileName);
    }

    /**
     * Override the file data of the given file.
     *
     * @param fileName the file name
     * @param data     the new data as bytes
     * @return true if successful
     */
    public boolean overrideFileData(String fileName, byte[] data) {
        return pacData.overrideFileData(fileName, data);
    }
}
