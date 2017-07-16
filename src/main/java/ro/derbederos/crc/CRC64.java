package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 using different models.
 */
public class CRC64 implements Checksum {

    final private long lookupTable[] = new long[0x100];
    final private long poly;
    final private long init;
    final private boolean refIn; // reflect input data bytes
    final private boolean refOut; // resulted sum needs to be reversed before xor
    final private long xorOut;
    private long crc;

    public CRC64(long poly, long init, boolean refIn, boolean refOut, long xorOut) {
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
        long poly = Long.reverse(this.poly);
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
        long poly = this.poly;
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
            crc = Long.reverse(init);
        } else {
            crc = init;
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
        if (refIn) {
            updateReflected(src, offset, len);
        } else {
            updateUnreflected(src, offset, len);
        }
    }

    private void updateReflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ value) & 0xff)];
        }
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        for (int i = offset; i < offset + len; i++) {
            int value = src[i];
            crc = (crc << 8) ^ lookupTable[(int) (((crc >>> 56) ^ value) & 0xff)];
        }
    }

    public long getValue() {
        if (refOut == refIn) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
