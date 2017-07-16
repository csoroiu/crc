package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC32Test {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC32Test(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC32((int) crcModel.getPoly(),
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
        Checksum checksum = new CRC32((int) crcModel.getPoly(),
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

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        CRCModel crc32 = new CRCModel("CRC-32", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0xcbf43926L, 0xdebb20e3L);
        CRCModel crc32autosar = new CRCModel("CRC-32/AUTOSAR", 32, 0xf4acfb13, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0x1697d06aL, 0x904cddbfL);
        CRCModel crc32bzip2 = new CRCModel("CRC-32/BZIP2", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                false, false, 0xFFFFFFFFL, 0xfc891918L, 0xc704dd7bL);
        CRCModel crc32c = new CRCModel("CRC-32C", 32, 0x1EDC6F41L, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0xE3069283L, 0xB798B438L);
        CRCModel crc32d = new CRCModel("CRC-32D", 32, 0xA833982BL, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0x87315576L, 0x45270551L);
        CRCModel crc32mpeg2 = new CRCModel("CRC-32/MPEG-2", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                false, false, 0, 0x0376E6E7L, 0);
        CRCModel crc32posix = new CRCModel("CRC-32/POSIX", 32, 0x04C11DB7L, 0,
                false, false, 0xFFFFFFFFL, 0x765E7680L, 0xC704DD7BL);
        CRCModel crc32q = new CRCModel("CRC-32Q", 32, 0x814141ABL, 0,
                false, false, 0, 0x3010BF7FL, 0);
        CRCModel jamcrc = new CRCModel("JAMCRC", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                true, true, 0, 0x340BC6D9L, 0);
        CRCModel xfer = new CRCModel("XFER", 32, 0x000000AFL, 0,
                false, false, 0, 0xBD0BE338L, 0);

        return Arrays.asList(crc32, crc32autosar, crc32bzip2, crc32c, crc32d, crc32mpeg2, crc32posix, crc32q, jamcrc, xfer);
    }
}