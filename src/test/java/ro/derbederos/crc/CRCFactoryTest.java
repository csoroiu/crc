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

package ro.derbederos.crc;

import org.junit.Ignore;
import org.junit.Test;
import ro.derbederos.crc.purejava.crc32.CRC32_JAMCRC;

import java.util.zip.Checksum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CRCFactoryTest {

    @Test
    public void testModelsSize() {
        CRCModel[] crcModels = CRCFactory.getDefinedModels();
        assertEquals(103, crcModels.length);
    }

    @Test
    public void testGetModelByName() {
        CRCModel crcModel = CRCFactory.getModel("CRC-32");
        assertNotNull(crcModel);
    }

    @Test
    @Ignore //FIXME support for alliases not implemented yet
    public void testGetModelByAlias() {
        CRCModel crcModel = CRCFactory.getModel("PKZIP");
        assertNotNull(crcModel);
    }

    @Test
    public void testGetCrc32BZip() {
        Checksum crc = CRCFactory.getCRC("CRC-32/BZIP2");
        assertTrue(crc instanceof CRC);
    }

    @Test
    public void testGetCrc32() {
        Checksum crc = CRCFactory.getCRC("CRC-32");
        assertFalse(crc instanceof CRC);
    }

    @Test
    public void testGetCrc32C() {
        Checksum crc = CRCFactory.getCRC("CRC-32C");
        assertFalse(crc instanceof CRC);
    }

    @Test
    public void testGetJAMCRC() {
        Checksum crc = CRCFactory.getCRC("JAMCRC");
        assertTrue(crc instanceof CRC32_JAMCRC);
    }
}