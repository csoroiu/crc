package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.initLookupTablesReflected;

/*
 * http://en.wikipedia.org/wiki/Cyclic_redundancy_check
 * http://reveng.sourceforge.net/crc-catalogue/
 * http://zlib.net/crc_v3.txt
 * http://create.stephan-brumme.com/crc32/
 */

/**
 * Byte-wise CRC implementation that can compute CRC-64 for little endian (reflected) byte input using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 */
public class CRC64ReflectedSlicingBy16 implements Checksum {

    private final long[][] lookupTable;
    final long poly;
    final long init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64ReflectedSlicingBy16(long poly, long init, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        lookupTable = initLookupTablesReflected(poly, 16);
        reset();
    }

    public void reset() {
        crc = Long.reverse(init);
    }

    public void update(int b) {
        crc = (crc >>> 8) ^ lookupTable[0][(int) ((crc ^ b) & 0xff)];
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    public void update(byte[] src, int offset, int len) {
        crc = updateReflected(lookupTable, crc, src, offset, len);
    }

    private static long updateReflected(long[][] lookupTable, long crc, byte[] src, int offset, int len) {
        long localCrc = crc;
        int index = offset;
        while (len > 15) {
            localCrc = lookupTable[15][(int) ((localCrc ^ src[index++]) & 0xff)] ^
                    lookupTable[14][(int) (((localCrc >>> 8) ^ src[index++]) & 0xff)] ^
                    lookupTable[13][(int) (((localCrc >>> 16) ^ src[index++]) & 0xff)] ^
                    lookupTable[12][(int) (((localCrc >>> 24) ^ src[index++]) & 0xff)] ^
                    lookupTable[11][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)] ^
                    lookupTable[10][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                    lookupTable[9][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                    lookupTable[8][(int) ((localCrc >>> 56) ^ src[index++]) & 0xff] ^
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
            localCrc = (localCrc >>> 8) ^ lookupTable[0][(int) ((localCrc ^ src[index++]) & 0xff)];
            len--;
        }
        return localCrc;
    }

    public long getValue() {
        if (refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
