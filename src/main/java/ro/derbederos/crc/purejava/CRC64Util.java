package ro.derbederos.crc.purejava;

/**
 * This class contains an optimization over GF functions and crc initialization.
 * https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation
 */
final class CRC64Util {
    static long[] fastInitLookupTableReflected(long poly) {
        long[] lookupTable = new long[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = poly;
        long v = poly;
        for (int i = 64; i != 0; i >>>= 1) {
            v = (v >>> 1) ^ (poly & ~((v & 1) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i <<= 1) {
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
        for (int i = 2; i <= 0x80; i <<= 1) {
            v = (v << 1) ^ (poly & ~((v >>> 63) - 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 0x100; i <<= 1) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static long[] initLookupTableReflected(long poly) {
        long[] lookupTable = new long[0x100];
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
        long[][] lookupTables = new long[dimension][0x100];
        if (dimension == 0) {
            return lookupTables;
        }
        lookupTables[0] = fastInitLookupTableReflected(poly);
        for (int n = 0; n < 0x100; n++) {
            long v = lookupTables[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTables[0][((int) v & 0xff)] ^ (v >>> 8);
                lookupTables[k][n] = v;
            }
        }
        return lookupTables;
    }

    static long[][] initLookupTablesUnreflected(long poly, int dimension) {
        long[][] lookupTables = new long[dimension][0x100];
        if (dimension == 0) {
            return lookupTables;
        }
        lookupTables[0] = fastInitLookupTableUnreflected(poly);
        for (int n = 0; n < 0x100; n++) {
            long v = lookupTables[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTables[0][((int) (v >>> 56) & 0xff)] ^ (v << 8);
                lookupTables[k][n] = v;
            }
        }
        return lookupTables;
    }
}
