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

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCParameters> getCRCParameters() {
        CRCParameters crc16 = new CRCParameters("CRC-16", 16, 0x8005, 0,
                true, true, 0, 0xBB3D, 0);
        CRCParameters crc16augccitt = new CRCParameters("CRC-16/AUG-CCITT", 16, 0x1021, 0x1D0F,
                false, false, 0, 0xE5CC, 0);
        CRCParameters crc16buypass = new CRCParameters("CRC-16/BUYPASS", 16, 0x8005, 0,
                false, false, 0, 0xFEE8, 0);
        CRCParameters ccittfalse = new CRCParameters("CRC-16/CCITT-FALSE", 16, 0x1021, 0xFFFF,
                false, false, 0, 0x29B1, 0);
        CRCParameters crc16cdma2000 = new CRCParameters("CRC-16/CDMA2000", 16, 0xC867, 0xFFFF,
                false, false, 0, 0x4C06, 0);
        CRCParameters crc16cms = new CRCParameters("CRC-16/CMS", 16, 0x8005, 0xFFFF,
                false, false, 0, 0xAEE7, 0);
        CRCParameters crc16dds110 = new CRCParameters("CRC-16/DDS-110", 16, 0x8005, 0x800D,
                false, false, 0, 0x9ECF, 0);
        CRCParameters crc16dectr = new CRCParameters("CRC-16/DECT-R", 16, 0x0589, 0,
                false, false, 0x01, 0x007E, 0x0589);
        CRCParameters crc16dectx = new CRCParameters("CRC-16/DECT-X", 16, 0x0589, 0,
                false, false, 0, 0x007F, 0);
        CRCParameters crc16dnp = new CRCParameters("CRC-16/DNP", 16, 0x3D65, 0,
                true, true, 0xFFFF, 0xEA82, 0x66C5);
        CRCParameters crc16en13757 = new CRCParameters("CRC-16/EN-13757", 16, 0x3D65, 0,
                false, false, 0xFFFF, 0xC2B7, 0xA366);
        CRCParameters crc16genibus = new CRCParameters("CRC-16/GENIBUS", 16, 0x1021, 0xFFFF,
                false, false, 0xFFFF, 0xD64E, 0x1D0F);
        CRCParameters crc16gsm = new CRCParameters("CRC-16/GSM", 16, 0x1021, 0,
                false, false, 0xFFFF, 0xCE3C, 0x1D0F);
        CRCParameters crc16lj1200 = new CRCParameters("CRC-16/LJ1200", 16, 0x6F63, 0,
                false, false, 0, 0xBDF4, 0);
        CRCParameters crc16maxim = new CRCParameters("CRC-16/MAXIM", 16, 0x8005, 0,
                true, true, 0xFFFF, 0x44C2, 0xB001);
        CRCParameters crc16mrcf4xx = new CRCParameters("CRC-16/MCRF4XX", 16, 0x1021, 0xFFFF,
                true, true, 0, 0x6F91, 0);
        CRCParameters crc16opensafetya = new CRCParameters("CRC-16/OPENSAFETY-A", 16, 0x5935, 0,
                false, false, 0, 0x5D38, 0);
        CRCParameters crc16opensafetyb = new CRCParameters("CRC-16/OPENSAFETY-B", 16, 0x755B, 0,
                false, false, 0, 0x20FE, 0);
        CRCParameters crc16profibus = new CRCParameters("CRC-16/PROFIBUS", 16, 0x1DCF, 0xFFFF,
                false, false, 0xFFFF, 0xA819, 0xE394);
        CRCParameters crc16riello = new CRCParameters("CRC-16/RIELLO", 16, 0x1021, 0xB2AA,
                true, true, 0, 0x63D0, 0);
        CRCParameters crc16t10dif = new CRCParameters("CRC-16/T10-DIF", 16, 0x8BB7, 0,
                false, false, 0, 0xD0DB, 0);
        CRCParameters crc16teledisk = new CRCParameters("CRC-16/TELEDISK", 16, 0xA097, 0,
                false, false, 0, 0x0FB3, 0);
        CRCParameters crc16tms37157 = new CRCParameters("CRC-16/TMS37157", 16, 0x1021, 0x89EC,
                true, true, 0, 0x26B1, 0);
        CRCParameters crc16usb = new CRCParameters("CRC-16/USB", 16, 0x8005, 0xFFFF,
                true, true, 0xFFFF, 0xB4C8, 0xB001);
        CRCParameters crca = new CRCParameters("CRC-A", 16, 0x1021, 0xC6C6,
                true, true, 0, 0xBF05, 0);
        CRCParameters kermit = new CRCParameters("KERMIT", 16, 0x1021, 0,
                true, true, 0, 0x2189, 0);
        CRCParameters modbus = new CRCParameters("MODBUS", 16, 0x8005, 0xFFFF,
                true, true, 0, 0x4B37, 0);
        CRCParameters x25 = new CRCParameters("X-25", 16, 0x1021, 0xFFFF,
                true, true, 0xFFFF, 0x906E, 0xF0B8);
        CRCParameters xmodem = new CRCParameters("XMODEM", 16, 0x1021, 0,
                false, false, 0, 0x31C3, 0);

        return Arrays.asList(
                crc16, crc16augccitt, crc16buypass, ccittfalse, crc16cdma2000, crc16cms, crc16dds110, crc16dectr,
                crc16dectx, crc16dnp, crc16en13757, crc16genibus, crc16gsm, crc16lj1200, crc16maxim, crc16mrcf4xx,
                crc16opensafetya, crc16opensafetyb, crc16profibus, crc16riello, crc16t10dif, crc16teledisk,
                crc16tms37157, crc16usb, crca, kermit, modbus, x25, xmodem);
    }
}