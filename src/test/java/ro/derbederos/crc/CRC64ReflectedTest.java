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
    private CRCParameters crcParameters;

    public CRC64ReflectedTest(CRCParameters crcParameters) {
        this.crcParameters = crcParameters;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC64Reflected(
                crcParameters.getPoly(),
                crcParameters.getInitialValue(),
                crcParameters.getRefOut(),
                crcParameters.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcParameters.getCheck()), Long.toHexString(value));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCParameters> getCRCParameters() {
        CRCParameters crc64goiso = new CRCParameters("CRC-64/GO-ISO", 64, 0x000000000000001bL, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0xb90956c775a41001L, 0x5300000000000000L);
        CRCParameters crc64xz = new CRCParameters("CRC-64/XZ", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0x995dc9bbdf1939faL, 0x49958c9abd7d353fL);
        return Arrays.asList(crc64goiso, crc64xz);
    }
}