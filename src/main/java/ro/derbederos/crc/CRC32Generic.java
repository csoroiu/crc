package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 */

/**
 * Byte-wise CRC implementation that can compute CRC with width <= 32 using different models.
 */
public class CRC32Generic implements Checksum {

    final private int lookupTable[] = new int[0x100];
    final private int width;
    final private int poly;
    final private int init;
    final private boolean refIn; // reflect input data bytes
    final private boolean refOut; // resulted sum needs to be reversed before xor
    final private int xorOut;
    private int crc;

    public CRC32Generic(int width, int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        this.width = width;
        this.poly = poly;
        this.init = init;
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
        int poly = Integer.reverse(this.poly << 32 - width);
        for (int i = 0; i < 0x100; i++) {
            int v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ poly;
                } else {
                    v = v >>> 1;
                }
            }
            lookupTable[i] = v;
        }
    }

    private void initLookupTableUnreflected() {
        int poly = this.poly << 32 - width;
        for (int i = 0; i < 0x100; i++) {
            int v = i << 24;
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
            crc = Integer.reverse(init << 32 - width);
        } else {
            crc = init << 32 - width;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ b) & 0xff];
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
            crc = (crc >>> 8) ^ lookupTable[(crc ^ value) & 0xff];
        }
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc << 8) ^ lookupTable[((crc >>> 24) ^ value) & 0xff];
        }
    }

    public long getValue() {
        long xorOut = this.xorOut;
        if (!refOut) {
            xorOut <<= 32 - width;
        }

        long result;
        if (refOut == refIn) {
            result = (crc ^ xorOut) & 0xFFFFFFFFL;
        } else {
            result = (Integer.reverse(crc) ^ xorOut) & 0xFFFFFFFFL;
        }

        if (!refOut) {
            result >>>= 32 - width;
        }
        return result;
    }
}
