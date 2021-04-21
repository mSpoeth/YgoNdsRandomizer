package randomizer.nds.utils;

import randomizer.nds.exceptions.RomWrapperException;
import randomizer.randomizerUtils.FileTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static randomizer.nds.utils.ndsTool.NdsToolCommandCreator.createCommand;

public class RomUnwrapper {

    private final File ndsTool;

    public RomUnwrapper(String ndsToolPath) {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        if (isWindows) {
            ndsToolPath += ".exe";
        }

        File ndsToolFile = new File(ndsToolPath);

        if (!ndsToolFile.exists()) {
            ndsToolFile = FileTools.extractResourceToTempFolder("executables", ndsToolFile.getName());
        }


        this.ndsTool = ndsToolFile;
    }

    public File unwrapRom(File rom) throws IOException, InterruptedException {
        File extractedRomFolder = new File(rom.getParent() + "/.Extracted");

        String ndsToolExtract = "-x \"" + rom.toPath().toRealPath() + "\"";

        if (!extractedRomFolder.exists()) {
            if (!extractedRomFolder.mkdir()) {
                throw new RomWrapperException("Rom could not be unwrapped:" +
                        " Folder could not be created for extracted files.");
            }
        }

        List<String> command = createCommand(ndsTool, ndsToolExtract, extractedRomFolder);
        callNdsTool(command);

        return extractedRomFolder;
    }

    public File wrapRom(File romDataFolder) throws IOException, InterruptedException {
        File wrappedRom = new File(romDataFolder.getParent() + "/.wrappedRom.nds");

        String ndsToolCreate = "-c \"" + wrappedRom.getPath() + "\"";

        if (wrappedRom.exists()) {
            if (!wrappedRom.delete()) {
                throw new RomWrapperException("Rom could not be wrapped:" +
                        " Rom file was already present and could not be deleted.");
            }
        }

        List<String> command = createCommand(ndsTool, ndsToolCreate, romDataFolder);
        callNdsTool(command);

        FileTools.deleteFolder(romDataFolder);

        return wrappedRom;
    }

    private void callNdsTool(List<String> command) throws IOException, InterruptedException {
        callNdsTool(command, false);
    }

    private void callNdsTool(List<String> command, boolean verbose) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();

        process.waitFor();

        if (verbose) {
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                System.out.println(nextLine);
            }

            inputStreamReader.close();
            reader.close();
        }
    }
}
