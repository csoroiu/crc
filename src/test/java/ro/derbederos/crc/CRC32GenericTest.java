package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.Util.intToBytes;
import static ro.derbederos.crc.Util.roundToByte;

@RunWith(Parameterized.class)
public class CRC32GenericTest {
    private static final byte[] testInput = "123456789".getBytes();
    private CRCModel crcModel;

    public CRC32GenericTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testCRCValue() {
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
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
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
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

    @Test
    public void testResidue() {
        Checksum checksum = new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                0,
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                0);

        int input = (int) crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Integer.reverse(input) >>> 32 - roundToByte(crcModel.getWidth());
        }
        byte[] newByte = intToBytes(input);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 4 - len, len);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        //CRC-32
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

        //CRC-31
        CRCModel crc31philips = new CRCModel("CRC-31/PHILIPS", 31, 0x04C11DB7L, 0x7FFFFFFFL,
                false, false, 0x7FFFFFFFL, 0x0CE9E46CL, 0x4EAF26F1L);

        //CRC-30
        CRCModel crc30cdma = new CRCModel("CRC-30/CDMA", 30, 0x2030B9C7L, 0x3FFFFFFFL,
                false, false, 0x3FFFFFFFL, 0x04C34ABFL, 0X34EFA55AL);

        //CRC-24
        CRCModel crc24 = new CRCModel("CRC-24", 24, 0x864CFBL, 0xB704CEL,
                false, false, 0, 0x21CF02L, 0);
        CRCModel crc24ble = new CRCModel("CRC-24/BLE", 24, 0x00065BL, 0x555555L,
                true, true, 0, 0xC25A56L, 0);
        CRCModel crc24flexyayA = new CRCModel("CRC-24/FLEXRAY-A", 24, 0x5D6DCBL, 0xFEDCBAL,
                false, false, 0, 0x7979BDL, 0);
        CRCModel crc24flexyayB = new CRCModel("CRC-24/FLEXRAY-B", 24, 0x5D6DCBL, 0xABCDEFL,
                false, false, 0, 0x1F23B8L, 0);
        CRCModel crc24interlaken = new CRCModel("CRC-24/INTERLAKEN", 24, 0x328B63L, 0xFFFFFFL,
                false, false, 0xFFFFFFL, 0xB4F3E6L, 0x144E63L);
        CRCModel crc24lteA = new CRCModel("CRC-24/LTE-A", 24, 0x864CFBL, 0,
                false, false, 0, 0xCDE703L, 0);
        CRCModel crc24lteB = new CRCModel("CRC-24/LTE-B", 24, 0x800063L, 0,
                false, false, 0, 0x23EF52L, 0);

        //CRC-21
        CRCModel crc21canfd = new CRCModel("CRC-21/CAN-FD", 21, 0x102899L, 0,
                false, false, 0, 0x0ED841L, 0);

        //CRC-17
        CRCModel crc17canfd = new CRCModel("CRC-17/CAN-FD", 17, 0x1685BL, 0,
                false, false, 0, 0x04F03L, 0);

        //CRC-16
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

        //CRC-15
        CRCModel crc15 = new CRCModel("CRC-15", 15, 0x4599, 0,
                false, false, 0, 0x059E, 0);
        CRCModel crc15mpt1327 = new CRCModel("CRC-15/MPT1327", 15, 0x6815, 0,
                false, false, 0x0001, 0x2566, 0x6815);

        //CRC-14
        CRCModel crc14darc = new CRCModel("CRC-14/DARC", 14, 0x0805, 0,
                true, true, 0, 0x082D, 0);

        CRCModel crc14gsm = new CRCModel("CRC-14/GSM", 14, 0x202D, 0,
                false, false, 0x3FFF, 0x30AE, 0x031E);

        //CRC-13
        CRCModel crc13bbc = new CRCModel("CRC-13/BBC", 13, 0x1CF5, 0,
                false, false, 0, 0x04FA, 0);

        //CRC-12
        CRCModel crc12cdma2000 = new CRCModel("CRC-12/CDMA2000", 12, 0xF13, 0xFFF,
                false, false, 0, 0xD4D, 0);
        CRCModel crc12dect = new CRCModel("CRC-12/DECT", 12, 0x80F, 0,
                false, false, 0, 0xF5B, 0);
        CRCModel crc12gsm = new CRCModel("CRC-12/GSM", 12, 0xD31, 0,
                false, false, 0xFFF, 0xB34, 0x178);
        CRCModel crc12umts = new CRCModel("CRC-12/UMTS", 12, 0x80F, 0,
                false, true, 0, 0xDAF, 0);

        //CRC-11
        CRCModel crc11 = new CRCModel("CRC-11", 11, 0x385, 0x01A,
                false, false, 0, 0x5A3, 0);
        CRCModel crc11umts = new CRCModel("CRC-11/UMTS", 11, 0x307, 0,
                false, false, 0, 0x061, 0);

        //CRC-10
        CRCModel crc10 = new CRCModel("CRC-10", 10, 0x233, 0,
                false, false, 0, 0x199, 0);
        CRCModel crc10cdma2000 = new CRCModel("CRC-10/CDMA2000", 10, 0x3D9, 0x3FF,
                false, false, 0, 0x233, 0);
        CRCModel crc10gsm = new CRCModel("CRC-10/GSM", 10, 0x175, 0,
                false, false, 0x3FF, 0x12A, 0x0C6);

        //CRC-8
        CRCModel crc8 = new CRCModel("CRC-8", 8, 0x07, 0,
                false, false, 0, 0xF4, 0);
        CRCModel crc8autosar = new CRCModel("CRC-8/AUTOSAR", 8, 0x2F, 0xFF,
                false, false, 0xFF, 0xDF, 0x42);
        CRCModel crc8bluetooth = new CRCModel("CRC-8/BLUETOOTH", 8, 0xA7, 0,
                true, true, 0, 0x26, 0);
        CRCModel crc8cdma2000 = new CRCModel("CRC-8/CDMA2000", 8, 0x9B, 0xFF,
                false, false, 0, 0xDA, 0);
        CRCModel crc8darc = new CRCModel("CRC-8/DARC", 8, 0x39, 0,
                true, true, 0, 0x15, 0);
        CRCModel crc8dvbs2 = new CRCModel("CRC-8/DVB-S2", 8, 0xD5, 0,
                false, false, 0, 0xBC, 0);
        CRCModel crc8ebu = new CRCModel("CRC-8/EBU", 8, 0x1D, 0xFF,
                true, true, 0, 0x97, 0);
        CRCModel crc8gsma = new CRCModel("CRC-8/GSM-A", 8, 0x1D, 0,
                false, false, 0, 0x37, 0);
        CRCModel crc8gsmb = new CRCModel("CRC-8/GSM-B", 8, 0x49, 0,
                false, false, 0xFF, 0x94, 0x53);
        CRCModel crc8icode = new CRCModel("CRC-8/I-CODE", 8, 0x1D, 0xFD,
                false, false, 0, 0x7E, 0);
        CRCModel crc8itu = new CRCModel("CRC-8/ITU", 8, 0x07, 0,
                false, false, 0x55, 0xA1, 0xAC);
        CRCModel crc8lte = new CRCModel("CRC-8/LTE", 8, 0x9B, 0,
                false, false, 0, 0xEA, 0);
        CRCModel crc8maxim = new CRCModel("CRC-8/MAXIM", 8, 0x31, 0,
                true, true, 0, 0xA1, 0);
        CRCModel crc8opensafety = new CRCModel("CRC-8/OPENSAFETY", 8, 0x2F, 0,
                false, false, 0, 0x3E, 0);
        CRCModel crc8rohc = new CRCModel("CRC-8/ROHC", 8, 0x07, 0xFF,
                true, true, 0, 0xD0, 0);
        CRCModel crc8saej1850 = new CRCModel("CRC-8/SAE-J1850", 8, 0x1D, 0xFF,
                false, false, 0xFF, 0x4B, 0xC4);
        CRCModel crc8wcdma = new CRCModel("CRC-8/WCDMA", 8, 0x9B, 0,
                true, true, 0, 0x25, 0);

        //CRC-7
        CRCModel crc7 = new CRCModel("CRC-7", 7, 0x09, 0,
                false, false, 0, 0x75, 0);
        CRCModel crc7rohc = new CRCModel("CRC-7/ROHC", 7, 0x4F, 0x7F,
                true, true, 0, 0x53, 0);
        CRCModel crc7umts = new CRCModel("CRC-7/UMTS", 7, 0x45, 0,
                false, false, 0, 0x61, 0);

        //CRC-6
        CRCModel crc6cdma2000A = new CRCModel("CRC-6/CDMA2000-A", 6, 0x27, 0x3F,
                false, false, 0, 0x0D, 0);
        CRCModel crc6cdma2000B = new CRCModel("CRC-6/CDMA2000-A", 6, 0x07, 0x3F,
                false, false, 0, 0x3B, 0);
        CRCModel crc6darc = new CRCModel("CRC-6/DARC", 6, 0x19, 0,
                true, true, 0, 0x26, 0);
        CRCModel crc6gsm = new CRCModel("CRC-6/GSM", 6, 0x2F, 0,
                false, false, 0x3F, 0x13, 0x3A);
        CRCModel crc6itu = new CRCModel("CRC-6/ITU", 6, 0x03, 0,
                true, true, 0, 0x06, 0);

        //CRC-5
        CRCModel crc5epc = new CRCModel("CRC-5/EPC", 5, 0x09, 0x09,
                false, false, 0, 0, 0);
        CRCModel crc5itu = new CRCModel("CRC-5/ITU", 5, 0x15, 0,
                true, true, 0, 0x07, 0);
        CRCModel crc5usb = new CRCModel("CRC-5/USB", 5, 0x05, 0x1F,
                true, true, 0x1F, 0x19, 0x06);

        //CRC-4
        CRCModel crc4interlaken = new CRCModel("CRC-4/INTERLAKEN", 4, 0x3, 0xF,
                false, false, 0xF, 0xB, 0x2);
        CRCModel crc4itu = new CRCModel("CRC-4/ITU", 4, 0x3, 0,
                true, true, 0, 0x7, 0);

        //CRC-3
        CRCModel crc3gsm = new CRCModel("CRC-3/GSM", 3, 0x3, 0,
                false, false, 0x7, 0x4, 0x2);
        CRCModel crc3rohc = new CRCModel("CRC-3/ROHC", 3, 0x3, 0x7,
                true, true, 0, 0x6, 0);


        return Arrays.asList(
                crc32, crc32autosar, crc32bzip2, crc32c, crc32d, crc32mpeg2, crc32posix, crc32q, jamcrc, xfer,
                crc31philips,
                crc30cdma,
                crc24, crc24ble, crc24flexyayA, crc24flexyayB, crc24interlaken, crc24lteA, crc24lteB,
                crc21canfd,
                crc17canfd,
                crc16, crc16augccitt, crc16buypass, ccittfalse, crc16cdma2000, crc16cms, crc16dds110, crc16dectr,
                crc16dectx, crc16dnp, crc16en13757, crc16genibus, crc16gsm, crc16lj1200, crc16maxim, crc16mrcf4xx,
                crc16opensafetya, crc16opensafetyb, crc16profibus, crc16riello, crc16t10dif, crc16teledisk,
                crc16tms37157, crc16usb, crca, kermit, modbus, x25, xmodem,
                crc15, crc15mpt1327,
                crc14darc, crc14gsm,
                crc13bbc,
                crc12cdma2000, crc12dect, crc12gsm, crc12umts,
                crc11, crc11umts,
                crc10, crc10cdma2000, crc10gsm,
                crc8, crc8autosar, crc8bluetooth, crc8cdma2000, crc8darc, crc8dvbs2, crc8ebu, crc8gsma, crc8gsmb,
                crc8icode, crc8itu, crc8lte, crc8maxim, crc8opensafety, crc8rohc, crc8saej1850, crc8wcdma,
                crc7, crc7rohc, crc7umts,
                crc6cdma2000A, crc6cdma2000B, crc6darc, crc6gsm, crc6itu,
                crc5epc, crc5itu, crc5usb,
                crc4interlaken, crc4itu,
                crc3gsm, crc3rohc);
    }
}