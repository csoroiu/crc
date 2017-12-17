package ro.derbederos.crc.purejava;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 64 using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC64Generic extends CRC64SlicingBy16 {
    private final int width;

    public CRC64Generic(int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        super(poly << 64 - width, init << 64 - width, refIn, refOut, refOut ? xorOut : xorOut << 64 - width);
        this.width = width;
    }

    @Override
    public long getValue() {
        long result = super.getValue();
        if (!refOut) {
            result >>>= 64 - width;
        }
        return result;
    }
}
