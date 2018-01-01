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

import org.junit.Assert;
import org.junit.Test;
import ro.derbederos.crc.purejava.CRC64SlicingBy16;

import java.util.Random;
import java.util.function.Function;
import java.util.zip.Checksum;

import static java.lang.Long.toHexString;
import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.CRCModelSelfCheck.computeResidue;

public abstract class AbstractCRCTest {
    private static final byte[] testInput = "123456789".getBytes();
    private static final byte[] testInputSample = "alabalaportocala".getBytes();
    private static final byte[] testInputSampleA = "alabal".getBytes();
    private static final byte[] testInputSampleB = "aportocala".getBytes();
    private static final byte[] testInputLong = new byte[1024];

    static {
        long SEED = 0x12fed1a214ecbd00L;
        Random r = new Random(SEED);
        r.nextBytes(testInputLong);
    }

    protected final CRCModel crcModel;
    private final CRC crc;

    protected AbstractCRCTest(CRCModel crcModel, Function<CRCModel, CRC> supplier) {
        this.crcModel = crcModel;
        this.crc = supplier.apply(crcModel);
    }

    @Test
    public void testCRCValue() {
        crc.update(testInput, 0, testInput.length);
        long value = crc.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        for (byte inputByte : testInput) {
            crc.update(inputByte);
        }
        long value = crc.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueUpdateBitsArray() {
        crc.updateBits(testInput, 0, testInput.length * 8);
        long value = crc.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueUpdateBits64() {
        long input = 0xFEDCBA9876543210L;
        for (int i = 0; i < 8; i++) {
            crc.updateBits(input >> 8 * i, 8);
        }
        long crcExpected = crc.getValue();

        if (!crcModel.getRefIn()) {
            input = Long.reverseBytes(input);
        }

        crc.reset();
        crc.updateBits(input, 64);

        long crcActual = crc.getValue();
        assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    @Test
    public void testCRCValueUpdateBits32() {
        int input = 0x76543210;
        for (int i = 0; i < 4; i++) {
            crc.updateBits(input >> (8 * i), 8);
        }
        long crcExpected = crc.getValue();

        if (!crcModel.getRefIn()) {
            input = Integer.reverseBytes(input);
        }
        crc.reset();
        crc.updateBits(input, 32);

        long crcActual = crc.getValue();
        assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    @Test
    public void testCRCValueLongAndUnaligned() {
        CRC checksumSliceBy16 = new CRC64SlicingBy16(crcModel);

        for (int i = 0; i < 16; i++) {
            long crcExpected = computeCrc(checksumSliceBy16, testInputLong,
                    i % 16, testInputLong.length - i % 16);

            long crcActual = computeCrc(crc, testInputLong, i % 16, testInputLong.length - i % 16);
            assertEquals("at iteration " + i, toHexString(crcExpected), toHexString(crcActual));
        }
    }

    @Test
    public void testModelSelfCheckResidue() {
        crc.update(testInput, 0, testInput.length);
        long residue = computeResidue(crc, crcModel);
        assertEquals(toHexString(crcModel.getResidue()), toHexString(residue));
    }

    @Test
    public void testModelSelfCheckResidueLong() {
        crc.update(testInputLong, 0, testInputLong.length);
        long residue = computeResidue(crc, crcModel);
        assertEquals(toHexString(crcModel.getResidue()), toHexString(residue));
    }

    @Test
    public void testGFResidue() {
        long crcOfCrc = crc.getCrcOfCrc();
        Assert.assertEquals(toHexString(crcModel.getResidue()), toHexString(crcOfCrc));
    }

    @Test
    public void testConcatenate() {
        long crcExpected = computeCrc(crc, testInputSample, 0, testInputSample.length);
        long crcA = computeCrc(crc, testInputSampleA, 0, testInputSampleA.length);
        long crcB = computeCrc(crc, testInputSampleB, 0, testInputSampleB.length);

        long crcActual = crc.concatenate(crcA, crcB, testInputSampleB.length);

        Assert.assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    @Test
    public void testConcatenateLong() {
        long crcExpected = computeCrc(crc, testInputLong, 0, testInputLong.length);

        for (int i = 0; i < testInputLong.length; i++) {
            int bytesB = testInputLong.length - i;
            long crcA = computeCrc(crc, testInputLong, 0, i);
            long crcB = computeCrc(crc, testInputLong, i, bytesB);
            long crcActual = crc.concatenate(crcA, crcB, bytesB);

            assertEquals("at iteration " + i, toHexString(crcExpected), toHexString(crcActual));
        }
    }

    @Test
    public void testConcatenateZeroes() {
        crc.update(testInputLong, 0, testInputLong.length);
        long crcInitial = crc.getValue();
        for (int i = 0; i < 1024; i++) {
            crc.update(0);
        }
        long crcOfZeroesExpected = crc.getValue();

        long crcOfZeroesActual = crc.concatenateZeroes(crcInitial, 1024);

        Assert.assertEquals(toHexString(crcOfZeroesExpected), toHexString(crcOfZeroesActual));
    }

    @Test
    public void testAppendZeroes() {
        crc.update(testInputLong, 0, testInputLong.length);
        for (int i = 0; i < 1024; i++) {
            crc.update(0);
        }
        long crcOfZeroesExpected = crc.getValue();

        crc.reset();
        crc.update(testInputLong, 0, testInputLong.length);
        crc.appendZeroes(1024);
        long crcOfZeroesActual = crc.getValue();

        Assert.assertEquals(toHexString(crcOfZeroesExpected), toHexString(crcOfZeroesActual));
    }

    @Test
    public void testAppend() {
        long crcExpected = computeCrc(crc, testInputSample, 0, testInputSample.length);
        long crcB = computeCrc(crc, testInputSampleB, 0, testInputSampleB.length);

        crc.reset();
        crc.update(testInputSampleA, 0, testInputSampleA.length);
        crc.append(crcB, testInputSampleB.length);

        long crcActual = crc.getValue();

        Assert.assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    private static long computeCrc(Checksum checksum, byte[] bytes, int offset, int len) {
        checksum.reset();
        checksum.update(bytes, offset, len);
        return checksum.getValue();
    }
}
