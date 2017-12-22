package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRC;
import ro.derbederos.crc.CRCModel;

import static java.lang.Integer.reverse;
import static java.lang.Integer.toUnsignedLong;
import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.purejava.CRC32Util.initLookupTablesUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 32 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC32 implements CRC {

    protected final CRCModel crcModel;
    private final GfUtil gfUtil;
    protected final int[][] lookupTables;
    private final int width;
    private final int poly;
    private final int init;
    protected final boolean refIn; // reflect input data bytes
    private final boolean refOut; // resulted sum needs to be reversed before xor
    protected int crc;

    public CRC32(CRCModel crcModel) {
        this(crcModel, 1);
    }

    CRC32(CRCModel crcModel, int lookupTablesCount) {
        this.crcModel = crcModel;
        this.width = crcModel.getWidth();
        this.refIn = crcModel.getRefIn();
        this.refOut = crcModel.getRefOut();
        int poly = (int) crcModel.getPoly() << 32 - width;
        int init = (int) crcModel.getInit() << 32 - width;
        GfUtil gfUtil = new GfUtil32Reflected(crcModel);
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
            crc = (crc >>> 8) ^ lookupTables[0][(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTables[0][((crc >>> 24) ^ b) & 0xff];
        }
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
        int[] lookupTable = lookupTables[0];
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = (localCrc >>> 8) ^ lookupTable[(localCrc ^ src[i]) & 0xff];
        }
        return localCrc;
    }

    private static int updateUnreflected(int[][] lookupTables, int crc, byte[] src, int offset, int len) {
        int[] lookupTable = lookupTables[0];
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = (localCrc << 8) ^ lookupTable[((localCrc >>> 24) ^ src[i]) & 0xff];
        }
        return localCrc;
    }

    @Override
    public void updateBits(int b, int bits) {
        for (int i = 0; i < bits; i++) {
            if (refIn) {
                crc = (crc >>> 1) ^ (poly & ~(((crc ^ b) & 1) - 1));
                b >>>= 1;
            } else {
                crc = (crc << 1) ^ (poly & ~((((crc >>> 31) ^ (b >>> 7)) & 1) - 1));
                b <<= 1;
            }
        }
    }

    @Override
    public long getValue() {
        long result = toUnsignedLong(crc);
        //reflect output when necessary
        if (refOut != refIn) {
            result = toUnsignedLong(reverse(crc));
        }
        if (!refOut) {
            result >>>= 32 - width;
        }
        result = result ^ crcModel.getXorOut();
        return result;
    }

    @Override
    public void setValue(long crc) {
        int result = (int) (crc ^ crcModel.getXorOut());
        if (!refOut) {
            result <<= 32 - width;
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
