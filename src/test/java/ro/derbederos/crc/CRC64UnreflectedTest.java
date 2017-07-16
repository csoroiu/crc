package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class CRC64UnreflectedTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC64UnreflectedTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        assertFalse(crcModel.getRefIn());
        Checksum checksum = new CRC64Unreflected(
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
        Checksum checksum = new CRC64Unreflected(
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
        CRCModel crc64 = new CRCModel("CRC-64", 64, 0x42F0E1EBA9EA3693L, 0,
                false, false, 0, 0x6c40df5f0b497347L, 0);
        CRCModel crc64we = new CRCModel("CRC-64/WE", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                false, false, 0xFFFFFFFFFFFFFFFFL, 0x62ec59e3f1a4f00aL, 0xfcacbebd5931a992L);
        return Arrays.asList(crc64, crc64we);
    }
}