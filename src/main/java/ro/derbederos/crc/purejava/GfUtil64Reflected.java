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

import static java.lang.Long.compareUnsigned;
import static java.lang.Long.reverse;

/**
 * Andrew Kadatch's and Bob Jenkins's gf_util functions from crcutil library
 * (https://code.google.com/archive/p/crcutil/downloads).
 */
class GfUtil64Reflected implements GfUtil {

    private final int degree;
    private final long init;
    private final long canonize;
    private long[] x_pow_2n = new long[Long.BYTES * 8];
    private long one;
    private long[] normalize = new long[2];

    private long crcOfCrc;

    GfUtil64Reflected(CRCModel crcModel) {
        this.degree = crcModel.getWidth();
        long poly = reverse(crcModel.getPoly()) >>> (64 - this.degree);
        this.init = reverse(crcModel.getInit()) >>> (64 - this.degree);
        this.canonize = reverse(crcModel.getXorOut()) >>> (64 - this.degree);
        init(poly);
    }

    /**
     * Initializes the tables given generating polynomial of degree (degree).
     * If "canonical" is true, starting CRC value and computed CRC value will be
     * XOR-ed with 111...111.
     */
    private void init(long poly) {
        long one = 1;
        one <<= this.degree - 1;
        this.one = one;

        this.normalize[0] = 0;
        this.normalize[1] = poly;

        long k = one >>> 1;

        for (int i = 0; i < this.x_pow_2n.length; i++) {
            this.x_pow_2n[i] = k;
            k = multiply(k, k);
        }

        this.crcOfCrc = multiply(this.canonize, this.one ^ XpowN(this.degree));
    }

    /**
     * Returns value of CRC(A, |A|, start_new) given known
     * crc=CRC(A, |A|, start_old) -- without touching the data.
     */
    private long changeStartValue(long crc, long bytes, long start_old, long start_new) {
        return (crc ^ multiply(start_new ^ start_old, Xpow8N(bytes)));
    }

    /**
     * Returns CRC of concatenation of blocks A and B when CRCs
     * of blocks A and B are known -- without touching the data.
     * <p>
     * To be precise, given CRC(A, |A|, startA) and CRC(B, |B|, 0),
     * returns CRC(AB, |AB|, startA).
     */
    @Override
    public long concatenate(long crc_A, long crc_B, long bytes_B) {
        return changeStartValue(crc_B, bytes_B, this.init ^ this.canonize /* start_B */, crc_A);
    }

    /**
     * Returns CRC of sequence of zeroes -- without touching the data.
     */
    @Override
    public long crcOfZeroes(long bytes, long start) {
        long tmp = this.canonize ^ multiply(start ^ this.canonize, Xpow8N(bytes));
        return tmp;
    }

    /**
     * Returns expected CRC value of {@code CRC(Message,CRC(Message))}
     * when CRC is stored after the message. This value is fixed
     * and does not depend on the message or CRC start value.
     * This is also called <b>residue</b>.
     */
    @Override
    public long getCrcOfCrc() {
        return this.crcOfCrc;
    }

    /**
     * Returns (x ** (8 * n) mod P).
     */
    private long Xpow8N(long n) {
        //works for N < 0x2000000000000000L
        return XpowN(n << 3);
    }

    /**
     * Returns (x ** n mod P).
     */
    private long XpowN(long n) {
        long result = this.one;

        for (int i = 0; n != 0; i++, n >>>= 1) {
            if ((n & 1) != 0) {
                result = multiply(result, this.x_pow_2n[i]);
            }
        }
        return result;
    }

    /**
     * Returns ((a * b) mod P) where "a" and "b" are of degree <= (D-1).
     */
    private long multiply(long aa, long bb) {
        return multiplyCrcUtil(aa, bb);
    }

    /**
     * Returns ((a * b) mod P) where "a" and "b" are of degree <= (D-1).
     */
    //https://github.com/torvalds/linux/blob/master/lib/crc32.c#L213 - gf2_multiply
    private long multiplyLinuxKernel(long x, long y) {
        long product = (x & 1) == 1 ? y : 0;

        for (int i = 0; i < this.degree - 1; i++) {
            product = (product >>> 1) ^ this.normalize[(int) (product & 1)];
            x >>>= 1;
            product ^= (x & 1) == 1 ? y : 0;
        }

        return product;
    }

    /**
     * Returns ((a * b) mod P) where "a" and "b" are of degree <= (D-1).
     */
    private long multiplyCrcUtil(long aa, long bb) {
        long a = aa;
        long b = bb;
        if (compareUnsigned(a ^ (a - 1), b ^ (b - 1)) < 0) {
            long temp = a;
            a = b;
            b = temp;
        }

        if (a == 0) {
            return a;
        }

        long product = 0;
        long one = this.one;
        for (; a != 0; a <<= 1) {
            if ((a & one) != 0) {
                product ^= b;
                a ^= one;
            }
            b = (b >>> 1) ^ this.normalize[(int) (b & 1)];
        }
        return product;
    }

    /**
     * Returns ((unnorm * m) mod P) where degree of m is <= (D-1)
     * and degree of value "unnorm" is provided explicitly.
     */
    private long multiplyUnnormalized(long unnorm, int degree, long m) {
        long ones = this.one | (this.one - 1);
        long v = unnorm;
        long result = 0;
        while (degree > this.degree) {
            degree -= this.degree;
            long value = v & ones;
            result ^= multiply(value, multiply(m, XpowN(degree)));
            v >>>= this.degree;
        }
        result ^= multiply(v << (this.degree - degree), m);
        return result;
    }
}
