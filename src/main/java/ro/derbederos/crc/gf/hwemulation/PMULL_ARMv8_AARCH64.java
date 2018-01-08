package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;

/**
 * pmull/pmull2 armv8 cpu instructions AARCH64
 */
public class PMULL_ARMv8_AARCH64 {

    /**
     * Polynomial Multiply Long - lower half
     * part of ACLE
     *
     * @see #pmull(long, long, long, long)
     */
    public static Vector128 vmull_p64(long l0, long h0, long l1, long h1) {
        return pmull(l0, h0, l1, h1);
    }

    /**
     * Polynomial Multiply Long - upper half
     * part of ACLE
     *
     * @see #pmull2(long, long, long, long)
     */
    public static Vector128 vmull_high_p64(long l0, long h0, long l1, long h1) {
        return pmull2(l0, h0, l1, h1);
    }

    /**
     * Polynomial Multiply Long - lower half
     */
    public static Vector128 pmull(long l0, long h0, long l1, long h1) {
        long[] r = clmul64(l0, l1);
        return new Vector128(r[0], r[1]);
    }

    /**
     * Polynomial Multiply Long - upper half
     */
    public static Vector128 pmull2(long l0, long h0, long l1, long h1) {
        long[] r = clmul64(h0, h1);
        return new Vector128(r[0], r[1]);
    }
}
