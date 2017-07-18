package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.Util.reverseShort;

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

    private final short lookupTable[];
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
            lookupTable = fastInitLookupTableReflected(reverseShort(poly));
        } else {
            lookupTable = fastInitLookupTableUnreflected((short) poly);
        }
        reset();
    }

    protected static short[] fastInitLookupTableReflected(short poly) {
        short lookupTable[] = new short[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = poly;
        int v = poly & 0xFFFF;
        for (int i = 64; i != 0; i /= 2) {
            v = (v >> 1) ^ (poly & ~((v & 1) - 1));
            v = v & 0xFFFF;
            lookupTable[i] = (short) (v);
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = (short) (lookupTable[i] ^ lookupTable[j]);
            }
        }
        return lookupTable;
    }

    protected static short[] fastInitLookupTableUnreflected(short poly) {
        short lookupTable[] = new short[0x100];
        lookupTable[0] = 0;
        lookupTable[1] = poly;
        short v = poly;
        for (int i = 2; i <= 128; i *= 2) {
            v = (short) ((v << 1) ^ (poly & ~(((v & Integer.MIN_VALUE) >>> 31) - 1)));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = (short) (lookupTable[i] ^ lookupTable[j]);
            }
        }
        return lookupTable;
    }

    protected static short[] initLookupTableReflected(short poly) {
        short lookupTable[] = new short[0x100];
        for (int i = 0; i < 0x100; i++) {
            short v = (short) i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (short) (((v & 0xFFFF) >>> 1) ^ poly);
                } else {
                    v = (short) ((v & 0xFFFF) >>> 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    protected static short[] initLookupTableUnreflected(short poly) {
        short lookupTable[] = new short[0x100];
        for (int i = 0; i < 0x100; i++) {
            short v = (short) (i << 8);
            for (int j = 0; j < 8; j++) {
                if ((v & Short.MIN_VALUE) != 0) {
                    v = (short) ((v << 1) ^ poly);
                } else {
                    v = (short) (v << 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
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

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        if (refIn) {
            updateReflected(src, offset, len);
        } else {
            updateUnreflected(src, offset, len);
        }
    }

    private void updateReflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (short) ((((int) crc & 0xFFFF) >>> 8) ^ lookupTable[((crc ^ value) & 0xff)]);
        }
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (short) ((((int) crc & 0xFFFF) << 8) ^ lookupTable[((crc >>> 8) ^ value) & 0xff]);
        }
    }

    public long getValue() {
        if (refOut == refIn) {
            return (crc ^ xorOut) & 0xFFFFL;
        } else {
            return (reverseShort(crc) ^ xorOut) & 0xFFFFL;
        }
    }
}