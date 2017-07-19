package ro.derbederos.crc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class CRC16UtilTest {
    private CRCModel crcModel;

    public CRC16UtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testFastVsSlowInitLookupTableReflected() {
        short[] lookupTableSlow = CRC16Util.initLookupTableReflected((short) crcModel.getPoly());
        short[] lookupTableFast = CRC16Util.fastInitLookupTableReflected((short) crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Test
    public void testFastVsSlowInitLookupTableUnreflected() {
        short[] lookupTableSlow = CRC16Util.initLookupTableUnreflected((short) crcModel.getPoly());
        short[] lookupTableFast = CRC16Util.fastInitLookupTableUnreflected((short) crcModel.getPoly());
        assertArrayEquals(lookupTableFast, lookupTableSlow);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.asList(CRCFactory.getDefinedModels());
    }
}
