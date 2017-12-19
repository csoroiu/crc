package ro.derbederos.crc;

import java.util.zip.Checksum;

public interface CRC extends Checksum {

    CRCModel getCRCModel();

    void updateBits(int b, int bits);

    default void updateBits(byte[] b, int off, int bits) {
        int len = bits >>> 3;
        update(b, off, len);
        if ((bits & 0x7) != 0) {
            updateBits(b[len], bits - (len << 3));
        }
    }

//    long concatenate(long crc_A, long crc_B, long bytes_B);
//
//    default long combine(long crc_A, long crc_B, long bytes_B) {
//        return concatenate(crc_A, crc_B, bytes_B);
//    }

}
