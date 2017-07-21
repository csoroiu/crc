package ro.derbederos.crc;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CRCFactoryTest {

    @Test
    public void testModelsSize() {
        CRCModel[] crcModels = CRCFactory.getDefinedModels();
        assertEquals(101, crcModels.length);
    }

    @Test
    public void testGetModelByName() {
        CRCModel crcModel = CRCFactory.getModel("CRC-32");
        assertNotNull(crcModel);
    }

    @Test
    @Ignore
    public void testGetModelByAlias() {
        CRCModel crcModel = CRCFactory.getModel("PKZIP");
        assertNotNull(crcModel);
    }
}