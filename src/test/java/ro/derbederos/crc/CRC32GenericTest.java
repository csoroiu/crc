package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static ro.derbederos.crc.Util.longToBytes;
import static ro.derbederos.crc.Util.roundToByte;

@RunWith(Parameterized.class)
public class CRC32GenericTest extends AbstractCRCTest {

    public CRC32GenericTest(CRCModel crcModel) {
        super(crcModel, CRC32GenericTest::createCrc);
    }

    private static Checksum createCrc(CRCModel crcModel) {
        return new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
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

        long input = crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Long.reverse(input) >>> 64 - roundToByte(crcModel.getWidth());
        }
        byte[] newByte = longToBytes(input);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 8 - len, len);
        long residue = checksum.getValue();
        assertEquals(Long.toHexString(crcModel.getResidue()), Long.toHexString(residue));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 32)
                .collect(Collectors.toList());
    }
}
