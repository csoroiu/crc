package ro.derbederos.crc;

final class CRC16Util {
    static short[] fastInitLookupTableReflected(short poly) {
        short reflectedPoly = reverseShort(poly);
        short[] lookupTable = new short[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = reflectedPoly;
        int v = reflectedPoly & 0xFFFF;
        for (int i = 64; i != 0; i /= 2) {
            v = (v >> 1) ^ (reflectedPoly & ~((v & 1) - 1));
            v = v & 0xFFFF;
            lookupTable[i] = (short) (v);
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = (short) (lookupTable[i] ^ lookupTable[j]);
            }
        }
        return lookupTable;
    }

    static short[] fastInitLookupTableUnreflected(short poly) {
        short[] lookupTable = new short[0x100];
        lookupTable[0] = 0;
        lookupTable[1] = poly;
        short v = poly;
        for (int i = 2; i <= 128; i *= 2) {
            v = (short) ((v << 1) ^ (poly & ~(((v & Integer.MIN_VALUE) >>> 31) - 1)));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 256; i *= 2) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = (short) (lookupTable[i] ^ lookupTable[j]);
            }
        }
        return lookupTable;
    }

    static short[] initLookupTableReflected(short poly) {
        short reflectedPoly = reverseShort(poly);
        short[] lookupTable = new short[0x100];
        for (int i = 0; i < 0x100; i++) {
            short v = (short) i;
            for (int j = 0; j < 8; j++) {
                if ((v & 1) == 1) {
                    v = (short) (((v & 0xFFFF) >>> 1) ^ reflectedPoly);
                } else {
                    v = (short) ((v & 0xFFFF) >>> 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static short[] initLookupTableUnreflected(short poly) {
        short[] lookupTable = new short[0x100];
        for (int i = 0; i < 0x100; i++) {
            short v = (short) (i << 8);
            for (int j = 0; j < 8; j++) {
                if ((v & Short.MIN_VALUE) != 0) {
                    v = (short) ((v << 1) ^ poly);
                } else {
                    v = (short) (v << 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static short reverseShort(int i) {
        return (short) (Integer.reverse(i) >>> 16);
    }
}
