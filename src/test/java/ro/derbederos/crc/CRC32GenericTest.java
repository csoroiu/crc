package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.Util.intToBytes;
import static ro.derbederos.crc.Util.roundToByte;

@RunWith(Parameterized.class)
public class CRC32GenericTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC32GenericTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
        checksum.reset();
        for (byte inputByte : testInput) {
            checksum.update(inputByte);
        }
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testResidue() {
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                0,
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                0);

        int input = (int) crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Integer.reverse(input) >>> 32 - roundToByte(crcModel.getWidth());
        }
        byte[] newByte = intToBytes(input);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 4 - len, len);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 32).collect(Collectors.toList());

    }
}