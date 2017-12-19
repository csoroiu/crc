package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRCModel;

import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC32SlicingBy16 extends CRC32 {

    protected final int[][] lookupTables;

    public CRC32SlicingBy16(CRCModel crcModel) {
        super(crcModel);
        if (this.refIn) {
            lookupTables = initLookupTablesReflected(this.poly, 16);
        } else {
            lookupTables = initLookupTablesUnreflected(this.poly, 16);
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
        while (len > 15) {
            localCrc = lookupTables[15][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTables[14][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTables[13][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTables[12][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTables[11][src[index++] & 0xff] ^
                    lookupTables[10][src[index++] & 0xff] ^
                    lookupTables[9][src[index++] & 0xff] ^
                    lookupTables[8][src[index++] & 0xff] ^
                    lookupTables[7][src[index++] & 0xff] ^
                    lookupTables[6][src[index++] & 0xff] ^
                    lookupTables[5][src[index++] & 0xff] ^
                    lookupTables[4][src[index++] & 0xff] ^
                    lookupTables[3][src[index++] & 0xff] ^
                    lookupTables[2][src[index++] & 0xff] ^
                    lookupTables[1][src[index++] & 0xff] ^
                    lookupTables[0][src[index++] & 0xff];
            len -= 16;
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
        while (len > 15) {
            localCrc = lookupTables[15][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTables[14][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTables[13][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTables[12][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTables[11][src[index++] & 0xff] ^
                    lookupTables[10][src[index++] & 0xff] ^
                    lookupTables[9][src[index++] & 0xff] ^
                    lookupTables[8][src[index++] & 0xff] ^
                    lookupTables[7][src[index++] & 0xff] ^
                    lookupTables[6][src[index++] & 0xff] ^
                    lookupTables[5][src[index++] & 0xff] ^
                    lookupTables[4][src[index++] & 0xff] ^
                    lookupTables[3][src[index++] & 0xff] ^
                    lookupTables[2][src[index++] & 0xff] ^
                    lookupTables[1][src[index++] & 0xff] ^
                    lookupTables[0][src[index++] & 0xff];
            len -= 16;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTables[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            len--;
        }
        return localCrc;
    }
}
