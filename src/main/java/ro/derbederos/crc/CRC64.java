package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.CRC64Util.fastInitLookupTableUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
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
            crc = updateReflected(lookupTable, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTable, crc, src, offset, len);
        }
    }

    private static long updateReflected(long[] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            localCrc = (localCrc >>> 8) ^ lookupTable[(int) ((localCrc ^ value) & 0xff)];
        }
        return localCrc;
    }

    private static long updateUnreflected(long[] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            localCrc = (localCrc << 8) ^ lookupTable[(int) (((localCrc >>> 56) ^ value) & 0xff)];
        }
        return localCrc;
    }

    public long getValue() {
        if (refOut == refIn) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
