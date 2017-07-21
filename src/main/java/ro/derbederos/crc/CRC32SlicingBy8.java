package ro.derbederos.crc;

import sun.misc.Unsafe;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.CRC32Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC32SlicingBy8 implements Checksum {

    private static final boolean ARRAY_BYTE_INDEX_SCALE_ONE = Unsafe.ARRAY_BYTE_INDEX_SCALE == 1;

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

    public void reset() {
        if (refIn) {
            crc = Integer.reverse(init);
        } else {
            crc = init;
        }
    }

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
        if (len > 7 && ARRAY_BYTE_INDEX_SCALE_ONE) {
            int alignLength = getByteArrayAlignLength(offset);
            while (alignLength > 0) {
                localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index++]) & 0xff];
                alignLength--;
                len--;
            }
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
        if (len > 7 && ARRAY_BYTE_INDEX_SCALE_ONE) {
            int alignLength = getByteArrayAlignLength(offset);
            while (alignLength > 0) {
                localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
                alignLength--;
                len--;
            }
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
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    private static int getByteArrayAlignLength(int offset) {
        return (8 - ((Unsafe.ARRAY_BYTE_BASE_OFFSET + offset) & 0x7)) & 0x7;
    }

    public long getValue() {
        if (refOut == refIn) {
            return ((long) (crc ^ xorOut)) & 0xFFFFFFFFL;
        } else {
            return ((long) (Integer.reverse(crc) ^ xorOut)) & 0xFFFFFFFFL;
        }
    }
}
