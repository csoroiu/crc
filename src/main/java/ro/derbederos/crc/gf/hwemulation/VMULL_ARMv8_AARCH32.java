package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;

public class VMULL_ARMv8_AARCH32 extends VMULL_ARMv7_AARCH32 {
    /**
     * armv8 - Vector Polynomial Multiply Long
     */
    public static Vector128 vmull_p64(long l0, long h0, long l1, long h1) {
        long[] r = clmul64(l0, l1);
        return new Vector128(r[0], r[1]);
    }

    /**
     * armv8 - Vector Polynomial Multiply Long
     * part of ACLE
     */
    public static Vector128 vmull_high_p64(long l0, long h0, long l1, long h1) {
        long[] r = clmul64(h0, h1);
        return new Vector128(r[0], r[1]);
    }

    /**
     * armv7/armv8 - Vector Polynomial Multiply Long
     */
    public static Vector128 vmull_p8(long l0, long l1) {
        return VMULL_ARMv7_AARCH32.vmull_p8(l0, l1);
    }
}
