package ro.derbederos.crc.purejava;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import static java.lang.Long.toHexString;

@RunWith(Parameterized.class)
public class ConcatenateTest {

    private final CRCModel crcModel;
    private final Checksum crc;
    private final GfUtil64 gfUtil64;

    public ConcatenateTest(CRCModel crcModel) {
        this.crcModel = crcModel;
        long poly = reflect(crcModel.getPoly());
        long init = reflect(crcModel.getInit());
        long xorOut = reflect(crcModel.getXorOut());
        gfUtil64 = new GfUtil64(poly, crcModel.getWidth(), init, xorOut);
        crc = CRCFactory.getCRC(crcModel);
    }


    @Test
    public void testResidue() {
        long crcOfCrc = gfUtil64.getCrcOfCrc();
        if (!crcModel.getRefOut()) {
            crcOfCrc = reflect(crcOfCrc);
        }
        Assert.assertEquals(toHexString(crcModel.getResidue()), toHexString(crcOfCrc ^ crcModel.getXorOut()));
    }

    @Test
    public void testCombine() {
        crc.reset();
        crc.update("alabalaportocala".getBytes());
        long crcExpected = crc.getValue();
        crc.reset();
        crc.update("alabala".getBytes());
        long crc1 = crc.getValue();
        crc.reset();
        crc.update("portocala".getBytes());
        long crc2 = crc.getValue();
        long crcActual;

        if (!crcModel.getRefOut()) {
            crc1 = reflect(crc1);
            crc2 = reflect(crc2);
        }

        crcActual = gfUtil64.concatenate(crc1, crc2, 9);

        if (!crcModel.getRefOut()) {
            crcActual = reflect(crcActual);
        }

        Assert.assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    private long reflect(long crc) {
        return Long.reverse(crc) >>> (64 - crcModel.getWidth());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
//                .filter(crcModel -> crcModel.getWidth() <= 32)
//                .filter(crcModel -> crcModel.getWidth() == 32)
//                .filter(crcModel -> crcModel.getWidth() >= 32)
//                .filter(crcModel -> crcModel.getWidth() % 8 == 0)
//                .filter(crcModel -> crcModel.getXorOut() == crcModel.getInit())
                .collect(Collectors.toList());
    }
}
