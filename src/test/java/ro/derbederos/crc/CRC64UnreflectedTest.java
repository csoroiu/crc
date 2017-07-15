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
    private CRCParameters crcParameters;

    public CRC64UnreflectedTest(CRCParameters crcParameters) {
        this.crcParameters = crcParameters;
    }

    @Test
    public void testCRCValue() {
        assertFalse(crcParameters.getRefIn());
        Checksum checksum = new CRC64Unreflected(
                crcParameters.getPoly(),
                crcParameters.getInitialValue(),
                crcParameters.getRefOut(),
                crcParameters.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcParameters.getCheck()), Long.toHexString(value));
    }

    @Parameterized.Parameters
    public static List<CRCParameters> getCRCParameters() {
        CRCParameters crc64 = new CRCParameters("CRC-64", 64, 0x42F0E1EBA9EA3693L, 0,
                false, false, 0, 0x6c40df5f0b497347L, 0);
        CRCParameters crc64we = new CRCParameters("CRC-64/WE", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                false, false, 0xFFFFFFFFFFFFFFFFL, 0x62ec59e3f1a4f00aL, 0xfcacbebd5931a992L);
        return Arrays.asList(crc64, crc64we);
    }
}