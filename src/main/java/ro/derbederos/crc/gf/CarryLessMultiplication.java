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

package ro.derbederos.crc.gf;

public class CarryLessMultiplication {
    public static long clmul(int x, int y) {
        return clmul(x, y, 32);
    }

    public static long clmul(int x, int y, int degree) {
        //make sure x and y are no longer than degree
        long product = 0;
        for (int i = 0; i < degree; i++) {
            product = (product << 1L) ^ Integer.toUnsignedLong(y & (x >> 31));
            x <<= 1;
        }
        return product;
    }

//    public static long clmul(int x, int y, int degree) {
//        //make sure x and y are no longer than degree
//        long product = 0;
//        long rx = Integer.toUnsignedLong(x);
//        long ry = Integer.toUnsignedLong(y);
//        for (; rx != 0; ) {
//            product ^= rx & -(ry & 1);
//            ry >>= 1;
//            rx <<= 1;
//        }
//        return product;
//    }

    private static int high(long l) {
        return (int) (l >>> 32);
    }

    private static int low(long l) {
        return (int) (l & 0xFFFFFFFFL);
    }

    private static long newLong(int h, int l) {
        return (Integer.toUnsignedLong(h) << 32) | (Integer.toUnsignedLong(l));
    }

    //one iteration carry-less Karatsuba
    public static long[] clmul64(long a, long b) {
        long[] result = new long[2];
        int a1 = high(a);
        int a0 = low(a);
        int b1 = high(b);
        int b0 = low(b);
        long c = clmul(a1, b1);
        long d = clmul(a0, b0);
        long e = clmul(a0 ^ a1, b0 ^ b1);
        int c1 = high(c);
        int c0 = low(c);
        int d1 = high(d);
        int d0 = low(d);
        int e1 = high(e);
        int e0 = low(e);
        result[1] = newLong(c1, c0 ^ c1 ^ d1 ^ e1);
        result[0] = newLong(d1 ^ c0 ^ d0 ^ e0, d0);
        return result;
    }

    //one iteration carry-less Karatsuba
    public static long[] clmul128(long[] a, long[] b) {
        long[] result = new long[4];
        long[] c = clmul64(a[1], b[1]);
        long[] d = clmul64(a[0], b[0]);
        long[] e = clmul64(a[0] ^ a[1], b[0] ^ b[1]);
        result[3] = c[1];
        result[2] = c[0] ^ c[1] ^ d[1] ^ e[1];
        result[1] = d[1] ^ c[0] ^ d[0] ^ e[0];
        result[0] = d[0];
        return result;
    }

    public static long clmulReflected(int x, int y) {
        return clmul(x, y) << 1;
    }
}
