package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRC;
import ro.derbederos.crc.CRCModel;

import static java.lang.Integer.reverse;
import static java.lang.Integer.toUnsignedLong;
import static ro.derbederos.crc.purejava.CRC32Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.purejava.CRC32Util.fastInitLookupTableUnreflected;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 32 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC32 implements CRC {

    protected final CRCModel crcModel;
    protected final GfUtil gfUtil;
    protected final int[] lookupTable;
    protected final int width;
    protected final int poly;
    protected final int init;
    protected final boolean refIn; // reflect input data bytes
    protected final boolean refOut; // resulted sum needs to be reversed before xor
    protected int crc;

    public CRC32(CRCModel crcModel) {
        this.crcModel = crcModel;
        this.gfUtil = new GfUtil32(crcModel);
        this.width = crcModel.getWidth();
        this.refIn = crcModel.getRefIn();
        this.refOut = crcModel.getRefOut();
        int poly = (int) crcModel.getPoly() << 32 - width;
        int init = (int) crcModel.getInit() << 32 - width;
        if (this.refIn) {
            this.poly = reverse(poly);
            this.init = reverse(init);
            this.lookupTable = fastInitLookupTableReflected(this.poly);
        } else {
            this.poly = poly;
            this.init = init;
            this.lookupTable = fastInitLookupTableUnreflected(this.poly);
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
            crc = (crc >>> 8) ^ lookupTable[(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ b) & 0xff];
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

    private static int updateReflected(int[] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            localCrc = (localCrc >>> 8) ^ lookupTable[(localCrc ^ src[i]) & 0xff];
        }
        return localCrc;
    }

    private static int updateUnreflected(int[] lookupTable, int crc, byte[] src, int offset, int len) {
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
        return reflectIfNeeded(gfUtil.getCrcOfCrc()) ^ crcModel.getXorOut();
    }

    @Override
    public long concatenate(long crcA, long crcB, long bytesB) {
        return reflectIfNeeded(gfUtil.concatenate(reflectIfNeeded(crcA), reflectIfNeeded(crcB), bytesB));
    }

    @Override
    public long concatenateZeroes(long crcA, long bytesB) {
        return reflectIfNeeded(gfUtil.crcOfZeroes(bytesB, reflectIfNeeded(crcA)));
    }

    private long reflectIfNeeded(long value) {
        if (!refOut) {
            return reflect(value);
        } else {
            return value;
        }
    }

    private long reflect(long value) {
        return Long.reverse(value) >>> (64 - width);
    }

}
