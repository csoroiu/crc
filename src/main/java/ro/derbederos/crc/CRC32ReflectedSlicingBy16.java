package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC32Util.initLookupTablesReflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-32 for little endian (reflected) byte input using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 */
public class CRC32ReflectedSlicingBy16 implements Checksum {

    private final int[][] lookupTable;
    final int poly;
    final int init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final int xorOut;
    private int crc;

    public CRC32ReflectedSlicingBy16(int poly, int init, boolean refOut, int xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        lookupTable = initLookupTablesReflected(poly, 16);
        reset();
    }

    public void reset() {
        crc = Integer.reverse(init);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[0][(crc ^ b) & 0xff];
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    public void update(byte[] src, int offset, int len) {
        updateReflected(src, offset, len);
    }

    private void updateReflected(byte[] src, int offset, int len) {
        int localCrc = this.crc;
        int index = offset;
        while (len > 15) {
            localCrc = lookupTable[15][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTable[14][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[13][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[12][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[11][src[index++] & 0xff] ^
                    lookupTable[10][src[index++] & 0xff] ^
                    lookupTable[9][src[index++] & 0xff] ^
                    lookupTable[8][src[index++] & 0xff] ^
                    lookupTable[7][src[index++] & 0xff] ^
                    lookupTable[6][src[index++] & 0xff] ^
                    lookupTable[5][src[index++] & 0xff] ^
                    lookupTable[4][src[index++] & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 16;
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
