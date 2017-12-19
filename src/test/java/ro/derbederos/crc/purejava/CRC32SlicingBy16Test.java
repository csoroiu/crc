package ro.derbederos.crc.purejava;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.AbstractCRCTest;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC32SlicingBy16Test extends AbstractCRCTest {

    public CRC32SlicingBy16Test(CRCModel crcModel) {
        super(crcModel, CRC32SlicingBy16::new);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 32)
                .collect(Collectors.toList());
    }
}
