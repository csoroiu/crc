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

package ro.derbederos.crc;

import java.util.zip.Checksum;

public interface CRC extends Checksum {

    default void update(byte[] b) {
        update(b, 0, b.length);
    }

    CRCModel getCRCModel();

    void updateBits(long b, int bits);

    default void updateBits(byte[] b, int off, int bits) {
        int len = bits >>> 3;
        update(b, off, len);
        if ((bits & 0x7) != 0) {
            updateBits(b[len], bits - (len << 3));
        }
    }

    void setValue(long crc);

    long getCrcOfCrc();

    long concatenate(long crcA, long crcB, long bytesB);

    default void append(long crcB, long bytesB) {
        setValue(concatenate(getValue(), crcB, bytesB));
    }

    long concatenateZeroes(long crcA, long bytesB);

    default void appendZeroes(long bytesB) {
        setValue(concatenateZeroes(getValue(), bytesB));
    }
}
