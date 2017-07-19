package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.CRC64Util.fastInitLookupTableUnreflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 * https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 */
public class CRC64 implements Checksum {

    private final long[] lookupTable;
    final long poly;
    final long init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64(long poly, long init, boolean refIn, boolean refOut, long xorOut) {
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
            crc = Long.reverse(init);
        } else {
            crc = init;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ b) & 0xff)];
        } else {
            crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ b) & 0xff)];
        }
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            updateReflected(src, offset, len);
        } else {
            updateUnreflected(src, offset, len);
        }
    }

    private void updateReflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ value) & 0xff)];
        }
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ value) & 0xff)];
        }
    }

    public long getValue() {
        if (refOut == refIn) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
