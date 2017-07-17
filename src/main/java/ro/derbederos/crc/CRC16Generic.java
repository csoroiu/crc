package ro.derbederos.crc;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 */

/**
 * Byte-wise CRC implementation that can compute CRC with width <= 16 using different models.
 */
public class CRC16Generic extends CRC16 {
    final private int width;

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
