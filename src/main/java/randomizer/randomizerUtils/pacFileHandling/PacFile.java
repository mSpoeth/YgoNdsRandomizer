package randomizer.randomizerUtils.pacFileHandling;

class PacFile {

    private String fileName = "";
    private byte[] data = {};

    private final int fileSize;
    private final int address;

    public PacFile(int fileSize, int address) {
        this.fileSize = fileSize;
        this.address = address;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(byte[] bytes) {
        data = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getAddress() {
        return address;
    }
}
