package ro.derbederos.crc;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC64SlicingBy8Test extends AbstractCRCTest {

    public CRC64SlicingBy8Test(CRCModel crcModel) {
        super(crcModel, CRC64SlicingBy8Test::createCrc);
    }

    private static CRC createCrc(CRCModel crcModel) {
        return new CRC64SlicingBy8(
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() == 64)
                .collect(Collectors.toList());
    }
}
