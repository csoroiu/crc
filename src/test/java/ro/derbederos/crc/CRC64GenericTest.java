package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC64GenericTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCParameters crcParameters;

    public CRC64GenericTest(CRCParameters crcParameters) {
        this.crcParameters = crcParameters;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC64Generic(
                crcParameters.getWidth(),
                crcParameters.getPoly(),
                crcParameters.getInitialValue(),
                crcParameters.getRefIn(),
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
        CRCParameters crc64go = new CRCParameters("CRC-64/GO-ISO", 64, 0x000000000000001bL, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0xb90956c775a41001L, 0x5300000000000000L);
        CRCParameters crc64we = new CRCParameters("CRC-64/WE", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                false, false, 0xFFFFFFFFFFFFFFFFL, 0x62ec59e3f1a4f00aL, 0xfcacbebd5931a992L);
        CRCParameters crc64xz = new CRCParameters("CRC-64/XZ", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0x995dc9bbdf1939faL, 0x49958c9abd7d353fL);
        CRCParameters crc32 = new CRCParameters("CRC-32", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0xcbf43926L, 0xdebb20e3L);
        CRCParameters crc32autosar = new CRCParameters("CRC-32/AUTOSAR", 32, 0xf4acfb13, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0x1697d06aL, 0x904cddbfL);
        CRCParameters crc32bzip2 = new CRCParameters("CRC-32/BZIP2", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                false, false, 0xFFFFFFFFL, 0xfc891918L, 0xc704dd7bL);
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

        return Arrays.asList(crc64, crc64go, crc64we, crc64xz,
                crc32, crc32autosar, crc32bzip2,
                crc16, xmodem, ccittfalse, augccitt, kermit);
    }
}