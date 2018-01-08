package ro.derbederos.crc.gf.hwemulation;

import static ro.derbederos.crc.gf.CarryLessMultiplication.clmul64;

public class PCLMUL {
    //x86 PCLMULQDQ instruction

    public static Vector128 pclmullqlqdq(long l0, long h0, long l1, long h1) {
        return pclmulqdq(l0, h0, l1, h1, 0x00);
    }

    public static Vector128 pclmulhqlqdq(long l0, long h0, long l1, long h1) {
        return pclmulqdq(l0, h0, l1, h1, 0x01);
    }

    public static Vector128 pclmullqhqdq(long l0, long h0, long l1, long h1) {
        return pclmulqdq(l0, h0, l1, h1, 0x10);
    }

    public static Vector128 pclmulhqhqdq(long l0, long h0, long l1, long h1) {
        return pclmulqdq(l0, h0, l1, h1, 0x11);
    }

    //__m128i _mm_clmulepi64_si128 (__m128i, __m128i, const int)
    public static Vector128 _mm_clmulepi64_si128(long l0, long h0, long l1, long h1, int selector) {
        return pclmulqdq(l0, h0, l1, h1, selector);
    }

    public static Vector128 pclmulqdq(long l0, long h0, long l1, long h1, int selector) {
        long a = (selector & 0x01) == 0 ? l0 : h0;
        long b = (selector & 0x10) == 0 ? l1 : h1;
        long r[] = clmul64(a, b);
        return new Vector128(r[0], r[1]);
    }


}
