package ro.derbederos.crc;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC16Test extends AbstractCRCTest {

    public CRC16Test(CRCModel crcModel) {
        super(crcModel, CRC16Test::createCrc);
    }

    private static CRC createCrc(CRCModel crcModel) {
        return new CRC16(
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() == 16)
                .collect(Collectors.toList());
    }
}
