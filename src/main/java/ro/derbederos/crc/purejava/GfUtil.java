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
 * Andrew Kadatch's and Bob Jenkins's gf_util functions from crcutil library
 * (https://code.google.com/archive/p/crcutil/downloads).
 */
interface GfUtil {

    /**
     * Returns CRC of concatenation of blocks A and B when CRCs
     * of blocks A and B are known -- without touching the data.
     * <p>
     * To be precise, given CRC(A, |A|, startA) and CRC(B, |B|, 0),
     * returns CRC(AB, |AB|, startA).
     */
    long concatenate(long crc_A, long crc_B, long bytes_B);

    /**
     * Returns CRC of sequence of zeroes -- without touching the data.
     */
    long crcOfZeroes(long bytes, long start);

    /**
     * Returns expected CRC value of {@code CRC(Message,CRC(Message))}
     * when CRC is stored after the message. This value is fixed
     * and does not depend on the message or CRC start value.
     * This is also called <b>residue</b>.
     */
    long getCrcOfCrc();

    /**
     * Returns (x ** (8 * n) mod P).
     */
    default long Xpow8N(long n) {
        //works for N < 0x2000000000000000L
        return XpowN(n << 3);
    }

    /**
     * Returns (x ** n mod P).
     */
    long XpowN(long n);
}
