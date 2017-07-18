package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 for little endian (reflected) byte input using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 */
public class CRC64ReflectedSlicingBy8 implements Checksum {

    private final long lookupTable[][] = new long[8][0x100];
    final long poly;
    final long init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64ReflectedSlicingBy8(long poly, long init, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        initLookupTableReflected();
        reset();
    }

    private void initLookupTableReflected() {
        long poly = Long.reverse(this.poly);
        for (int n = 0; n < 256; n++) {
            long v = n;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ poly;
                } else {
                    v = (v >>> 1);
                }
            }
            lookupTable[0][n] = v;
        }
        for (int n = 0; n < 256; n++) {
            long v = lookupTable[0][n];
            for (int k = 1; k < 8; k++) {
                v = lookupTable[0][(int) (v & 0xff)] ^ (v >>> 8);
                lookupTable[k][n] = v;
            }
        }
    }

    public void reset() {
        crc = Long.reverse(init);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[0][(int) ((crc ^ b) & 0xff)];
    }

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        updateReflected(src, offset, len);
    }

    private void updateReflected(byte[] src, int offset, int len) {
        long localCrc = this.crc;
        int index = offset;
        while (len >= 8) {
            localCrc = lookupTable[7][(int) ((localCrc ^ src[index++]) & 0xff)] ^
                    lookupTable[6][(int) (((localCrc >>> 8) ^ src[index++]) & 0xff)] ^
                    lookupTable[5][(int) (((localCrc >>> 16) ^ src[index++]) & 0xff)] ^
                    lookupTable[4][(int) (((localCrc >>> 24) ^ src[index++]) & 0xff)] ^
                    lookupTable[3][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)] ^
                    lookupTable[2][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                    lookupTable[1][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                    lookupTable[0][(int) ((localCrc >>> 56) ^ src[index++]) & 0xff];
            len -= 8;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTable[0][(int) ((localCrc ^ src[index++]) & 0xff)];
            len--;
        }
        this.crc = localCrc;
    }

    public long getValue() {
        if (refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
