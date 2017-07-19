package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.CRC64Util.initLookupTablesUnreflected;

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
public class CRC64UnreflectedSlicingBy8 implements Checksum {

    private final long[][] lookupTable;
    final long poly;
    final long init;
    final boolean refOut; // resulted sum needs to be reversed before xor
    final long xorOut;
    private long crc;

    public CRC64UnreflectedSlicingBy8(long poly, long init, boolean refOut, long xorOut) {
        this.poly = poly;
        this.init = init;
        this.refOut = refOut;
        this.xorOut = xorOut;
        lookupTable = initLookupTablesUnreflected(poly, 8);
        reset();
    }

    public void reset() {
        crc = init;
    }

    public void update(int b) {
        crc = (crc << 8) ^ lookupTable[0][(int) (((crc >>> 56) ^ b) & 0xff)];
    }

    public void update(byte[] src) {
        update(src, 0, src.length);
    }

    public void update(byte[] src, int offset, int len) {
        updateUnreflected(src, offset, len);
    }

    private void updateUnreflected(byte[] src, int offset, int len) {
        long localCrc = this.crc;
        int index = offset;
        while (len > 7) {
            localCrc = lookupTable[7][(int) ((localCrc >>> 56) ^ src[index++]) & 0xff] ^
                    lookupTable[6][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                    lookupTable[5][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                    lookupTable[4][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)] ^
                    lookupTable[3][(int) (((localCrc >>> 24) ^ src[index++]) & 0xff)] ^
                    lookupTable[2][(int) (((localCrc >>> 16) ^ src[index++]) & 0xff)] ^
                    lookupTable[1][(int) (((localCrc >>> 8) ^ src[index++]) & 0xff)] ^
                    lookupTable[0][(int) ((localCrc ^ src[index++]) & 0xff)];
            len -= 8;
        }
        switch (len) {
            case 7:
            case 6:
            case 5:
            case 4:
                localCrc = (localCrc << 32) ^
                        lookupTable[3][(int) (((localCrc >>> 56) ^ src[index++]) & 0xff)] ^
                        lookupTable[2][(int) (((localCrc >>> 48) ^ src[index++]) & 0xff)] ^
                        lookupTable[1][(int) (((localCrc >>> 40) ^ src[index++]) & 0xff)] ^
                        lookupTable[0][(int) (((localCrc >>> 32) ^ src[index++]) & 0xff)];
                len -= 4;
        }
        switch (len) {
            case 3:
                localCrc = (localCrc << 8) ^ lookupTable[0][(int) (((localCrc >>> 56) ^ src[index++]) & 0xff)];
            case 2:
                localCrc = (localCrc << 8) ^ lookupTable[0][(int) (((localCrc >>> 56) ^ src[index++]) & 0xff)];
            case 1:
                localCrc = (localCrc << 8) ^ lookupTable[0][(int) (((localCrc >>> 56) ^ src[index]) & 0xff)];
        }
        this.crc = localCrc;
    }

    public long getValue() {
        if (!refOut) {
            return crc ^ xorOut;
        } else {
            return Long.reverse(crc) ^ xorOut;
        }
    }
}
