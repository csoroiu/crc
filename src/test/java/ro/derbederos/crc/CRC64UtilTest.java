package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class CRC64UtilTest {
    private CRCModel crcModel;

    public CRC64UtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testFastVsSlowInitLookupTableReflected() {
        long[] lookupTableSlow = CRC64Util.initLookupTableReflected(crcModel.getPoly());
        long[] lookupTableFast = CRC64Util.fastInitLookupTableReflected(crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Test
    public void testFastVsSlowInitLookupTableUnreflected() {
        long[] lookupTableSlow = CRC64Util.initLookupTableUnreflected(crcModel.getPoly());
        long[] lookupTableFast = CRC64Util.fastInitLookupTableUnreflected(crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.asList(CRCFactory.getDefinedModels());
    }
}
