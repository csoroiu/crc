package ro.derbederos.crc;

import sun.misc.Unsafe;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.initLookupTablesReflected;
import static ro.derbederos.crc.CRC64Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC64SlicingBy16 implements Checksum {

    private static final boolean ARRAY_BYTE_INDEX_SCALE_ONE = Unsafe.ARRAY_BYTE_INDEX_SCALE == 1;

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

    public void reset() {
        if (refIn) {
            crc = Long.reverse(init);
        } else {
            crc = init;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[0][(int) ((crc ^ b) & 0xff)];
        } else {
            crc = (crc << 8) ^ lookupTable[0][(int) (((crc >>> 56) ^ b) & 0xff)];
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

    private static long updateReflected(long[][] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        if (len > 15 && ARRAY_BYTE_INDEX_SCALE_ONE) {
            int alignLength = getByteArrayAlignLength(offset);
            while (alignLength > 0) {
                localCrc = (localCrc >>> 8) ^ lookupTable[0][(int) ((localCrc ^ src[index++]) & 0xff)];
                alignLength--;
                len--;
            }
            while (len > 15) {
                localCrc = lookupTable[15][(int) ((localCrc ^ src[index++]) & 0xff)] ^
                           lookupTable[14][(int) (((localCrc >>> 8) ^ src[index++]) & 0xff)] ^
                           lookupTable[13][(int) (((localCrc >>> 16) ^ src[index++]) & 0xff)] ^
                           lookupTable[12][(int) (((localCrc >>> 24) ^ src[index++]) & 0xff)] ^
                           lookupTable[11][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)] ^
                           lookupTable[10][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                           lookupTable[9][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                           lookupTable[8][(int) ((localCrc >>> 56) ^ src[index++]) & 0xff] ^
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
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTable[0][(int) ((localCrc ^ src[index++]) & 0xff)];
            len--;
        }
        return localCrc;
    }

    private static long updateUnreflected(long[][] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        if (len > 15 && ARRAY_BYTE_INDEX_SCALE_ONE) {
            int alignLength = getByteArrayAlignLength(offset);
            while (alignLength > 0) {
                localCrc = (localCrc << 8) ^ lookupTable[0][(int) (((localCrc >>> 56) ^ src[index++]) & 0xff)];
                alignLength--;
                len--;
            }
            while (len > 15) {
                localCrc = lookupTable[15][(int) ((localCrc >>> 56) ^ src[index++]) & 0xff] ^
                           lookupTable[14][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                           lookupTable[13][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                           lookupTable[12][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)] ^
                           lookupTable[11][(int) (((localCrc >>> 24) ^ src[index++]) & 0xff)] ^
                           lookupTable[10][(int) (((localCrc >>> 16) ^ src[index++]) & 0xff)] ^
                           lookupTable[9][(int) (((localCrc >>> 8) ^ src[index++]) & 0xff)] ^
                           lookupTable[8][(int) ((localCrc ^ src[index++]) & 0xff)] ^
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
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTable[0][(int) (((localCrc >>> 56) ^ src[index++]) & 0xff)];
            len--;
        }
        return localCrc;
    }

    private static int getByteArrayAlignLength(int offset) {
        return (8 - ((Unsafe.ARRAY_BYTE_BASE_OFFSET + offset) & 0x7)) & 0x7;
    }

    public long getValue() {
        if (refOut == refIn) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
