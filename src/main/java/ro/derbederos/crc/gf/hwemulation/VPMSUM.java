package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;

/**
 * PowerPC ISA 2.07 VPMSUM instructions
 */
public class VPMSUM {

    /**
     * Vector Polynomial Multiply-Sum Doubleword VX-form
     */
    public static Vector128 vpmsumd(long l0, long h0, long l1, long h1) {
        long[] lr = clmul64(l0, l1);
        long[] hr = clmul64(h0, h1);
        return new Vector128(hr[0] ^ lr[0], hr[1] ^ lr[1]);
    }

    /**
     * Vector Polynomial Multiply-Sum Word VX-form
     */
    public static Vector128 vpmsumw(long l0, long h0, long l1, long h1) {
        Vector128 a = new Vector128(l0, h0);
        Vector128 b = new Vector128(l1, h1);
        long[] prod = new long[4];
        for (int i = 0; i < 4; i++) {
            prod[i] = clmul(a.getInt(i), b.getInt(i));
        }
        return new Vector128(prod[0] ^ prod[1], prod[2] ^ prod[3]);
    }

    /**
     * Vector Polynomial Multiply-Sum Halfword VX-form
     */
    public static Vector128 vpmsumh(long l0, long h0, long l1, long h1) {
        Vector128 a = new Vector128(l0, h0);
        Vector128 b = new Vector128(l1, h1);
        int[] prod = new int[8];
        for (int i = 0; i < 8; i++) {
            int aShort = Short.toUnsignedInt(a.getShort(i));
            int bShort = Short.toUnsignedInt(b.getShort(i));
            prod[i] = (int) clmul(aShort, bShort);
        }
        return new Vector128(prod[0] ^ prod[1], prod[2] ^ prod[3], prod[4] ^ prod[5], prod[6] ^ prod[7]);
    }

    /**
     * Vector Polynomial Multiply-Sum Byte VX-form
     */
    public static Vector128 vpmsumb(long l0, long h0, long l1, long h1) {
        Vector128 a = new Vector128(l0, h0);
        Vector128 b = new Vector128(l1, h1);
        short[] prod = new short[16];
        for (int i = 0; i < 16; i++) {
            int aShort = Byte.toUnsignedInt(a.getByte(i));
            int bShort = Byte.toUnsignedInt(b.getByte(i));
            prod[i] = (short) clmul(aShort, bShort);
        }
        return new Vector128(prod[0] ^ prod[1] + (prod[2] ^ prod[3]) << 16,
                prod[4] ^ prod[5] + (prod[6] ^ prod[7]) << 16,
                prod[8] ^ prod[9] + (prod[10] ^ prod[11]) << 16,
                prod[12] ^ prod[13] + (prod[14] ^ prod[15]) << 16
        );
    }
}
