package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRCModel;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 64 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC64SlicingBy8 extends CRC64 {

    public CRC64SlicingBy8(CRCModel crcModel) {
        super(crcModel, 8);
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(lookupTables, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTables, crc, src, offset, len);
        }
    }

    private static long updateReflected(long[][] lookupTables, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        while (len > 7) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTables[7][(low ^ src[index++]) & 0xFF] ^
                    lookupTables[6][((low >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[5][((low >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[4][((low >>> 24) ^ src[index++]) & 0xFF] ^
                    lookupTables[3][(high ^ src[index++]) & 0xFF] ^
                    lookupTables[2][((high >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[1][((high >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[0][((high >>> 24) ^ src[index++]) & 0xFF];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTables[0][((int) localCrc ^ src[index++]) & 0xFF];
            len--;
        }
        return localCrc;
    }

    private static long updateUnreflected(long[][] lookupTables, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        while (len > 7) {
            int high = (int) (localCrc >>> 32);
            int low = (int) localCrc;
            localCrc = lookupTables[7][((high >>> 24) ^ src[index++]) & 0xFF] ^
                    lookupTables[6][((high >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[5][((high >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[4][(high ^ src[index++]) & 0xFF] ^
                    lookupTables[3][((low >>> 24) ^ src[index++]) & 0xFF] ^
                    lookupTables[2][((low >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[1][((low >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[0][(low ^ src[index++]) & 0xFF];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTables[0][((int) (localCrc >>> 56) ^ src[index++]) & 0xFF];
            len--;
        }
        return localCrc;
    }
}
