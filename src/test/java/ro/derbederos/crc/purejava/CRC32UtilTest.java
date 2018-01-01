/*
 * Copyright (c) 2017-2018 Claudiu Soroiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.derbederos.crc.purejava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.reverse;
import static java.lang.Integer.toHexString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CRC32UtilTest {
    private CRCModel crcModel;

    public CRC32UtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
    }

    @Test
    public void testReflectedVsUnreflected() {
        int poly = (int) crcModel.getPoly();
        int reflectedPoly = reverse(poly);
        int[] lookupTableReflected = CRC32Util.initLookupTableReflected(reflectedPoly);
        int[] lookupTableUnreflected = CRC32Util.initLookupTableUnreflected(poly);
        for (int i = 0; i < 256; i++) {
            int expected = reverse(lookupTableReflected[i]);
            int actual = lookupTableUnreflected[reverse(i) >>> 24];
            assertEquals("at iteration " + i, toHexString(expected), toHexString(actual));
        }
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
