package ro.derbederos.crc;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

@RunWith(Parameterized.class)
public class CRC64SlicingBy16Test extends AbstractCRCTest {

    public CRC64SlicingBy16Test(CRCModel crcModel) {
        super(crcModel, CRC64SlicingBy16Test::createCrc);
    }

    private static Checksum createCrc(CRCModel crcModel) {
        return new CRC64SlicingBy16(
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
