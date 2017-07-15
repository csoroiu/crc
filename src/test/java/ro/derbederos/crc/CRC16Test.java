package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC16Test {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCParameters crcParameters;

    public CRC16Test(CRCParameters crcParameters) {
        this.crcParameters = crcParameters;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC16((int) crcParameters.getPoly(),
                (int) crcParameters.getInitialValue(),
                crcParameters.getRefIn(),
                crcParameters.getRefOut(),
                (int) crcParameters.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        assertEquals(Long.toHexString(crcParameters.getCheck()), Long.toHexString(value));
    }

    @Parameterized.Parameters
    public static List<CRCParameters> getCRCParameters() {
        CRCParameters crc16 = new CRCParameters("CRC-16", 16, 0x8005, 0,
                true, true, 0, 0xbb3d, 0);
        CRCParameters xmodem = new CRCParameters("ZMODEM", 16, 0x1021, 0,
                false, false, 0, 0x31c3, 0);
        CRCParameters ccittfalse = new CRCParameters("CRC-16/CCITT-FALSE", 16, 0x1021,
                0xFFFF, false, false, 0, 0x29b1, 0);
        CRCParameters augccitt = new CRCParameters("CRC-16/SPI-FUJITSU", 16, 0x1021,
                0x1D0F, false, false, 0, 0xe5cc, 0);
        CRCParameters kermit = new CRCParameters("CRC-16/CCITT", 16, 0x1021,
                0, true, true, 0, 0x2189, 0);
        return Arrays.asList(crc16, xmodem, ccittfalse, augccitt, kermit);
    }
}