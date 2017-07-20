package ro.derbederos.crc;

/**
 * Byte-wise CRC implementation that can compute CRC with width <= 16 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC16Generic extends CRC16 {
    private final int width;

    public CRC16Generic(int width, int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        super(poly << 16 - width, init << 16 - width, refIn, refOut, refOut ? xorOut : xorOut << 16 - width);
        this.width = width;
    }

    public long getValue() {
        long result = super.getValue();
        if (!refOut) {
            result >>>= 16 - width;
        }
        return result;
    }
}
