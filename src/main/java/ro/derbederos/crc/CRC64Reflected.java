package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.fastInitLookupTableReflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 for little endian (reflected) byte input using different models.
 */
public class CRC64Reflected implements Checksum {

    private final long lookupTable[];
    final long poly;
    final long init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64Reflected(long poly, long init, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        lookupTable = fastInitLookupTableReflected(poly);
        reset();
    }

    public void reset() {
        crc = Long.reverse(init);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ b) & 0xff)];
    }

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        updateReflected(src, offset, len);
    }

    private void updateReflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ value) & 0xff)];
        }
    }

    public long getValue() {
        if (refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
