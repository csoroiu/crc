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

import ro.derbederos.crc.CRCModel;

/**
 * Byte-wise CRC implementation that can compute CRC with width &lt;= 32 using different models.
 * It uses slicing-by-16 method (16 tables of 256 elements each).
 * We use the algorithm described by Michael E. Kounavis and Frank L. Berry in
 * "A Systematic Approach to Building High Performance, Software-based, CRC Generators",
 * Intel Research and Development, 2005
 */
public class CRC32SlicingBy16 extends CRC32 {

    public CRC32SlicingBy16(CRCModel crcModel) {
        super(crcModel, 16);
    }

    @Override
    public void update(byte[] src, int offset, int len) {
        if (refIn) {
            crc = updateReflected(lookupTables, crc, src, offset, len);
        } else {
            crc = updateUnreflected(lookupTables, crc, src, offset, len);
        }
    }

    private static int updateReflected(int[][] lookupTables, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 15) {
            localCrc = lookupTables[15][(localCrc ^ src[index++]) & 0xFF] ^
                    lookupTables[14][((localCrc >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[13][((localCrc >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[12][((localCrc >>> 24) ^ src[index++]) & 0xFF] ^
                    lookupTables[11][src[index++] & 0xFF] ^
                    lookupTables[10][src[index++] & 0xFF] ^
                    lookupTables[9][src[index++] & 0xFF] ^
                    lookupTables[8][src[index++] & 0xFF] ^
                    lookupTables[7][src[index++] & 0xFF] ^
                    lookupTables[6][src[index++] & 0xFF] ^
                    lookupTables[5][src[index++] & 0xFF] ^
                    lookupTables[4][src[index++] & 0xFF] ^
                    lookupTables[3][src[index++] & 0xFF] ^
                    lookupTables[2][src[index++] & 0xFF] ^
                    lookupTables[1][src[index++] & 0xFF] ^
                    lookupTables[0][src[index++] & 0xFF];
            len -= 16;
        }
        while (len > 0) {
            localCrc = (localCrc >>> 8) ^ lookupTables[0][(localCrc ^ src[index++]) & 0xFF];
            len--;
        }
        return localCrc;
    }

    private static int updateUnreflected(int[][] lookupTables, int crc, byte[] src, int offset, int len) {
        int localCrc = crc;
        int index = offset;
        while (len > 15) {
            localCrc = lookupTables[15][((localCrc >>> 24) ^ src[index++]) & 0xFF] ^
                    lookupTables[14][((localCrc >>> 16) ^ src[index++]) & 0xFF] ^
                    lookupTables[13][((localCrc >>> 8) ^ src[index++]) & 0xFF] ^
                    lookupTables[12][(localCrc ^ src[index++]) & 0xFF] ^
                    lookupTables[11][src[index++] & 0xFF] ^
                    lookupTables[10][src[index++] & 0xFF] ^
                    lookupTables[9][src[index++] & 0xFF] ^
                    lookupTables[8][src[index++] & 0xFF] ^
                    lookupTables[7][src[index++] & 0xFF] ^
                    lookupTables[6][src[index++] & 0xFF] ^
                    lookupTables[5][src[index++] & 0xFF] ^
                    lookupTables[4][src[index++] & 0xFF] ^
                    lookupTables[3][src[index++] & 0xFF] ^
                    lookupTables[2][src[index++] & 0xFF] ^
                    lookupTables[1][src[index++] & 0xFF] ^
                    lookupTables[0][src[index++] & 0xFF];
            len -= 16;
        }
        while (len > 0) {
            localCrc = (localCrc << 8) ^ lookupTables[0][((localCrc >>> 24) ^ src[index++]) & 0xFF];
            len--;
        }
        return localCrc;
    }
}
