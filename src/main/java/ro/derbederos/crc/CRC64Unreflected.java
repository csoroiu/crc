package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.fastInitLookupTableUnreflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 for big endian (unreflected) byte input using different models.
 */
public class CRC64Unreflected implements Checksum {

    private final long lookupTable[];
    final long poly;
    final long init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64Unreflected(long poly, long init, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        lookupTable = fastInitLookupTableUnreflected(poly);
        reset();
    }

    public void reset() {
        crc = init;
    }

    public void update(int b) {
        crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ b) & 0xff)];
    }

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        updateUnreflected(src, offset, len);
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ value) & 0xff)];
        }
    }

    public long getValue() {
        if (!refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
