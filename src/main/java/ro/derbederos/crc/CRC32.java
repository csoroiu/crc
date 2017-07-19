package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC32Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.CRC32Util.fastInitLookupTableUnreflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 * https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation
 */

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 */
public class CRC32 implements Checksum {

    private final int lookupTable[];
    final int poly;
    final int init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final int xorOut;
    private int crc;

    public CRC32(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            lookupTable = fastInitLookupTableReflected(poly);
        } else {
            lookupTable = fastInitLookupTableUnreflected(poly);
        }
        reset();
    }

    public void reset() {
        if (refIn) {
            crc = Integer.reverse(init);
        } else {
            crc = init;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ b) & 0xff];
        }
    }

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        if (refIn) {
            updateReflected(src, offset, len);
        } else {
            updateUnreflected(src, offset, len);
        }
    }

    private void updateReflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc >>> 8) ^ lookupTable[(crc ^ value) & 0xff];
        }
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ value) & 0xff];
        }
    }

    public long getValue() {
        if (refOut == refIn) {
            return ((long) (crc ^ xorOut)) & 0xFFFFFFFFL;
        } else {
            return ((long) (Integer.reverse(crc) ^ xorOut)) & 0xFFFFFFFFL;
        }
    }
}
