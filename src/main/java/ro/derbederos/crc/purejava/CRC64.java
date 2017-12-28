package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRC;
import ro.derbederos.crc.CRCModel;

import static java.lang.Long.reverse;
import static ro.derbederos.crc.purejava.CRC64Util.initLookupTablesReflected;
import static ro.derbederos.crc.purejava.CRC64Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 64 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC64 implements CRC {

    protected final CRCModel crcModel;
    protected final GfUtil gfUtil;
    protected final long[][] lookupTables;
    protected final int width;
    protected final long poly;
    protected final long init;
    protected final boolean refIn; // reflect input data bytes
    protected final boolean refOut; // resulted sum needs to be reversed before xor
    protected long crc;

    public CRC64(CRCModel crcModel) {
        this(crcModel, 1);
    }

    CRC64(CRCModel crcModel, int lookupTablesCount) {
        this.crcModel = crcModel;
        this.width = crcModel.getWidth();
        this.refIn = crcModel.getRefIn();
        this.refOut = crcModel.getRefOut();
        long poly = crcModel.getPoly() << 64 - width;
        long init = crcModel.getInit() << 64 - width;
        GfUtil gfUtil = new GfUtil64Reflected(crcModel);
        if (!refOut) {
            gfUtil = new GfUtilUnreflected(gfUtil, width);
        }
        this.gfUtil = gfUtil;
        if (this.refIn) {
            this.poly = reverse(poly);
            this.init = reverse(init);
            this.lookupTables = initLookupTablesReflected(this.poly, lookupTablesCount);
        } else {
            this.poly = poly;
            this.init = init;
            this.lookupTables = initLookupTablesUnreflected(this.poly, lookupTablesCount);
        }
        reset();
    }

    @Override
    public CRCModel getCRCModel() {
        return crcModel;
    }

    @Override
    public void reset() {
        crc = init;
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = updateByteReflected(crc, b & 0xFF);
        } else {
            crc = updateByteUnreflected(crc, b & 0xFF);
        }
    }

    protected long updateByteReflected(long crc, int b) {
        return (crc >>> 8) ^ lookupTables[0][((int) crc ^ b) & 0xFF];
    }

    protected long updateByteUnreflected(long crc, int b) {
        return (crc << 8) ^ lookupTables[0][(int) (crc >>> 56) ^ b];
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(crc, src, offset, len);
        } else {
            crc = updateUnreflected(crc, src, offset, len);
        }
    }

    private long updateReflected(long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = updateByteReflected(localCrc, Byte.toUnsignedInt(src[i]));
        }
        return localCrc;
    }

    private long updateUnreflected(long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = updateByteUnreflected(localCrc, Byte.toUnsignedInt(src[i]));
        }
        return localCrc;
    }

    @Override
    public void updateBits(long b, int bits) {
        if (refIn) {
            long mask = 0xFFFFFFFFFFFFFFFFL >>> 64 - bits;
            crc ^= b & mask;
            for (int i = 0; i < bits; i++) {
                crc = (crc >>> 1) ^ (poly & -(crc & 1));
            }
        } else {
            crc ^= b << 64 - bits;
            for (int i = 0; i < bits; i++) {
                crc = (crc << 1) ^ (poly & -(crc >>> 63));
            }
        }
    }

    @Override
    public long getValue() {
        long result = crc;
        //reflect output when necessary
        if (refOut != refIn) {
            result = reverse(result);
        }
        if (!refOut) {
            result >>>= 64 - width;
        }
        result = result ^ crcModel.getXorOut();
        return result;
    }

    @Override
    public void setValue(long crc) {
        long result = crc ^ crcModel.getXorOut();
        if (!refOut) {
            result <<= 64 - width;
        }
        //reflect output when necessary
        if (refOut != refIn) {
            result = reverse(result);
        }
        this.crc = result;
    }

    @Override
    public long getCrcOfCrc() {
        return gfUtil.getCrcOfCrc() ^ crcModel.getXorOut();
    }

    @Override
    public long concatenate(long crcA, long crcB, long bytesB) {
        return gfUtil.concatenate(crcA, crcB, bytesB);
    }

    @Override
    public long concatenateZeroes(long crcA, long bytesB) {
        return gfUtil.crcOfZeroes(bytesB, crcA);
    }
}
