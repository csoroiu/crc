package ro.derbederos.crc.purejava;

import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC32SlicingBy8 extends CRC32 {

    protected final int[][] lookupTables;

    public CRC32SlicingBy8(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        super(poly, init, refIn, refOut, xorOut);
        if (refIn) {
            lookupTables = initLookupTablesReflected(poly, 8);
        } else {
            lookupTables = initLookupTablesUnreflected(poly, 8);
        }
        lookupTables[0] = lookupTable;
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(lookupTables, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTables, crc, src, offset, len);
        }
    }

    private static int updateReflected(int[][] lookupTables, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTables[7][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTables[6][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTables[5][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTables[4][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTables[3][src[index++] & 0xff] ^
                    lookupTables[2][src[index++] & 0xff] ^
                    lookupTables[1][src[index++] & 0xff] ^
                    lookupTables[0][src[index++] & 0xff];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTables[0][(localCrc ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }

    private static int updateUnreflected(int[][] lookupTables, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTables[7][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTables[6][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTables[5][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTables[4][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTables[3][src[index++] & 0xff] ^
                    lookupTables[2][src[index++] & 0xff] ^
                    lookupTables[1][src[index++] & 0xff] ^
                    lookupTables[0][src[index++] & 0xff];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTables[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }
}
