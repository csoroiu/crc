package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC64ReflectedTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC64ReflectedTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC64Reflected(
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getCheck()), Long.toHexString(value));
    }

    @Test
    public void testCRCValueUpdateOneByOne() {
        Checksum checksum = new CRC64Reflected(
                crcModel.getPoly(),
                crcModel.getInit(),
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
        CRCModel crc64goiso = new CRCModel("CRC-64/GO-ISO", 64, 0x000000000000001bL, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0xb90956c775a41001L, 0x5300000000000000L);
        CRCModel crc64xz = new CRCModel("CRC-64/XZ", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0x995dc9bbdf1939faL, 0x49958c9abd7d353fL);
        return Arrays.asList(crc64goiso, crc64xz);
    }
}