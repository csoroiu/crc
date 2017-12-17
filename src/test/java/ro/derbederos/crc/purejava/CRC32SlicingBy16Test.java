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
public class CRC32SlicingBy16Test extends AbstractCRCTest {

    public CRC32SlicingBy16Test(CRCModel crcModel) {
        super(crcModel, CRC32SlicingBy16Test::createCrc);
    }

    private static CRC createCrc(CRCModel crcModel) {
        return new CRC32SlicingBy16(
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() == 32)
                .collect(Collectors.toList());
    }
}
