package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRC;

import static ro.derbederos.crc.purejava.CRC64Util.initLookupTablesReflected;
import static ro.derbederos.crc.purejava.CRC64Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC64SlicingBy16 implements CRC {

    private final long[][] lookupTable;
    final long poly;
    final long init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64SlicingBy16(long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            lookupTable = initLookupTablesReflected(poly, 16);
        } else {
            lookupTable = initLookupTablesUnreflected(poly, 16);
        }
        reset();
    }

    @Override
    public void reset() {
        if (refIn) {
            crc = Long.reverse(init);
        } else {
            crc = init;
        }
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[0][((int) crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[0][((int) (crc >>> 56) ^ b) & 0xff];
        }
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(lookupTable, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTable, crc, src, offset, len);
        }
    }

    private static long updateReflected(long[][] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        while (len > 15) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTable[15][(low ^ src[index++]) & 0xff] ^
                    lookupTable[14][((low >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[13][((low >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[12][((low >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[11][(high ^ src[index++]) & 0xff] ^
                    lookupTable[10][((high >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[9][((high >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[8][((high >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[7][src[index++] & 0xff] ^
                    lookupTable[6][src[index++] & 0xff] ^
                    lookupTable[5][src[index++] & 0xff] ^
                    lookupTable[4][src[index++] & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 16;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTable[0][((int) localCrc ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    private static long updateUnreflected(long[][] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        while (len > 15) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTable[15][((high >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[14][((high >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[13][((high >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[12][(high ^ src[index++]) & 0xff] ^
                    lookupTable[11][((low >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[10][((low >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[9][((low >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[8][(low ^ src[index++]) & 0xff] ^
                    lookupTable[7][src[index++] & 0xff] ^
                    lookupTable[6][src[index++] & 0xff] ^
                    lookupTable[5][src[index++] & 0xff] ^
                    lookupTable[4][src[index++] & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 16;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTable[0][((int) (localCrc >>> 56) ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    @Override
    public void updateBits(int b, int bits) {
        long reflectedPoly = Long.reverse(poly);
        for (int i = 0; i < bits; i++) {
            if (refIn) {
                crc = (crc >>> 1) ^ (reflectedPoly & ~(((crc ^ b) & 1) - 1));
                b >>>= 1;
            } else {
                crc = (crc << 1) ^ (poly & ~((((crc >>> 63) ^ (b >>> 7)) & 1) - 1));
                b <<= 1;
            }
        }
    }

    @Override
    public long getValue() {
        long result = crc;
        //reflect output when necessary
        if (refOut != refIn) {
            result = Long.reverse(crc);
        }
        result = (result ^ xorOut);
        return result;
    }
}
