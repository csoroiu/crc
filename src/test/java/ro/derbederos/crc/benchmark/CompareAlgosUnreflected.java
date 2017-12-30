package ro.derbederos.crc.benchmark;

import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;
import ro.derbederos.crc.purejava.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Checksum;

public class CompareAlgosUnreflected {

    public static final long SEED = 0x12fed1a214ecbd00L;
    private static Random r = new Random(SEED);

    public static void main(String[] args) {

        byte[] buffer = new byte[65536];
        r.nextBytes(buffer);
        System.out.println("CRC-32/BZIP2: e696f3ac");

        CRCModel crcModel = CRCFactory.getModel("CRC-32/BZIP2");

        List<Checksum> checksums = new ArrayList<>();
        checksums.add(CRCFactory.getCRC(crcModel));
        checksums.add(new CRC32(crcModel));
        checksums.add(new CRC64(crcModel));
        checksums.add(new CRC32SlicingBy8(crcModel));
        checksums.add(new CRC32SlicingBy16(crcModel));
        checksums.add(new CRC64SlicingBy16(crcModel));
//        checksums.add(new CRC32Branchfree(crcModel));

        for (Checksum checksum : checksums) {
            getCRCValue(checksum, buffer);
            getCRCValue(checksum, buffer);
            getCRCValue(checksum, buffer);
        }
    }

    private static void getCRCValue(Checksum crc, byte[] buffer) {
        String name = crc.getClass().getSimpleName();
        long t1 = System.currentTimeMillis();
        crc.reset();
        for (int i = 0; i < 16384; i++) {
            crc.update(buffer, i % 8, buffer.length - i % 16);
//                      crc.update(buffer, 0, buffer.length);
        }
        long t2 = System.currentTimeMillis();
        System.out.println(name + " Took (ms): " + (t2 - t1) + " value: " + Long.toHexString(crc.getValue()));
    }
}
