package ro.derbederos.crc;

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
    private static final byte[] testInputLong = new byte[1024];

    static {
        long SEED = 0x12fed1a214ecbd00L;
        Random r = new Random(SEED);
        r.nextBytes(testInputLong);
    }

    protected final CRCModel crcModel;
    private final Function<CRCModel, CRC> supplier;

    protected AbstractCRCTest(CRCModel crcModel, Function<CRCModel, CRC> supplier) {
        this.crcModel = crcModel;
        this.supplier = supplier;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = supplier.apply(crcModel);
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        Checksum checksum = supplier.apply(crcModel);
        checksum.reset();
        for (byte inputByte : testInput) {
            checksum.update(inputByte);
        }
        long value = checksum.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueUpdateBits() {
        CRC checksum = supplier.apply(crcModel);
        checksum.reset();
        for (byte inputByte : testInput) {
            checksum.updateBits(inputByte, 8);
        }
        long value = checksum.getValue();
        assertEquals(toHexString(crcModel.getCheck()), toHexString(value));
    }

    @Test
    public void testCRCValueLongAndUnaligned() {
        Checksum checksum = supplier.apply(crcModel);
        Checksum checksumSliceBy16 = new CRC64SlicingBy16(crcModel);

        for (int i = 0; i < 16; i++) {
            checksumSliceBy16.reset();
            checksumSliceBy16.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long expectedValue = checksumSliceBy16.getValue();
            checksum.reset();
            checksum.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long value = checksum.getValue();
            assertEquals("at iteration " + i, toHexString(expectedValue), toHexString(value));
        }
    }

    @Test
    public void testResidue() {
        CRC crc = supplier.apply(crcModel);
        crc.update(testInput, 0, testInput.length);
        long residue = computeResidue(crc, crcModel);
        assertEquals(toHexString(crcModel.getResidue()), toHexString(residue));
    }

    @Test
    public void testResidueLong() {
        CRC crc = supplier.apply(crcModel);
        crc.update(testInputLong, 0, testInputLong.length);
        long residue = computeResidue(crc, crcModel);
        assertEquals(toHexString(crcModel.getResidue()), toHexString(residue));
    }
}
