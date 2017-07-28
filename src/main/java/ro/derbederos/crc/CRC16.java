package ro.derbederos.crc;

import static ro.derbederos.crc.CRC16Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.CRC16Util.fastInitLookupTableUnreflected;
import static ro.derbederos.crc.CRC16Util.reverseShort;

/**
 * Byte-wise CRC implementation that can compute CRC-16 using different models.
 * We use the algorithm described by Dilip Sarwate in "Computation of Cyclic Redundancy Checks
 * via Table Look-Up", 1988
 */
public class CRC16 implements CRC {

    private final short[] lookupTable;
    final short poly;
    final short init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final short xorOut;
    private short crc;

    public CRC16(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        this.poly = (short) poly;
        this.init = (short) init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = (short) xorOut;
        if (refIn) {
            lookupTable = fastInitLookupTableReflected((short) poly);
        } else {
            lookupTable = fastInitLookupTableUnreflected((short) poly);
        }
        reset();
    }

    @Override
    public void reset() {
        if (refIn) {
            crc = reverseShort(init);
        } else {
            crc = init;
        }
    }

    @Override
    public void update(int b) {
        if (refIn) {
            crc = (short) ((((int) crc & 0xFFFF) >>> 8) ^ lookupTable[((crc ^ b) & 0xff)]);
        } else {
            crc = (short) ((((int) crc & 0xFFFF) << 8) ^ lookupTable[((crc >>> 8) ^ b) & 0xff]);
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

    private static short updateReflected(short[] lookupTable, short crc, byte[] src, int offset, int len) {
        short localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            localCrc = (short) ((((int) localCrc & 0xFFFF) >>> 8) ^ lookupTable[((localCrc ^ value) & 0xff)]);
        }
        return localCrc;
    }

    private static short updateUnreflected(short[] lookupTable, short crc, byte[] src, int offset, int len) {
        short localCrc = crc;
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            localCrc = (short) ((((int) localCrc & 0xFFFF) << 8) ^ lookupTable[((localCrc >>> 8) ^ value) & 0xff]);
        }
        return localCrc;
    }

    @Override
    public void updateBits(int b, int bits) {
        short reflectedPoly = reverseShort(poly);
        for (int i = 0; i < bits; i++) {
            if (refIn) {
                crc = (short) (((crc & 0xffff) >>> 1) ^ (reflectedPoly & ~(((crc ^ b) & 1) - 1)));
                b >>>= 1;
            } else {
                crc = (short) ((crc << 1) ^ (poly & ~(((((crc & 0xffff) >>> 15) ^ (b >>> 7)) & 1) - 1)));
                b <<= 1;
            }
        }
    }

    @Override
    public long getValue() {
        long result = crc;
        //reflect output when necessary
        if (refOut != refIn) {
            result = reverseShort(crc);
        }
        result = (result ^ xorOut) & 0xFFFFL;
        return result;
    }
}
