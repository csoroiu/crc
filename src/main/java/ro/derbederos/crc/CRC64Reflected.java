package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 */
public class CRC64Reflected implements Checksum {

    final private long lookupTable[] = new long[0x100];
    final private long poly;
    final private long initialValue;
    final private boolean refOut; // resulted sum needs to be reversed before xor
    final private long xorOut;
    private long crc;

    public CRC64Reflected(long poly, long initialValue,
                          boolean refOut, long xorOut) {
        this.poly = poly;
        this.initialValue = initialValue;
        this.refOut = refOut;
        this.xorOut = xorOut;
        initLookupTableReflected();
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

    public void reset() {
        crc = Long.reverse(initialValue);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[(int) ((crc ^ b) & 0xff)];
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
        if (refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
