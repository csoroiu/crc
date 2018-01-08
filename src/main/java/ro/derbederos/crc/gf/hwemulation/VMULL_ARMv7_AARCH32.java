package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul;

public class VMULL_ARMv7_AARCH32 {
    /**
     * armv7 - Vector Polynomial Multiply Long
     */
    public static Vector128 vmull_p8(long l0, long l1) {
        Vector128 a = new Vector128(l0, 0);
        Vector128 b = new Vector128(l1, 0);
        Vector128 result = new Vector128();
        for (int i = 0; i < 8; i++) {
            int aShort = Byte.toUnsignedInt(a.getByte(i));
            int bShort = Byte.toUnsignedInt(b.getByte(i));
            result.setShort(i, (short) clmul(aShort, bShort));
        }
        return result;
    }
}
