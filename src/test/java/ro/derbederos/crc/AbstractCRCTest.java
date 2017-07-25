package ro.derbederos.crc;

import org.junit.Assume;
import org.junit.Test;

import java.nio.ByteOrder;
import java.util.Random;
import java.util.function.Function;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.Util.longToBytes;
import static ro.derbederos.crc.Util.roundToByte;

public abstract class AbstractCRCTest {
    protected static final byte[] testInput = "123456789".getBytes();
    protected static final byte[] testInputLong = new byte[1024];

    static {
        long SEED = 0x12fed1a214ecbd00L;
        Random r = new Random(SEED);
        r.nextBytes(testInputLong);
    }

    protected final CRCModel crcModel;
    protected final Function<CRCModel, Checksum> supplier;

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

        for (int i = 0; i < 16; i++) {
            checksumSliceBy16.reset();
            checksumSliceBy16.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long expectedValue = checksumSliceBy16.getValue();
            checksum.reset();
            checksum.update(testInputLong, i % 16, testInputLong.length - i % 16);
            long value = checksum.getValue();
            assertEquals("at iteration " + i, Long.toHexString(expectedValue), Long.toHexString(value));
        }
    }

    @Test
    public void testResidue() {
        Assume.assumeTrue(crcModel.getWidth() % 8 == 0);
        Checksum checksum = supplier.apply(crcModel);
        long input = checksum.getValue();
        byte[] newBytes = crcModel.getRefOut() ?
                longToBytes(input, ByteOrder.LITTLE_ENDIAN) :
                longToBytes(input << 64 - roundToByte(crcModel.getWidth()), ByteOrder.BIG_ENDIAN);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newBytes, 0, len);

        long residue = checksum.getValue() ^ crcModel.getXorOut();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Test
    public void testResidueLong() {
        Assume.assumeTrue(crcModel.getWidth() % 8 == 0);
        Checksum checksum = supplier.apply(crcModel);
        checksum.update(testInputLong, 0, testInputLong.length);
        long input = checksum.getValue();
        byte[] newBytes = crcModel.getRefOut() ?
                longToBytes(input, ByteOrder.LITTLE_ENDIAN) :
                longToBytes(input << 64 - roundToByte(crcModel.getWidth()), ByteOrder.BIG_ENDIAN);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newBytes, 0, len);

        long residue = checksum.getValue() ^ crcModel.getXorOut();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

}
