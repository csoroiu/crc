package ro.derbederos.crc.purejava;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import static java.lang.Long.toHexString;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GfUtilTest {

    private static final byte[] testInput = "alabalaportocala".getBytes();
    private static final byte[] testInputA = "alabal".getBytes();
    private static final byte[] testInputB = "aportocala".getBytes();
    private static final byte[] testInputLong = new byte[1024];

    static {
        long SEED = 0x12fed1a214ecbd00L;
        Random r = new Random(SEED);
        r.nextBytes(testInputLong);
    }

    private final CRCModel crcModel;
    private final Checksum crc;
    private final GfUtil gfUtil;

    public GfUtilTest(CRCModel crcModel) {
        this.crcModel = crcModel;
        long poly = reflect(crcModel, crcModel.getPoly());
        long init = reflect(crcModel, crcModel.getInit());
        long xorOut = reflect(crcModel, crcModel.getXorOut());
        gfUtil = new GfUtil32((int) poly, crcModel.getWidth(), (int) init, (int) xorOut);
        crc = CRCFactory.getCRC(crcModel);
    }

    private static long computeCrcOfCrc(GfUtil gfUtil, CRCModel crcModel) {
        long crcOfCrc = gfUtil.getCrcOfCrc();
        if (!crcModel.getRefOut()) {
            crcOfCrc = reflect(crcModel, crcOfCrc);
        }
        return crcOfCrc ^ crcModel.getXorOut();
    }

    @Test
    public void testResidue() {
        long crcOfCrc = computeCrcOfCrc(gfUtil, crcModel);
        Assert.assertEquals(toHexString(crcModel.getResidue()), toHexString(crcOfCrc));
    }

    @Test
    public void testAppendCrcOfZeroes() {
        crc.update(testInputLong);
        long crcInitial = crc.getValue();
        for (int i = 0; i < 1024; i++) {
            crc.update(0);
        }
        long crcOfZeroesExpected = crc.getValue();
        if (!crcModel.getRefOut()) {
            crcInitial = reflect(crcModel, crcInitial);
            crcOfZeroesExpected = reflect(crcModel, crcOfZeroesExpected);
        }
        long crcOfZeroesActual = gfUtil.crcOfZeroes(1024, crcInitial);
        Assert.assertEquals(toHexString(crcOfZeroesExpected), toHexString(crcOfZeroesActual));
    }

    @Test
    public void testCombine() {
        long crcExpected = computeCrc(crc, testInput, 0, testInput.length);
        long crcA = computeCrc(crc, testInputA, 0, testInputA.length);
        long crcB = computeCrc(crc, testInputB, 0, testInputB.length);

        long crcActual = concatenate(crcA, crcB, testInputB.length);

        Assert.assertEquals(toHexString(crcExpected), toHexString(crcActual));
    }

    private long concatenate(long crcA, long crcB, long bytes_b) {
        if (!crcModel.getRefOut()) {
            crcA = reflect(crcModel, crcA);
            crcB = reflect(crcModel, crcB);
        }

        long crcActual = gfUtil.concatenate(crcA, crcB, bytes_b);

        if (!crcModel.getRefOut()) {
            crcActual = reflect(crcModel, crcActual);
        }
        return crcActual;
    }

    @Test
    public void testCombineLong() {
        long crcExpected = computeCrc(crc, testInputLong, 0, testInputLong.length);

        for (int i = 0; i < testInputLong.length; i++) {
            int bytes_b = testInputLong.length - i;

            long crcA = computeCrc(crc, testInputLong, 0, i);
            long crcB = computeCrc(crc, testInputLong, i, bytes_b);
            long crcActual = concatenate(crcA, crcB, bytes_b);

            assertEquals("at iteration " + i, toHexString(crcExpected), toHexString(crcActual));
        }
    }

    private static long computeCrc(Checksum checksum, byte[] bytes, int offset, int len) {
        checksum.reset();
        checksum.update(bytes, offset, len);
        return checksum.getValue();
    }

    private static long reflect(CRCModel crcModel, long crc) {
        return Long.reverse(crc) >>> (64 - crcModel.getWidth());
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 64)
                .collect(Collectors.toList());
    }
}
