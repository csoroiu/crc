package ro.derbederos.crc.purejava;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.AbstractCRCTest;
import ro.derbederos.crc.CRC;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC64GenericTest extends AbstractCRCTest {

    public CRC64GenericTest(CRCModel crcModel) {
        super(crcModel, CRC64GenericTest::createCrc);
    }

    private static CRC createCrc(CRCModel crcModel) {
        return new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 64)
                .collect(Collectors.toList());
    }
}