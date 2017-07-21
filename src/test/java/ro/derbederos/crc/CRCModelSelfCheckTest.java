package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CRCModelSelfCheckTest {
    private final CRCModel crcModel;

    public CRCModelSelfCheckTest(CRCModel crcModel) {
        this.crcModel = crcModel;
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
        assertTrue(CRCModelSelfCheck.validateCRCResidue(crcModel));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 64)
                .collect(Collectors.toList());
    }
}
