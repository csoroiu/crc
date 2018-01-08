package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul;
import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;

/**
 * IBM z13 Vector Extension Facility (SIMD)
 * <p>
 * - Vector Galois Field Multiply Sum
 * <p>
 * - Vector Galois Field Multiply Sum and Accumulate
 */
public class VGFM {
    /**
     * Vector Galois Field Multiply Sum Doubleword
     */
    public static Vector128 vgfmg(long l0, long h0, long l1, long h1) {
        long[] lr = clmul64(l0, l1);
        long[] hr = clmul64(h0, h1);
        return new Vector128(hr[0] ^ lr[0], hr[1] ^ lr[1]);
    }

    /**
     * Vector Galois Field Multiply Sum Word
     */
    public static long vgfmf(long l0, long h0, long l1, long h1) {
        long result = clmul(low(l0), low(l1));
        result ^= clmul(high(l0), high(l1));
        result ^= clmul(low(h0), low(h1));
        result ^= clmul(high(h0), high(h1));
        return result;
    }

    /**
     * Vector Galois Field Multiply Sum Halfword
     */
    public static int vgfmh(long l0, long h0, long l1, long h1) {
        Vector128 a = new Vector128(l0, h0);
        Vector128 b = new Vector128(l1, h1);
        int prod = 0;
        for (int i = 0; i < 8; i++) {
            int aShort = Short.toUnsignedInt(a.getShort(i));
            int bShort = Short.toUnsignedInt(b.getShort(i));
            prod ^= clmul(aShort, bShort);
        }
        return prod;
    }

    /**
     * Vector Galois Field Multiply Sum Byte
     */
    public static short vgfmb(long l0, long h0, long l1, long h1) {
        Vector128 a = new Vector128(l0, h0);
        Vector128 b = new Vector128(l1, h1);
        short prod = 0;
        for (int i1 = 0; i1 < 16; i1++) {
            int aShort = Byte.toUnsignedInt(a.getByte(i1));
            int bShort = Byte.toUnsignedInt(b.getByte(i1));
            prod ^= (short) clmul(aShort, bShort);
        }
        return prod;
    }

    /**
     * Vector Galois Field Multiply Sum and Accumulate Doubleword
     */
    public static Vector128 vgfmag(long l0, long h0, long l1, long h1, long lacc, long hacc) {
        Vector128 result = vgfmg(l0, h0, l1, h1);
        result.setLong(0, result.getLong(0) ^ lacc);
        result.setLong(1, result.getLong(1) ^ hacc);
        return result;
    }

    /**
     * Vector Galois Field Multiply Sum and Accumulate Word
     */
    public static long vgfmaf(long l0, long h0, long l1, long h1, long acc) {
        return vgfmf(l0, h0, l1, h1) ^ acc;
    }

    /**
     * Vector Galois Field Multiply Sum and Accumulate Halfword
     */
    public static int vgfmah(long l0, long h0, long l1, long h1, int acc) {
        return vgfmh(l0, h0, l1, h1) ^ acc;
    }

    /**
     * Vector Galois Field Multiply Sum and Accumulate Byte
     */
    public static short vgfmab(long l0, long h0, long l1, long h1, short acc) {
        return (short) (vgfmb(l0, h0, l1, h1) ^ acc);
    }

    private static int high(long l) {
        return (int) (l >>> 32);
    }

    private static int low(long l) {
        return (int) (l & 0xFFFFFFFFL);
    }
}
