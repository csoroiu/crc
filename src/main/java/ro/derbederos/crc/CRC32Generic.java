package ro.derbederos.crc;

/**
 * Byte-wise CRC implementation that can compute CRC with width <= 32 using different models.
 */
public class CRC32Generic extends CRC32 {
    final private int width;

    public CRC32Generic(int width, int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        super(poly << 32 - width, init << 32 - width, refIn, refOut, refOut ? xorOut : xorOut << 32 - width);
        this.width = width;
    }

    public long getValue() {
        long result = super.getValue();
        if (!refOut) {
            result >>>= 32 - width;
        }
        return result;
    }
}
