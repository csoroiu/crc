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