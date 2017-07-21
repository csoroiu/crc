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
public class CRC64GenericTest extends AbstractCRCTest {

    public CRC64GenericTest(CRCModel crcModel) {
        super(crcModel, CRC64GenericTest::createCrc);
    }

    private static Checksum createCrc(CRCModel crcModel) {
        return new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
    }

    @Test
    public void testResidue() {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
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
                .filter(crcModel -> crcModel.getWidth() <= 64)
                .collect(Collectors.toList());
    }
}
