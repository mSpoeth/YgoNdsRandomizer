package randomizer.randomizerUtils;

/**
 * Wrapper class around a byte array
 */
public class ByteWord {

    private final byte[] bytes;


    /**
     * Instantiates a new Byte word.
     *
     * @param bytes the bytes
     */
    public ByteWord(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * To hex byte string. Calls ByteTool's getBytesToString on bytes
     *
     * @return the string
     */
    public String toHexByteString() {
        return ByteTools.getBytesToString(bytes);
    }

    /**
     * Get bytes
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return FileTools.getBytesToString(bytes);
    }
}
