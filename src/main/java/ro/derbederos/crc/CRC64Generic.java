package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 */
public class CRC64Generic implements Checksum {

    final private long lookupTable[] = new long[0x100];
    final private int width;
    final private long poly;
    final private long initialValue;
    final private boolean refIn; // reflect input data bytes
    final private boolean refOut; // resulted sum needs to be reversed before xor
    final private long xorOut;
    private long crc;

    public CRC64Generic(int width, long poly, long initialValue,
                        boolean refIn, boolean refOut, long xorOut) {
        this.width = width;
        this.poly = poly;
        this.initialValue = initialValue;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            initLookupTableReflected();
        } else {
            initLookupTableUnreflected();
        }
        reset();
    }

    private void initLookupTableReflected() {
        long poly = Long.reverse(this.poly << 64 - width);
        for (int i = 0; i < 0x100; i++) {
            long v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ poly;
                } else {
                    v = (v >>> 1);
                }
            }
            lookupTable[i] = v;
        }
    }

    private void initLookupTableUnreflected() {
        long poly = this.poly << 64 - width;
        for (int i = 0; i < 0x100; i++) {
            long v = ((long) i) << 56;
            for (int j = 0; j < 8; j++) {
                if ((v & 0x8000000000000000L) != 0) {
                    v = (v << 1) ^ poly;
                } else {
                    v = (v << 1);
                }
            }
            lookupTable[i] = v;
        }
    }

    public void reset() {
        if (refIn) {
            crc = Long.reverse(initialValue << 64 - width);
        } else {
            crc = initialValue << 64 - width;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ b) & 0xff)];
        } else {
            crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ b) & 0xff)];
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
        long xorOut = this.xorOut;
        if (!refOut) {
            xorOut <<= 64 - width;
        }

        long result;
        if (refOut == refIn) {
            result = crc ^ xorOut;
        } else {
            result = Long.reverse(crc) ^ xorOut;
        }

        if (!refOut) {
            result >>>= 64 - width;
        }
        return result;
    }
}
