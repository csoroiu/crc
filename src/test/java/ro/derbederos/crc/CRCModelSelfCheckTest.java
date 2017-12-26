package ro.derbederos.crc;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.purejava.CRC64SlicingBy16;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CRCModelSelfCheckTest {
    private final CRCModel crcModel;
    private final CRC crc;

    public CRCModelSelfCheckTest(CRCModel crcModel) {
        this.crcModel = crcModel;
        this.crc = new CRC64SlicingBy16(crcModel);
    }

    @Test
    public void testValidateCRCModelParams() {
        CRCModelSelfCheck.validateCRCModelParams(crcModel);
    }

    @Test
    public void testValidateCRCValue() {
        assertTrue(CRCModelSelfCheck.validateCRCValue(crcModel));
    }

    @Test
    public void testValidateCRCResidue() {
        assertEquals(Long.toHexString(crcModel.getResidue()),
                Long.toHexString(CRCModelSelfCheck.computeResidue(crc, crcModel)));
    }

    @Test
    @Ignore
    public void testInit() {
        if (crcModel.getInit() != 0) {
            long mask = 1L << crcModel.getWidth() - 1;
            mask |= mask - 1;
            assertEquals(Long.toHexString(mask), Long.toHexString(crcModel.getInit()));
        }
    }

    @Test
    @Ignore
    public void testXorOut() {
        if (crcModel.getXorOut() != 0) {
            long mask = 1L << crcModel.getWidth() - 1;
            mask |= mask - 1;
            assertEquals(Long.toHexString(mask), Long.toHexString(crcModel.getXorOut()));
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 64)
                .collect(Collectors.toList());
    }
}
