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
public class CRC64SlicingBy16Test {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC64SlicingBy16Test(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC64SlicingBy16(
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
        Checksum checksum = new CRC64SlicingBy16(
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

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() == 64)
                .collect(Collectors.toList());
    }
}
