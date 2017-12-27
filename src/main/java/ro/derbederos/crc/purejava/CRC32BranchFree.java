package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRCModel;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 32 using different models.
 * It is an implementation of improved branch approach from http://create.stephan-brumme.com/crc32/#fastest-bitwise-crc32.
 */
public class CRC32BranchFree extends CRC32 {

    public CRC32BranchFree(CRCModel crcModel) {
        super(crcModel, 0);
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = updateByteReflected(crc, b);
        } else {
            crc = updateByteUnreflected(crc, b);
        }
    }

    protected int updateByteReflected(int crc, int b) {
        crc ^= b & 0xFF;
        for (int i = 0; i < 8; i++) {
            crc = (crc >>> 1) ^ (poly & -(crc & 1));
        }
        return crc;
    }

    protected int updateByteUnreflected(int crc, int b) {
        crc ^= b << 24;
        for (int i = 0; i < 8; i++) {
            crc = (crc << 1) ^ (poly & -(crc >>> 31));
        }
        return crc;
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(crc, src, offset, len);
        } else {
            crc = updateUnreflected(crc, src, offset, len);
        }
    }

    private int updateReflected(int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = updateByteReflected(localCrc, src[i]);
        }
        return localCrc;
    }

    private int updateUnreflected(int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = updateByteUnreflected(localCrc, src[i]);
        }
        return localCrc;
    }
}
