package randomizer.randomizerUtils;

import java.util.Arrays;

/**
 * Utility tools for working with bytes
 */
public class ByteTools {

    /**
     * The constant line break bytes.
     */
    public static final byte[] lineBreakBytes = {0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18};

    /**
     * Get int from bytes
     *
     * @param bytes the bytes
     * @return the int
     */
    public static int getBytesToInt(byte[] bytes) {
        return getBytesToInt(bytes, false);
    }

    /**
     * Gets bytes to int.
     *
     * @param bytes        the bytes
     * @param littleEndian whether it's little endian
     * @return the int
     */
    public static int getBytesToInt(byte[] bytes, boolean littleEndian) {
        int sum = 0;

        for (int i = 0; i < bytes.length; ++i) {
            int currentByteIndex = littleEndian ? i : (bytes.length - (i + 1));
            int power = (int) Math.pow(256, i);

            sum += (bytes[currentByteIndex] & 0xFF) * power;
        }

        return sum;
    }

    /**
     * Get string representation of each byte. 2 hex each.
     *
     * @param bytes the bytes
     * @return the byte string
     */
    public static String getBytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X ", b));
        }

        return stringBuilder.toString();
    }

    /**
     * Gets byte to string.
     *
     * @param b the byte
     * @return the byte to string
     */
    public static String getByteToString(byte b) {
        return String.format("%02X", b);
    }

    /**
     * Reverse byte array
     *
     * @param bytes the bytes
     * @return the reversed byte array
     */
    public static byte[] reverseByteArray(byte[] bytes) {
        byte[] reversedBytes = Arrays.copyOf(bytes, bytes.length);

        for (int i = 0; i < reversedBytes.length / 2; i++) {
            byte temp = reversedBytes[i];
            reversedBytes[i] = reversedBytes[reversedBytes.length - i - 1];
            reversedBytes[reversedBytes.length - i - 1] = temp;
        }
        return reversedBytes;
    }

    /**
     * Is the byte contained in a byte array
     *
     * @param b    the byte
     * @param bytes the array of bytes, that may contain b
     * @return true if b is in bytes array
     */
    public static boolean isByteInByteArray(byte b, byte[] bytes) {
        for (byte element : bytes) {
            if (element == b) {
                return true;
            }
        }
        return false;
    }
}
