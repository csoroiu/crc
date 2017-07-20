package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC32Util.initLookupTablesReflected;
import static ro.derbederos.crc.CRC32Util.initLookupTablesUnreflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-32 using different models.
 * It uses slicing-by-8 method (8 tables of 256 elements each).
 */
public class CRC32SlicingBy8 implements Checksum {

    private final int[][] lookupTable;
    final int poly;
    final int init;
    final boolean refIn; // reflect input data bytes
    final boolean refOut; // resulted sum needs to be reversed before xor
    final int xorOut;
    private int crc;

    public CRC32SlicingBy8(int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        if (refIn) {
            lookupTable = initLookupTablesReflected(poly, 8);
        } else {
            lookupTable = initLookupTablesUnreflected(poly, 8);
        }
        reset();
    }

    public void reset() {
        if (refIn) {
            crc = Integer.reverse(init);
        } else {
            crc = init;
        }
    }

    public void update(int b) {
        if (refIn) {
            crc = (crc >>> 8) ^ lookupTable[0][(crc ^ b) & 0xff];
        } else {
            crc = (crc << 8) ^ lookupTable[0][((crc >>> 24) ^ b) & 0xff];
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

    private static int updateReflected(int[][] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTable[7][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTable[6][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[4][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 8;
        }
        switch (len) {
            case 7:
            case 6:
            case 5:
            case 4:
                localCrc = lookupTable[3][(localCrc ^ src[index++]) & 0xff] ^
                        lookupTable[2][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                        lookupTable[1][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                        lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
                len -= 4;
        }
        switch (len) {
            case 3:
                localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index++]) & 0xff];
            case 2:
                localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index++]) & 0xff];
            case 1:
                localCrc = (localCrc >>> 8) ^ lookupTable[0][(localCrc ^ src[index]) & 0xff];
        }
        return localCrc;
    }

    private static int updateUnreflected(int[][] lookupTable, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTable[7][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                    lookupTable[6][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                    lookupTable[5][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                    lookupTable[4][(localCrc ^ src[index++]) & 0xff] ^
                    lookupTable[3][src[index++] & 0xff] ^
                    lookupTable[2][src[index++] & 0xff] ^
                    lookupTable[1][src[index++] & 0xff] ^
                    lookupTable[0][src[index++] & 0xff];
            len -= 8;
        }
        switch (len) {
            case 7:
            case 6:
            case 5:
            case 4:
                localCrc = lookupTable[3][((localCrc >>> 24) ^ src[index++]) & 0xff] ^
                        lookupTable[2][((localCrc >>> 16) ^ src[index++]) & 0xff] ^
                        lookupTable[1][((localCrc >>> 8) ^ src[index++]) & 0xff] ^
                        lookupTable[0][(localCrc ^ src[index++]) & 0xff];
                len -= 4;
        }
        switch (len) {
            case 3:
                localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            case 2:
                localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index++]) & 0xff];
            case 1:
                localCrc = (localCrc << 8) ^ lookupTable[0][((localCrc >>> 24) ^ src[index]) & 0xff];
        }
        return localCrc;
    }

    public long getValue() {
        if (refOut == refIn) {
            return ((long) (crc ^ xorOut)) & 0xFFFFFFFFL;
        } else {
            return ((long) (Integer.reverse(crc) ^ xorOut)) & 0xFFFFFFFFL;
        }
    }
}
