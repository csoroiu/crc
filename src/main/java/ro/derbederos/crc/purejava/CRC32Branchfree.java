package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRCModel;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 32 using different models.
 * It is an implementation of improved branch approach from http://create.stephan-brumme.com/crc32/#fastest-bitwise-crc32.
 */
public class CRC32Branchfree extends CRC32 {

    public CRC32Branchfree(CRCModel crcModel) {
        super(crcModel, 0);
    }

    @Override
    protected int updateByteReflected(int crc, int b) {
        crc ^= b;
        for (int i = 0; i < 8; i++) {
            crc = (crc >>> 1) ^ (poly & -(crc & 1));
        }
        return crc;
    }

    @Override
    protected int updateByteUnreflected(int crc, int b) {
        crc ^= b << 24;
        for (int i = 0; i < 8; i++) {
            crc = (crc << 1) ^ (poly & -(crc >>> 31));
        }
        return crc;
    }
}
