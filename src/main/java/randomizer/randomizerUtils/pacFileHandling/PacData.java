package randomizer.randomizerUtils.pacFileHandling;

import randomizer.randomizerUtils.ByteWord;
import randomizer.randomizerUtils.exceptions.PacUnwrappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static randomizer.randomizerUtils.ByteTools.*;

class PacData {

    private final byte[] pacBytes;
    private int fileDataBeginningAddress;

    private Map<String, PacFile> fileStructure;

    PacData(File pacFile) throws IOException {
        this(Files.readAllBytes(pacFile.toPath()));
    }

    PacData(byte[] pacBytes) {
        this.pacBytes = pacBytes;

        LinkedList<ByteWord> sectors = divideBytesIntoSectors(this.pacBytes);


        List<ByteWord> fileNames = readFileNames(sectors.getFirst());
        List<PacFile> pacFiles = getPacFilesStructure(sectors.getLast());

        fillFileStructure(fileNames, pacFiles);
    }

    public List<String> getFileNames() {
        return new ArrayList<>(fileStructure.keySet());
    }

    public byte[] getFileBytes(String fileName) {
        PacFile file = fileStructure.get(fileName);
        if (file != null) {
            return file.getData();
        }
        return null;
    }

    public boolean overrideFileData(String fileName, byte[] data) {
        PacFile file = fileStructure.get(fileName);
        if (file != null) {
            file.setData(data);
            return true;
        }
        return false;
    }

    public byte[] repackPac() {
        int newTotalFileSize = 0;

        for (PacFile pacFile : fileStructure.values()) {
            int pacFileSpace = pacFile.getAddress() + pacFile.getFileSize();
            if (pacFileSpace > newTotalFileSize) {
                newTotalFileSize = pacFileSpace;
            }
        }

        byte[] newPacBytes = new byte[newTotalFileSize];

        System.arraycopy(pacBytes, 0, newPacBytes, 0, fileDataBeginningAddress);

        for (PacFile pacFile : fileStructure.values()) {
            for (int i = 0; i < pacFile.getData().length; ++i) {
                newPacBytes[pacFile.getAddress() + i] = pacFile.getData()[i];
            }
        }

        return newPacBytes;
    }

    private LinkedList<ByteWord> divideBytesIntoSectors(byte[] fileData) {
        ByteWord header = null;
        ByteWord fileNames = null;

        int i = 0;

        for (; i < fileData.length; i += 2) {
            if ((fileData[i] & 0xFF) == 0xFF && (fileData[i + 1] & 0xFF) == 0xFF) {
                i -= (i % 16);
                header = new ByteWord(Arrays.copyOfRange(fileData, 0, i));
                break;
            }
        }

        int fileNamesBeginning = i;
        for (; i < fileData.length; i += 16) {
            byte[] currentLine = Arrays.copyOfRange(fileData, i, i + 8);
            if (getBytesToInt(currentLine) == 0) {
                fileNames = new ByteWord(Arrays.copyOfRange(fileData, fileNamesBeginning, i));
                break;
            }
        }

        for (; i < fileData.length; i += 16) {
            byte[] currentLine = Arrays.copyOfRange(fileData, i, i + 8);
            if (getBytesToInt(currentLine) != 0) {
                break;
            }
        }

        fileDataBeginningAddress = i;

        LinkedList<ByteWord> byteWords = new LinkedList<>();
        byteWords.add(header);
        byteWords.add(fileNames);
        return byteWords;
    }

    private LinkedList<ByteWord> readFileNames(ByteWord header) {
        byte[] fileData = header.getBytes();

        LinkedList<ByteWord> words = new LinkedList<>();

        int beginningOfWord = 0;

        // Get file names
        for (int i = 0; i < fileData.length; ++i) {
            byte currentByte = fileData[i];

            if (isByteInByteArray(currentByte, lineBreakBytes)) {
                byte[] wordBytes = Arrays.copyOfRange(fileData, beginningOfWord, i);
                words.add(new ByteWord(wordBytes));

                beginningOfWord = i + 2;

                ++i; // skip ahead a symbol that is not part of the name
            }
        }

        // Last element skipped in iteration otherwise

        int finalCharacter = fileData.length - 1; // Trim off unneeded spaces at the end
        while ((fileData[finalCharacter - 3] & 0xFF) == 0x00) {
            --finalCharacter;
        }
        words.add(new ByteWord(Arrays.copyOfRange(fileData, beginningOfWord, finalCharacter)));

        words.removeIf(w -> !w.toString().contains("."));

        return words;
    }

    private ArrayList<PacFile> getPacFilesStructure(ByteWord byteWord) {
        LinkedList<PacFile> files = new LinkedList<>();

        for (int i = 8; i < byteWord.getBytes().length; i += 8) {
            // Decode slice in little endian - first 4 bytes is file size, rest is address offset
            byte[] byteSlice = reverseByteArray(Arrays.copyOfRange(byteWord.getBytes(), i, i + 8));

            int fileSize = getBytesToInt(Arrays.copyOfRange(byteSlice, 0, 4));
            if (fileSize != 0) {
                int address = getBytesToInt(Arrays.copyOfRange(byteSlice, 4, 8));

                files.add(new PacFile(fileSize, fileDataBeginningAddress + address));
            }
        }

        return new ArrayList<>(files);
    }

    private void fillFileStructure(List<ByteWord> fileNames, List<PacFile> allFiles) {
        if (fileNames.size() != allFiles.size()) {
            throw new PacUnwrappingException("Mismatch between file name count and file count: "
                    + fileNames.size() + " file names to " + allFiles.size() + " files.");
        }

        fileStructure = new HashMap<>();

        for (int i = 0; i < fileNames.size(); ++i) {
            String cleanedFileName = fileNames.get(i).toString().trim();

            PacFile file = allFiles.get(i);
            file.setFileName(cleanedFileName);
            int from = file.getAddress();
            int to = file.getAddress() + file.getFileSize();

            file.setData(Arrays.copyOfRange(pacBytes, from, to));

            fileStructure.put(cleanedFileName, file);
        }
    }
}
