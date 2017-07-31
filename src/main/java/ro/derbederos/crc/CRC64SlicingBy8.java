package ro.derbederos.crc;

import static ro.derbederos.crc.CRC64Util.initLookupTablesReflected;
import static ro.derbederos.crc.CRC64Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC64SlicingBy8 implements CRC {

    private final long[][] lookupTable;
    final long poly;
    final long init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64SlicingBy8(long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            lookupTable = initLookupTablesReflected(poly, 8);
        } else {
            lookupTable = initLookupTablesUnreflected(poly, 8);
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
        while (len > 7) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTable[7][(low ^ src[index++]) & 0xff] ^
                    lookupTable[6][((low >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((low >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[4][((low >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[3][(high ^ src[index++]) & 0xff] ^
                    lookupTable[2][((high >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[1][((high >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[0][((high >>> 24) ^ src[index++]) & 0xff];
            len -= 8;
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
        while (len > 7) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTable[7][((high >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[6][((high >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((high >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[4][(high ^ src[index++]) & 0xff] ^
                    lookupTable[3][((low >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[2][((low >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[1][((low >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[0][(low ^ src[index++]) & 0xff];
            len -= 8;
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
