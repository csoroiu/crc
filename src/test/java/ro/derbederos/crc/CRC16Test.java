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
    private CRCModel crcModel;

    public CRC16Test(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC16((int) crcModel.getPoly(),
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
        Checksum checksum = new CRC16((int) crcModel.getPoly(),
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
        CRCModel crc16 = new CRCModel("CRC-16", 16, 0x8005, 0,
                true, true, 0, 0xBB3D, 0);
        CRCModel crc16augccitt = new CRCModel("CRC-16/AUG-CCITT", 16, 0x1021, 0x1D0F,
                false, false, 0, 0xE5CC, 0);
        CRCModel crc16buypass = new CRCModel("CRC-16/BUYPASS", 16, 0x8005, 0,
                false, false, 0, 0xFEE8, 0);
        CRCModel ccittfalse = new CRCModel("CRC-16/CCITT-FALSE", 16, 0x1021, 0xFFFF,
                false, false, 0, 0x29B1, 0);
        CRCModel crc16cdma2000 = new CRCModel("CRC-16/CDMA2000", 16, 0xC867, 0xFFFF,
                false, false, 0, 0x4C06, 0);
        CRCModel crc16cms = new CRCModel("CRC-16/CMS", 16, 0x8005, 0xFFFF,
                false, false, 0, 0xAEE7, 0);
        CRCModel crc16dds110 = new CRCModel("CRC-16/DDS-110", 16, 0x8005, 0x800D,
                false, false, 0, 0x9ECF, 0);
        CRCModel crc16dectr = new CRCModel("CRC-16/DECT-R", 16, 0x0589, 0,
                false, false, 0x01, 0x007E, 0x0589);
        CRCModel crc16dectx = new CRCModel("CRC-16/DECT-X", 16, 0x0589, 0,
                false, false, 0, 0x007F, 0);
        CRCModel crc16dnp = new CRCModel("CRC-16/DNP", 16, 0x3D65, 0,
                true, true, 0xFFFF, 0xEA82, 0x66C5);
        CRCModel crc16en13757 = new CRCModel("CRC-16/EN-13757", 16, 0x3D65, 0,
                false, false, 0xFFFF, 0xC2B7, 0xA366);
        CRCModel crc16genibus = new CRCModel("CRC-16/GENIBUS", 16, 0x1021, 0xFFFF,
                false, false, 0xFFFF, 0xD64E, 0x1D0F);
        CRCModel crc16gsm = new CRCModel("CRC-16/GSM", 16, 0x1021, 0,
                false, false, 0xFFFF, 0xCE3C, 0x1D0F);
        CRCModel crc16lj1200 = new CRCModel("CRC-16/LJ1200", 16, 0x6F63, 0,
                false, false, 0, 0xBDF4, 0);
        CRCModel crc16maxim = new CRCModel("CRC-16/MAXIM", 16, 0x8005, 0,
                true, true, 0xFFFF, 0x44C2, 0xB001);
        CRCModel crc16mrcf4xx = new CRCModel("CRC-16/MCRF4XX", 16, 0x1021, 0xFFFF,
                true, true, 0, 0x6F91, 0);
        CRCModel crc16opensafetya = new CRCModel("CRC-16/OPENSAFETY-A", 16, 0x5935, 0,
                false, false, 0, 0x5D38, 0);
        CRCModel crc16opensafetyb = new CRCModel("CRC-16/OPENSAFETY-B", 16, 0x755B, 0,
                false, false, 0, 0x20FE, 0);
        CRCModel crc16profibus = new CRCModel("CRC-16/PROFIBUS", 16, 0x1DCF, 0xFFFF,
                false, false, 0xFFFF, 0xA819, 0xE394);
        CRCModel crc16riello = new CRCModel("CRC-16/RIELLO", 16, 0x1021, 0xB2AA,
                true, true, 0, 0x63D0, 0);
        CRCModel crc16t10dif = new CRCModel("CRC-16/T10-DIF", 16, 0x8BB7, 0,
                false, false, 0, 0xD0DB, 0);
        CRCModel crc16teledisk = new CRCModel("CRC-16/TELEDISK", 16, 0xA097, 0,
                false, false, 0, 0x0FB3, 0);
        CRCModel crc16tms37157 = new CRCModel("CRC-16/TMS37157", 16, 0x1021, 0x89EC,
                true, true, 0, 0x26B1, 0);
        CRCModel crc16usb = new CRCModel("CRC-16/USB", 16, 0x8005, 0xFFFF,
                true, true, 0xFFFF, 0xB4C8, 0xB001);
        CRCModel crca = new CRCModel("CRC-A", 16, 0x1021, 0xC6C6,
                true, true, 0, 0xBF05, 0);
        CRCModel kermit = new CRCModel("KERMIT", 16, 0x1021, 0,
                true, true, 0, 0x2189, 0);
        CRCModel modbus = new CRCModel("MODBUS", 16, 0x8005, 0xFFFF,
                true, true, 0, 0x4B37, 0);
        CRCModel x25 = new CRCModel("X-25", 16, 0x1021, 0xFFFF,
                true, true, 0xFFFF, 0x906E, 0xF0B8);
        CRCModel xmodem = new CRCModel("XMODEM", 16, 0x1021, 0,
                false, false, 0, 0x31C3, 0);

        return Arrays.asList(
                crc16, crc16augccitt, crc16buypass, ccittfalse, crc16cdma2000, crc16cms, crc16dds110, crc16dectr,
                crc16dectx, crc16dnp, crc16en13757, crc16genibus, crc16gsm, crc16lj1200, crc16maxim, crc16mrcf4xx,
                crc16opensafetya, crc16opensafetyb, crc16profibus, crc16riello, crc16t10dif, crc16teledisk,
                crc16tms37157, crc16usb, crca, kermit, modbus, x25, xmodem);
    }
}