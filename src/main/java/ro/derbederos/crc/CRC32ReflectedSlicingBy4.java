package ro.derbederos.crc;

import java.util.zip.Checksum;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-32 for little endian (reflected) byte input using different models.
 * It uses slicing-by-4 method (8 tables of 256 elements each).
 */
public class CRC32ReflectedSlicingBy4 implements Checksum {

    private final int lookupTable[][] = new int[4][0x100];
    final int poly;
    final int init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final int xorOut;
    private int crc;

    public CRC32ReflectedSlicingBy4(int poly, int init, boolean refOut, int xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        initLookupTableReflected();
        reset();
    }

    private void initLookupTableReflected() {
        int poly = Integer.reverse(this.poly);
        for (int n = 0; n < 256; n++) {
            int v = n;
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
            int v = lookupTable[0][n];
            for (int k = 1; k < 4; k++) {
                v = lookupTable[0][v & 0xff] ^ (v >>> 8);
                lookupTable[k][n] = v;
            }
        }
    }

    public void reset() {
        crc = Integer.reverse(init);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[0][(crc ^ b) & 0xff];
    }

    public void update(byte src[]) {
        update(src, 0, src.length);
    }

    public void update(byte src[], int offset, int len) {
        updateReflected(src, offset, len);
    }

    private void updateReflected(byte[] src, int offset, int len) {
        int localCrc = this.crc;
        int index = offset;
        while (len >= 4) {
            localCrc = lookupTable[3][(localCrc ^ src[index]) & 0xff] ^
                    lookupTable[2][(localCrc >>> 8 ^ src[index + 1]) & 0xff] ^
                    lookupTable[1][(localCrc >>> 16 ^ src[index + 2]) & 0xff] ^
                    lookupTable[0][(localCrc >>> 24 ^ src[index + 3]) & 0xff];
            index += 4;
            len -= 4;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index++]) & 0xff];
            len--;
        }
        this.crc = localCrc;
    }

    public long getValue() {
        if (refOut) {
            return ((long) (crc ^ xorOut)) & 0xFFFFFFFFL;
        } else {
            return ((long) (Integer.reverse(crc) ^ xorOut)) & 0xFFFFFFFFL;
        }
    }
}
