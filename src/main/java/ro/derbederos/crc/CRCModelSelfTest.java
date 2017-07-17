package ro.derbederos.crc;

import java.util.zip.Checksum;

import static ro.derbederos.crc.Util.longToBytes;
import static ro.derbederos.crc.Util.roundToByte;

public class CRCModelSelfTest {
    private static final byte[] testInput = "123456789".getBytes();

    public static void validateCRCModelParams(CRCModel crcModel) {
        long topBit = 1L << (crcModel.getWidth() - 1);
        long mask = (topBit << 1) - 1;

        if ((crcModel.getPoly() & ~mask) != 0) {
            throw new IllegalArgumentException("Poly width too large.");
        }
        if ((crcModel.getInit() & ~mask) != 0) {
            throw new IllegalArgumentException("Init width too large.");
        }
        if ((crcModel.getXorOut() & ~mask) != 0) {
            throw new IllegalArgumentException("XorOut width too large.");
        }
        if (crcModel.getRefIn() && !crcModel.getRefOut()) {
            throw new IllegalArgumentException("Model seems to be strange (refIn=true, refOut=false).");
        }
        if (crcModel.getWidth() < 3 || crcModel.getWidth() > 64) {
            throw new IllegalArgumentException("width < 3 and width > 64 unsupported in current version.");
        }
    }

    public static boolean validateCRCValue(CRCModel crcModel) {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        checksum.reset();
        checksum.update(testInput, 0, testInput.length);
        long value = checksum.getValue();
        return value == crcModel.getCheck();
    }

    public static boolean validateCRCResidue(CRCModel crcModel) {
        Checksum checksum = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                0,
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                0);
        long input = crcModel.getXorOut();
        if (crcModel.getRefOut()) {
            //TODO: hack, fixes issue with CRC-5/USB
            input = Long.reverse(input) >>> 64 - roundToByte(crcModel.getWidth());
        }
        byte[] newByte = longToBytes(input);
        int len = roundToByte(crcModel.getWidth()) / 8;
        checksum.update(newByte, 8 - len, len);
        long residue = checksum.getValue();
        return residue == crcModel.getResidue();
    }
}
