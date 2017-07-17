package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ro.derbederos.crc.Util.longToBytes;
import static ro.derbederos.crc.Util.roundToByte;

@RunWith(Parameterized.class)
public class CRC64GenericTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC64GenericTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        checksum.reset();
        for (byte inputByte : testInput) {
            checksum.update(inputByte);
        }
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testResidue() {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                0,
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                0);

        long input = crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Long.reverse(input) >>> 64 - roundToByte(crcModel.getWidth());
        }
        byte[] newByte = longToBytes(input);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 8 - len, len);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Test
    public void testCRCValueSelfTest() {
        CRCModelSelfTest.validateCRCModelParams(crcModel);
        assertTrue(CRCModelSelfTest.validateCRCValue(crcModel));
    }

    @Test
    public void testResidueSelfTest() {
        assertTrue(CRCModelSelfTest.validateCRCResidue(crcModel));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.asList(CRCFactory.getDefinedModels());
    }
}