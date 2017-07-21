package ro.derbederos.crc;

import org.junit.Test;

import java.util.Random;
import java.util.function.Function;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

public abstract class AbstractCRCTest {
    private static final byte[] testInput = "123456789".getBytes();
    private static final byte[] testInputLong = new byte[1024];

    static {
        long SEED = 0x12fed1a214ecbd00L;
        Random r = new Random(SEED);
        r.nextBytes(testInputLong);
    }

    protected final CRCModel crcModel;
    private final Function<CRCModel, Checksum> supplier;

    AbstractCRCTest(CRCModel crcModel, Function<CRCModel, Checksum> supplier) {
        this.crcModel = crcModel;
        this.supplier = supplier;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = supplier.apply(crcModel);
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        Checksum checksum = supplier.apply(crcModel);
        checksum.reset();
        for (byte inputByte : testInput) {
            checksum.update(inputByte);
        }
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testCRCValueLongAndUnaligned() {
        Checksum checksum = supplier.apply(crcModel);
        Checksum checksumSliceBy16 = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());

        for (int i = 0; i < 256; i++) {
            checksumSliceBy16.reset();
            checksumSliceBy16.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long expectedValue = checksumSliceBy16.getValue();
            checksum.reset();
            checksum.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long value = checksum.getValue();
            assertEquals("at iteration " + i, Long.toHexString(expectedValue), Long.toHexString(value));
        }
    }
}
