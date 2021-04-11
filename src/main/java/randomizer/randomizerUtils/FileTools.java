package randomizer.randomizerUtils;

import java.io.File;
import java.io.IOException;
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

    /**
     * The constant 1-byte charset when translation bytes to string without loss.
     */
    public static final Charset charset = StandardCharsets.ISO_8859_1;

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
}
