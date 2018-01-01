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

import org.junit.Test;

import static java.lang.Long.toBinaryString;
import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul128;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmulReflected;

public class CarryLessMultiplicationTest {

    @Test
    public void testClmul() {
        int a = 0xB; //0b1011;
        int b = 0xE; //0b1110;
        long c = 0b01100010;
        long actual = clmul(a, b);
        assertEquals(toBinaryString(c), toBinaryString(actual));
    }

    @Test
    public void testClmulAll() {
        int a = 0xFFFFFFFF;
        int b = 0xFFFFFFFF;
        long c = 0x5555555555555555L;
        long actual = clmul(a, b);
        assertEquals(toBinaryString(c), toBinaryString(actual));
    }

    @Test
    public void testClmul_64() {
        long a = 0x8000000000000001L;
        long b = 0x8000000000000001L;
        long c1 = 0x4000000000000000L;
        long c0 = 0x0000000000000001L;
        long actual[] = clmul64(a, b);
        assertEquals(toBinaryString(c1), toBinaryString(actual[1]));
        assertEquals(toBinaryString(c0), toBinaryString(actual[0]));
    }

    @Test
    public void testClmulAll_64() {
        long a = 0xFFFFFFFFFFFFFFFFL;
        long b = 0xFFFFFFFFFFFFFFFFL;
        long c1 = 0x5555555555555555L;
        long c0 = 0x5555555555555555L;
        long actual[] = clmul64(a, b);
        assertEquals(toBinaryString(c1), toBinaryString(actual[1]));
        assertEquals(toBinaryString(c0), toBinaryString(actual[0]));
    }

    @Test
    public void testClmul_128() {
        long[] a = {0x00000000000000001L, 0x8000000000000000L};
        long[] b = {0x00000000000000001L, 0x8000000000000000L};
        long c3 = 0x4000000000000000L;
        long c2 = 0x0000000000000000L;
        long c1 = 0x0000000000000000L;
        long c0 = 0x00000000000000001L;
        long actual[] = clmul128(a, b);
        assertEquals(toBinaryString(c3), toBinaryString(actual[3]));
        assertEquals(toBinaryString(c2), toBinaryString(actual[2]));
        assertEquals(toBinaryString(c1), toBinaryString(actual[1]));
        assertEquals(toBinaryString(c0), toBinaryString(actual[0]));
    }

    @Test
    public void testClmulAll_128() {
        long[] a = {0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL};
        long[] b = {0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL};
        long c3 = 0x5555555555555555L;
        long c2 = 0x5555555555555555L;
        long c1 = 0x5555555555555555L;
        long c0 = 0x5555555555555555L;
        long actual[] = clmul128(a, b);
        assertEquals(toBinaryString(c3), toBinaryString(actual[3]));
        assertEquals(toBinaryString(c2), toBinaryString(actual[2]));
        assertEquals(toBinaryString(c1), toBinaryString(actual[1]));
        assertEquals(toBinaryString(c0), toBinaryString(actual[0]));
    }

    @Test
    public void testClmulReflected() {
        int a = 0xD0000000; //0b11010000...
        int b = 0x70000000; //0b01110000....
        long expected = 0x4600000000000000L;//0b01000110...;
        long actual = clmulReflected(a, b);
        assertEquals(toBinaryString(expected), toBinaryString(actual));
    }

    @Test
    public void testClmulReflectedAll() {
        int a = 0xFFFFFFFF;
        int b = 0xFFFFFFFF;
        long expected = 0xAAAAAAAAAAAAAAAAL;
        long actual = clmulReflected(a, b);
        assertEquals(toBinaryString(expected), toBinaryString(actual));
    }
}