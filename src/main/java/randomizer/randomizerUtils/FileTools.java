package randomizer.randomizerUtils;

import ui.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility tools for file handling.
 */
public class FileTools {

    public enum OperatingSystem {Windows, Mac, Linux, Undefined}

    /**
     * The constant 1-byte charset when translation bytes to string without loss.
     */
    public static final Charset charset = StandardCharsets.ISO_8859_1;

    private static File tempFolder = null;

    private static OperatingSystem operatingSystem = null;

    /**
     * Gets file bytes as string.
     *
     * @param file the file
     * @return the string
     * @throws IOException the io exception
     */
    public static String getFileToString(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }

    /**
     * Write string to file.
     *
     * @param file    the file
     * @param content the content
     * @throws IOException the io exception
     */
    public static void writeStringToFile(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(charset), StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Delete folder and all its contents.
     *
     * @param folder the folder
     */
    public static boolean deleteFolder(File folder) {
        boolean successfulDeletion = true;
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    successfulDeletion &= deleteFolder(f);
                } else {
                    successfulDeletion &= f.delete();
                }
            }
        }
        successfulDeletion &= folder.delete();

        return successfulDeletion;
    }

    /**
     * Find all files in a directory with the given name.
     *
     * @param fileName        the file name
     * @param searchDirectory the search directory
     * @return the list of files
     */
    public static List<File> findAllFiles(String fileName, File searchDirectory) {
        List<File> foundFiles = new LinkedList<>();

        try (Stream<Path> files = Files.walk(searchDirectory.toPath())) {
            List<Path> collection = files
                    .filter(f -> f.getFileName().toString().equals(fileName))
                    .collect(Collectors.toList());

            collection.forEach(path -> foundFiles.add(path.toFile()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundFiles;
    }

    /**
     * Gets long from seed string. <br> Implementation of Java String's hashCode() method but for long
     *
     * @param seedString the seed string
     * @return the long
     */
    public static long getStringToSeed(String seedString) {
        if (seedString == null) {
            return 0L;
        }

        long seed = 0;
        for (char c : seedString.toCharArray()) {
            seed = seed * 31L + c; // String hashCode implementation for long
        }

        return seed;
    }

    /**
     * Replace file content
     *
     * @param source      the source file
     * @param destination the destination file
     * @throws IOException the io exception
     */
    public static void replaceFile(File source, File destination) throws IOException {
        Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Gets bytes to string.
     *
     * @param bytes the bytes
     * @return the bytes to string
     */
    public static String getBytesToString(byte[] bytes) {
        return new String(bytes, charset);
    }

    /**
     * Get the temporary folder created for this instance. Folder and its
     * contents will delete themselves on exit
     *
     * @return the temporary folder
     */
    public static File getTempFolder() throws IOException {
        if (tempFolder == null) {
            tempFolder = Files.createTempDirectory("ndsToolFolder").toFile();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> FileTools.deleteFolder(tempFolder)));
        }

        return tempFolder;
    }

    /**
     * Extract a resource to the default OS temp folder. Clears those files on exit.
     *
     * @param pathToFile The path to the directory the file is found in
     * @param fileName The name of the file
     * @return the now extracted file
     */
    public static File extractResourceToTempFolder(String pathToFile, String fileName) {
        try {
            getTempFolder();

            File file = new File(tempFolder.getPath() + "/" + fileName);

            if (!file.exists()) {
                InputStream link = (Main.class.getResourceAsStream("/"  + pathToFile + "/" + fileName));
                Files.copy(link, file.getAbsoluteFile().toPath());
                link.close();
            }


            if (!file.setReadable(true)) {
                System.out.println("Couldn't make file readable!");
            }

            if (!file.setExecutable(true)) {
                System.out.println("Couldn't make file executable!");
            }

            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the current operating system from an enum
     *
     * @return Operating System enum element
     */
    public static OperatingSystem getOperatingSystem() {
        if (operatingSystem == null) {
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.startsWith("windows")) {
                operatingSystem = OperatingSystem.Windows;
            } else if (osName.contains("mac")) {
                operatingSystem = OperatingSystem.Mac;
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                operatingSystem = OperatingSystem.Linux;
            } else {
                operatingSystem = OperatingSystem.Undefined;
            }
        }
        return operatingSystem;
    }
}
