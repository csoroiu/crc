package ro.derbederos.crc;

final class CRC64Util {
    static long[] fastInitLookupTableReflected(long poly) {
        long reflectedPoly = Long.reverse(poly);
        long[] lookupTable = new long[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = reflectedPoly;
        long v = reflectedPoly;
        for (int i = 64; i != 0; i /= 2) {
            v = (v >>> 1) ^ (reflectedPoly & ~((v & 1) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static long[] fastInitLookupTableUnreflected(long poly) {
        long[] lookupTable = new long[0x100];
        lookupTable[0] = 0;
        lookupTable[1] = poly;
        long v = poly;
        for (int i = 2; i <= 128; i *= 2) {
            v = (v << 1) ^ (poly & ~(((v & Long.MIN_VALUE) >>> 63) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static long[] initLookupTableReflected(long poly) {
        long reflectedPoly = Long.reverse(poly);
        long[] lookupTable = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long v = i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (v >>> 1) ^ reflectedPoly;
                } else {
                    v = (v >>> 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static long[] initLookupTableUnreflected(long poly) {
        long[] lookupTable = new long[0x100];
        for (int i = 0; i < 0x100; i++) {
            long v = ((long) i) << 56;
            for (int j = 0; j < 8; j++) {
                if ((v & Long.MIN_VALUE) != 0) {
                    v = (v << 1) ^ poly;
                } else {
                    v = (v << 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static long[][] initLookupTablesReflected(long poly, int dimension) {
        long[][] lookupTable = new long[dimension][0x100];
        lookupTable[0] = CRC64Util.fastInitLookupTableReflected(poly);
        for (int n = 0; n < 256; n++) {
            long v = lookupTable[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTable[0][(int) (v & 0xff)] ^ (v >>> 8);
                lookupTable[k][n] = v;
            }
        }
        return lookupTable;
    }

    static long[][] initLookupTablesUnreflected(long poly, int dimension) {
        long[][] lookupTable = new long[dimension][0x100];
        lookupTable[0] = CRC64Util.fastInitLookupTableUnreflected(poly);
        for (int n = 0; n < 256; n++) {
            long v = lookupTable[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTable[0][(int) ((v >>> 56) & 0xff)] ^ (v << 8);
                lookupTable[k][n] = v;
            }
        }
        return lookupTable;
    }
}
