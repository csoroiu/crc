/*
 * Copyright (c) 2017-2018 Claudiu Soroiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.derbederos.crc.purejava;

/**
 * This class contains an optimization over GF functions and crc initialization.
 * https://encode.ru/threads/1698-Fast-CRC-table-construction-and-rolling-CRC-hash-calculation
 */
final class CRC32Util {
    static int[] fastInitLookupTableReflected(int poly) {
        int[] lookupTable = new int[0x100];
        lookupTable[0] = 0;
        lookupTable[0x80] = poly;
        int v = poly;
        for (int i = 64; i != 0; i >>>= 1) {
            v = (v >>> 1) ^ (poly & -(v & 1));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 0x100; i <<= 1) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static int[] fastInitLookupTableUnreflected(int poly) {
        int[] lookupTable = new int[0x100];
        lookupTable[0] = 0;
        lookupTable[1] = poly;
        int v = poly;
        for (int i = 2; i <= 0x80; i <<= 1) {
            v = (v << 1) ^ (poly & -(v >>> 31));
            lookupTable[i] = v;
        }
        for (int i = 2; i < 0x100; i <<= 1) {
            for (int j = 1; j < i; j++) {
                lookupTable[i + j] = lookupTable[i] ^ lookupTable[j];
            }
        }
        return lookupTable;
    }

    static int[] initLookupTableReflected(int poly) {
        int[] lookupTable = new int[0x100];
        for (int i = 0; i < 0x100; i++) {
            int v = i;
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

    static int[] initLookupTableUnreflected(int poly) {
        int[] lookupTable = new int[0x100];
        for (int i = 0; i < 0x100; i++) {
            int v = i << 24;
            for (int j = 0; j < 8; j++) {
                if ((v & Integer.MIN_VALUE) != 0) {
                    v = (v << 1) ^ poly;
                } else {
                    v = (v << 1);
                }
            }
            lookupTable[i] = v;
        }
        return lookupTable;
    }

    static int[][] initLookupTablesReflected(int poly, int dimension) {
        int[][] lookupTables = new int[dimension][0x100];
        if (dimension == 0) {
            return lookupTables;
        }
        lookupTables[0] = fastInitLookupTableReflected(poly);
        for (int n = 0; n < 0x100; n++) {
            int v = lookupTables[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTables[0][v & 0xFF] ^ (v >>> 8);
                lookupTables[k][n] = v;
            }
        }
        return lookupTables;
    }

    static int[][] initLookupTablesUnreflected(int poly, int dimension) {
        int[][] lookupTables = new int[dimension][0x100];
        if (dimension == 0) {
            return lookupTables;
        }
        lookupTables[0] = fastInitLookupTableUnreflected(poly);
        for (int n = 0; n < 0x100; n++) {
            int v = lookupTables[0][n];
            for (int k = 1; k < dimension; k++) {
                v = lookupTables[0][(v >>> 24) & 0xFF] ^ (v << 8);
                lookupTables[k][n] = v;
            }
        }
        return lookupTables;
    }
}
