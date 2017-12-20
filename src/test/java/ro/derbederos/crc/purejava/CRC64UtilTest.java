package ro.derbederos.crc.purejava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;

import static java.lang.Long.reverse;
import static java.lang.Long.toHexString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC64UtilTest {
    private CRCModel crcModel;

    public CRC64UtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testReflectedVsUnreflected() {
        long poly = (int) crcModel.getPoly();
        long reflectedPoly = reverse(poly);
        long[] lookupTableReflected = CRC64Util.initLookupTableReflected(reflectedPoly);
        long[] lookupTableUnreflected = CRC64Util.initLookupTableUnreflected(poly);
        for (int i = 0; i < 256; i++) {
            long expected = reverse(lookupTableReflected[i]);
            long actual = lookupTableUnreflected[(int) (reverse(i) >>> 56)];
            assertEquals("at iteration " + i, toHexString(expected), toHexString(actual));
        }
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
