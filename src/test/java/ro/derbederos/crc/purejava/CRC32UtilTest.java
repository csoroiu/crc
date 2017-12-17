package ro.derbederos.crc.purejava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class CRC32UtilTest {
    private CRCModel crcModel;

    public CRC32UtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testFastVsSlowInitLookupTableReflected() {
        int[] lookupTableSlow = CRC32Util.initLookupTableReflected((int) crcModel.getPoly());
        int[] lookupTableFast = CRC32Util.fastInitLookupTableReflected((int) crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Test
    public void testFastVsSlowInitLookupTableUnreflected() {
        int[] lookupTableSlow = CRC32Util.initLookupTableUnreflected((int) crcModel.getPoly());
        int[] lookupTableFast = CRC32Util.fastInitLookupTableUnreflected((int) crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.asList(CRCFactory.getDefinedModels());
    }
}
