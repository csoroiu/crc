package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC16Util.fastInitLookupTableReflected;
import static ro.derbederos.crc.CRC16Util.fastInitLookupTableUnreflected;
import static ro.derbederos.crc.CRC16Util.reverseShort;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 * https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation
 */

/**
 * Byte-wise CRC implementation that can compute CRC-16 using different models.
 */
public class CRC16 implements Checksum {

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

    public void reset() {
        if (refIn) {
            crc = reverseShort(init);
        } else {
            crc = init;
        }
    }

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

    public long getValue() {
        if (refOut == refIn) {
            return (crc ^ xorOut) & 0xFFFFL;
        } else {
            return (reverseShort(crc) ^ xorOut) & 0xFFFFL;
        }
    }
}
