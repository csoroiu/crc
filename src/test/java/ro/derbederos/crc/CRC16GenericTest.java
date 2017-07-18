package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC16GenericTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC16GenericTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC16Generic(
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
        Checksum checksum = new CRC16Generic(
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
        Checksum checksum = new CRC16Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                0,
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                0);

        short input = (short) crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = (short) (Util.reverseShort(input) >>> 16 - Util.roundToByte(crcModel.getWidth()));
        }
        byte[] newByte = Util.shortToBytes(input);
        int len = Util.roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 2 - len, len);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 16)
                .collect(Collectors.toList());
    }
}