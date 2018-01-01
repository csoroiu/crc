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
 * It is an implementation of improved branch approach from http://create.stephan-brumme.com/crc32/#fastest-bitwise-crc32.
 */
public class CRC32Branchfree extends CRC32 {

    public CRC32Branchfree(CRCModel crcModel) {
        super(crcModel, 0);
    }

    @Override
    protected int updateByteReflected(int crc, int b) {
        crc ^= b;
        for (int i = 0; i < 8; i++) {
            crc = (crc >>> 1) ^ (poly & -(crc & 1));
        }
        return crc;
    }

    @Override
    protected int updateByteUnreflected(int crc, int b) {
        crc ^= b << 24;
        for (int i = 0; i < 8; i++) {
            crc = (crc << 1) ^ (poly & -(crc >>> 31));
        }
        return crc;
    }
}
