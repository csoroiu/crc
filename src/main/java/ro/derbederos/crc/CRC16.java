package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 */
public class CRC16 implements Checksum {

    final private short lookupTable[] = new short[0x100];
    final private short poly;
    final private short initialValue;
    final private boolean refIn; // reflect input data bytes
    final private boolean refOut; // resulted sum needs to be reversed before xor
    final private short xorOut;
    private short crc;

    public CRC16(int poly, int initialValue,
                 boolean refIn, boolean refOut, int xorOut) {
        this.poly = (short) poly;
        this.initialValue = (short) initialValue;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = (short) xorOut;
        if (refIn) {
            initLookupTableReflected();
        } else {
            initLookupTableUnreflected();
        }
    }

    private void initLookupTableReflected() {
        short poly = reverseShort(this.poly);
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
    }

    private void initLookupTableUnreflected() {
        short poly = this.poly;
        for (int i = 0; i < 0x100; i++) {
            short v = (short) (i << 8);
            for (int j = 0; j < 8; j++) {
                if ((v & 0x8000000000000000L) != 0) {
                    v = (short) ((v << 1) ^ poly);
                } else {
                    v = (short) (v << 1);
                }
            }
            lookupTable[i] = v;
        }
    }

    public void reset() {
        if (refIn) {
            crc = reverseShort(initialValue);
        } else {
            crc = initialValue;
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
        for (int i = offset; i < offset + len; i++) {
            byte value = src[i];
            update(value);
        }
    }

    public long getValue() {
        if (refOut == refIn) {
            return (crc ^ xorOut) & 0xFFFFL;
        } else {
            return (reverseShort(crc) ^ xorOut) & 0xFFFFL;
        }
    }

    private static short reverseShort(int i) {
        return (short) (Integer.reverse(i) >>> 16);
    }
}
