package ro.derbederos.crc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CRCFactoryTest {

    @Test
    public void testModelsSize() {
        CRCModel[] crcModels = CRCFactory.getDefinedModels();
        assertEquals(101, crcModels.length);
    }
}