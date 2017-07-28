package ro.derbederos.crc;

import static ro.derbederos.crc.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.CRC32Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC32SlicingBy8 implements CRC {

    private final int[][] lookupTable;
    final int poly;
    final int init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final int xorOut;
    private int crc;

    public CRC32SlicingBy8(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
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
            crc = Integer.reverse(init);
        } else {
            crc = init;
        }
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[0][(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[0][((crc >>> 24) ^ b) & 0xff];
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

    private static int updateReflected(int[][] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTable[7][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTable[6][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[4][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    private static int updateUnreflected(int[][] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTable[7][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[6][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[4][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    @Override
    public void updateBits(int b, int bits) {
        int reflectedPoly = Integer.reverse(poly);
        for (int i = 0; i < bits; i++) {
            if (refIn) {
                crc = (crc >>> 1) ^ (reflectedPoly & ~(((crc ^ b) & 1) - 1));
                b >>>= 1;
            } else {
                crc = (crc << 1) ^ (poly & ~((((crc >>> 31) ^ (b >>> 7)) & 1) - 1));
                b <<= 1;
            }
        }
    }

    @Override
    public long getValue() {
        long result = crc;
        //reflect output when necessary
        if (refOut != refIn) {
            result = Integer.reverse(crc);
        }
        result = (result ^ xorOut) & 0xFFFFFFFFL;
        return result;
    }
}
