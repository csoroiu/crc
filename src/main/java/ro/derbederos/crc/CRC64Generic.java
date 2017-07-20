package ro.derbederos.crc;

/**
 * Byte-wise CRC implementation that can compute CRC with width <= 64 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 */
public class CRC64Generic extends CRC64SlicingBy8 {
    private final int width;

    public CRC64Generic(int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        super(poly << 64 - width, init << 64 - width, refIn, refOut, refOut ? xorOut : xorOut << 64 - width);
        this.width = width;
    }

    public long getValue() {
        long result = super.getValue();
        if (!refOut) {
            result >>>= 64 - width;
        }
        return result;
    }
}
