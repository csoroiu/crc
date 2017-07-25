package ro.derbederos.crc;

import java.nio.ByteOrder;

import static ro.derbederos.crc.Util.longToBytes;

public class CRCModelSelfCheck {
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
        CRC crc = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        crc.update(testInput, 0, testInput.length);
        long value = crc.getValue();
        return value == crcModel.getCheck();
    }

    public static boolean validateCRCResidue(CRCModel crcModel) {
        CRC crc = new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
        long input = crc.getValue();
        byte[] newBytes = crcModel.getRefOut() ?
                crcModel.getRefIn() ?
                        longToBytes(input, ByteOrder.LITTLE_ENDIAN) :
                        longToBytes(Long.reverse(input), ByteOrder.BIG_ENDIAN) :
                longToBytes(input << 64 - crcModel.getWidth(), ByteOrder.BIG_ENDIAN);
        crc.updateBits(newBytes, 0, crcModel.getWidth());

        long residue = crc.getValue() ^ crcModel.getXorOut();
        return residue == crcModel.getResidue();
    }
}
