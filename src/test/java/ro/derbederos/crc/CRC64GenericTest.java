package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
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

    @Test
    public void testResidue() {
        Checksum checksum = new CRC64Generic(
                crcParameters.getWidth(),
                crcParameters.getPoly(),
                0,
                crcParameters.getRefIn(),
                crcParameters.getRefOut(),
                0);

        long input = crcParameters.getXorOut();
        if (crcParameters.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Long.reverse(input) >>> 64 - roundToByte(crcParameters.getWidth());
        }
        byte[] newByte = longToBytes(input);
        checksum.update(newByte, 0, newByte.length);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcParameters.getResidue()), Long.toHexString(residue));
    }

    private static int roundToByte(int bits) {
        return (bits + 7) / 8 * 8;
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCParameters> getCRCParameters() {
        //CRC-64
        CRCParameters crc64 = new CRCParameters("CRC-64", 64, 0x42F0E1EBA9EA3693L, 0,
                false, false, 0, 0x6c40df5f0b497347L, 0);
        CRCParameters crc64goiso = new CRCParameters("CRC-64/GO-ISO", 64, 0x000000000000001bL, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0xb90956c775a41001L, 0x5300000000000000L);
        CRCParameters crc64we = new CRCParameters("CRC-64/WE", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                false, false, 0xFFFFFFFFFFFFFFFFL, 0x62ec59e3f1a4f00aL, 0xfcacbebd5931a992L);
        CRCParameters crc64xz = new CRCParameters("CRC-64/XZ", 64, 0x42F0E1EBA9EA3693L, 0xFFFFFFFFFFFFFFFFL,
                true, true, 0xFFFFFFFFFFFFFFFFL, 0x995dc9bbdf1939faL, 0x49958c9abd7d353fL);

        //CRC-40/GSM
        CRCParameters crc40GSM = new CRCParameters("CRC-40/GSM", 40, 0x0004820009L, 0,
                false, false, 0xFFFFFFFFFFL, 0xD4164FC646L, 0xC4FF8071FFL);

        //CRC-32
        CRCParameters crc32 = new CRCParameters("CRC-32", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0xcbf43926L, 0xdebb20e3L);
        CRCParameters crc32autosar = new CRCParameters("CRC-32/AUTOSAR", 32, 0xf4acfb13, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0x1697d06aL, 0x904cddbfL);
        CRCParameters crc32bzip2 = new CRCParameters("CRC-32/BZIP2", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                false, false, 0xFFFFFFFFL, 0xfc891918L, 0xc704dd7bL);
        CRCParameters crc32c = new CRCParameters("CRC-32C", 32, 0x1EDC6F41L, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0xE3069283L, 0xB798B438L);
        CRCParameters crc32d = new CRCParameters("CRC-32D", 32, 0xA833982BL, 0xFFFFFFFFL,
                true, true, 0xFFFFFFFFL, 0x87315576L, 0x45270551L);
        CRCParameters crc32mpeg2 = new CRCParameters("CRC-32/MPEG-2", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                false, false, 0, 0x0376E6E7L, 0);
        CRCParameters crc32posix = new CRCParameters("CRC-32/POSIX", 32, 0x04C11DB7L, 0,
                false, false, 0xFFFFFFFFL, 0x765E7680L, 0xC704DD7BL);
        CRCParameters crc32q = new CRCParameters("CRC-32Q", 32, 0x814141ABL, 0,
                false, false, 0, 0x3010BF7FL, 0);
        CRCParameters jamcrc = new CRCParameters("JAMCRC", 32, 0x04C11DB7L, 0xFFFFFFFFL,
                true, true, 0, 0x340BC6D9L, 0);
        CRCParameters xfer = new CRCParameters("XFER", 32, 0x000000AFL, 0,
                false, false, 0, 0xBD0BE338L, 0);

        //CRC-31
        CRCParameters crc31philips = new CRCParameters("CRC-31/PHILIPS", 31, 0x04C11DB7L, 0x7FFFFFFFL,
                false, false, 0x7FFFFFFFL, 0x0CE9E46CL, 0x4EAF26F1L);

        //CRC-30
        CRCParameters crc30cdma = new CRCParameters("CRC-30/CDMA", 30, 0x2030B9C7L, 0x3FFFFFFFL,
                false, false, 0x3FFFFFFFL, 0x04C34ABFL, 0X34EFA55AL);

        //CRC-24
        CRCParameters crc24 = new CRCParameters("CRC-24", 24, 0x864CFBL, 0xB704CEL,
                false, false, 0, 0x21CF02L, 0);
        CRCParameters crc24ble = new CRCParameters("CRC-24/BLE", 24, 0x00065BL, 0x555555L,
                true, true, 0, 0xC25A56L, 0);
        CRCParameters crc24flexyayA = new CRCParameters("CRC-24/FLEXRAY-A", 24, 0x5D6DCBL, 0xFEDCBAL,
                false, false, 0, 0x7979BDL, 0);
        CRCParameters crc24flexyayB = new CRCParameters("CRC-24/FLEXRAY-B", 24, 0x5D6DCBL, 0xABCDEFL,
                false, false, 0, 0x1F23B8L, 0);
        CRCParameters crc24interlaken = new CRCParameters("CRC-24/INTERLAKEN", 24, 0x328B63L, 0xFFFFFFL,
                false, false, 0xFFFFFFL, 0xB4F3E6L, 0x144E63L);
        CRCParameters crc24lteA = new CRCParameters("CRC-24/LTE-A", 24, 0x864CFBL, 0,
                false, false, 0, 0xCDE703L, 0);
        CRCParameters crc24lteB = new CRCParameters("CRC-24/LTE-B", 24, 0x800063L, 0,
                false, false, 0, 0x23EF52L, 0);

        //CRC-21
        CRCParameters crc21canfd = new CRCParameters("CRC-21/CAN-FD", 21, 0x102899L, 0,
                false, false, 0, 0x0ED841L, 0);

        //CRC-17
        CRCParameters crc17canfd = new CRCParameters("CRC-17/CAN-FD", 17, 0x1685BL, 0,
                false, false, 0, 0x04F03L, 0);

        //CRC-16
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

        //CRC-15
        CRCParameters crc15 = new CRCParameters("CRC-15", 15, 0x4599, 0,
                false, false, 0, 0x059E, 0);
        CRCParameters crc15mpt1327 = new CRCParameters("CRC-15/MPT1327", 15, 0x6815, 0,
                false, false, 0x0001, 0x2566, 0x6815);

        //CRC-14
        CRCParameters crc14darc = new CRCParameters("CRC-14/DARC", 14, 0x0805, 0,
                true, true, 0, 0x082D, 0);

        CRCParameters crc14gsm = new CRCParameters("CRC-14/GSM", 14, 0x202D, 0,
                false, false, 0x3FFF, 0x30AE, 0x031E);

        //CRC-13
        CRCParameters crc13bbc = new CRCParameters("CRC-13/BBC", 13, 0x1CF5, 0,
                false, false, 0, 0x04FA, 0);

        //CRC-12
        CRCParameters crc12cdma2000 = new CRCParameters("CRC-12/CDMA2000", 12, 0xF13, 0xFFF,
                false, false, 0, 0xD4D, 0);
        CRCParameters crc12dect = new CRCParameters("CRC-12/DECT", 12, 0x80F, 0,
                false, false, 0, 0xF5B, 0);
        CRCParameters crc12gsm = new CRCParameters("CRC-12/GSM", 12, 0xD31, 0,
                false, false, 0xFFF, 0xB34, 0x178);
        CRCParameters crc12umts = new CRCParameters("CRC-12/UMTS", 12, 0x80F, 0,
                false, true, 0, 0xDAF, 0);

        //CRC-11
        CRCParameters crc11 = new CRCParameters("CRC-11", 11, 0x385, 0x01A,
                false, false, 0, 0x5A3, 0);
        CRCParameters crc11umts = new CRCParameters("CRC-11/UMTS", 11, 0x307, 0,
                false, false, 0, 0x061, 0);

        //CRC-10
        CRCParameters crc10 = new CRCParameters("CRC-10", 10, 0x233, 0,
                false, false, 0, 0x199, 0);
        CRCParameters crc10cdma2000 = new CRCParameters("CRC-10/CDMA2000", 10, 0x3D9, 0x3FF,
                false, false, 0, 0x233, 0);
        CRCParameters crc10gsm = new CRCParameters("CRC-10/GSM", 10, 0x175, 0,
                false, false, 0x3FF, 0x12A, 0x0C6);

        //CRC-8
        CRCParameters crc8 = new CRCParameters("CRC-8", 8, 0x07, 0,
                false, false, 0, 0xF4, 0);
        CRCParameters crc8autosar = new CRCParameters("CRC-8/AUTOSAR", 8, 0x2F, 0xFF,
                false, false, 0xFF, 0xDF, 0x42);
        CRCParameters crc8bluetooth = new CRCParameters("CRC-8/BLUETOOTH", 8, 0xA7, 0,
                true, true, 0, 0x26, 0);
        CRCParameters crc8cdma2000 = new CRCParameters("CRC-8/CDMA2000", 8, 0x9B, 0xFF,
                false, false, 0, 0xDA, 0);
        CRCParameters crc8darc = new CRCParameters("CRC-8/DARC", 8, 0x39, 0,
                true, true, 0, 0x15, 0);
        CRCParameters crc8dvbs2 = new CRCParameters("CRC-8/DVB-S2", 8, 0xD5, 0,
                false, false, 0, 0xBC, 0);
        CRCParameters crc8ebu = new CRCParameters("CRC-8/EBU", 8, 0x1D, 0xFF,
                true, true, 0, 0x97, 0);
        CRCParameters crc8gsma = new CRCParameters("CRC-8/GSM-A", 8, 0x1D, 0,
                false, false, 0, 0x37, 0);
        CRCParameters crc8gsmb = new CRCParameters("CRC-8/GSM-B", 8, 0x49, 0,
                false, false, 0xFF, 0x94, 0x53);
        CRCParameters crc8icode = new CRCParameters("CRC-8/I-CODE", 8, 0x1D, 0xFD,
                false, false, 0, 0x7E, 0);
        CRCParameters crc8itu = new CRCParameters("CRC-8/ITU", 8, 0x07, 0,
                false, false, 0x55, 0xA1, 0xAC);
        CRCParameters crc8lte = new CRCParameters("CRC-8/LTE", 8, 0x9B, 0,
                false, false, 0, 0xEA, 0);
        CRCParameters crc8maxim = new CRCParameters("CRC-8/MAXIM", 8, 0x31, 0,
                true, true, 0, 0xA1, 0);
        CRCParameters crc8opensafety = new CRCParameters("CRC-8/OPENSAFETY", 8, 0x2F, 0,
                false, false, 0, 0x3E, 0);
        CRCParameters crc8rohc = new CRCParameters("CRC-8/ROHC", 8, 0x07, 0xFF,
                true, true, 0, 0xD0, 0);
        CRCParameters crc8saej1850 = new CRCParameters("CRC-8/SAE-J1850", 8, 0x1D, 0xFF,
                false, false, 0xFF, 0x4B, 0xC4);
        CRCParameters crc8wcdma = new CRCParameters("CRC-8/WCDMA", 8, 0x9B, 0,
                true, true, 0, 0x25, 0);

        //CRC-7
        CRCParameters crc7 = new CRCParameters("CRC-7", 7, 0x09, 0,
                false, false, 0, 0x75, 0);
        CRCParameters crc7rohc = new CRCParameters("CRC-7/ROHC", 7, 0x4F, 0x7F,
                true, true, 0, 0x53, 0);
        CRCParameters crc7umts = new CRCParameters("CRC-7/UMTS", 7, 0x45, 0,
                false, false, 0, 0x61, 0);

        //CRC-6
        CRCParameters crc6cdma2000A = new CRCParameters("CRC-6/CDMA2000-A", 6, 0x27, 0x3F,
                false, false, 0, 0x0D, 0);
        CRCParameters crc6cdma2000B = new CRCParameters("CRC-6/CDMA2000-A", 6, 0x07, 0x3F,
                false, false, 0, 0x3B, 0);
        CRCParameters crc6darc = new CRCParameters("CRC-6/DARC", 6, 0x19, 0,
                true, true, 0, 0x26, 0);
        CRCParameters crc6gsm = new CRCParameters("CRC-6/GSM", 6, 0x2F, 0,
                false, false, 0x3F, 0x13, 0x3A);
        CRCParameters crc6itu = new CRCParameters("CRC-6/ITU", 6, 0x03, 0,
                true, true, 0, 0x06, 0);

        //CRC-5
        CRCParameters crc5epc = new CRCParameters("CRC-5/EPC", 5, 0x09, 0x09,
                false, false, 0, 0, 0);
        CRCParameters crc5itu = new CRCParameters("CRC-5/ITU", 5, 0x15, 0,
                true, true, 0, 0x07, 0);
        CRCParameters crc5usb = new CRCParameters("CRC-5/USB", 5, 0x05, 0x1F,
                true, true, 0x1F, 0x19, 0x06);

        //CRC-4
        CRCParameters crc4interlaken = new CRCParameters("CRC-4/INTERLAKEN", 4, 0x3, 0xF,
                false, false, 0xF, 0xB, 0x2);
        CRCParameters crc4itu = new CRCParameters("CRC-4/ITU", 4, 0x3, 0,
                true, true, 0, 0x7, 0);

        //CRC-3
        CRCParameters crc3gsm = new CRCParameters("CRC-3/GSM", 3, 0x3, 0,
                false, false, 0x7, 0x4, 0x2);
        CRCParameters crc3rohc = new CRCParameters("CRC-3/ROHC", 3, 0x3, 0x7,
                true, true, 0, 0x6, 0);


        return Arrays.asList(crc64, crc64goiso, crc64we, crc64xz,
                crc40GSM,
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