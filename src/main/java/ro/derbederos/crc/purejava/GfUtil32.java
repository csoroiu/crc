package ro.derbederos.crc.purejava;

import static java.lang.Integer.compareUnsigned;

class GfUtil32 implements GfUtil {
    private int init;
    private int xorOut;
    private int x_pow_2n[] = new int[Long.BYTES * 8];
    private int one;
    private int normalize[] = new int[2];

    private long crcOfCrc;

    public GfUtil32(int generatingPolynomial, int degree, int init, int xorOut) {
        init(generatingPolynomial, degree, init, xorOut);
    }

    private void init(int generatingPolynomial, int degree, int init, int xorOut) {
        int one = 1;
        one <<= degree - 1;
        this.one = one;
        this.init = init;
        this.xorOut = xorOut;

        this.normalize[0] = 0;
        this.normalize[1] = generatingPolynomial;

        int k = one >>> 1;

        for (int i = 0; i < Long.BYTES * 8; i++) {
            this.x_pow_2n[i] = k;
            k = multiply(k, k);
        }

        this.crcOfCrc = multiply(this.xorOut, this.one ^ XpowN(degree)) & 0xFFFFFFFFL;
    }

    /**
     * Returns value of CRC(A, |A|, start_new) given known
     * crc=CRC(A, |A|, start_old) -- without touching the data.
     */
    private int changeStartValue(int crc, long bytes, int start_old, int start_new) {
        return (crc ^ multiply(start_new ^ start_old, Xpow8N(bytes)));
    }

    /**
     * Returns CRC of concatenation of blocks A and B when CRCs
     * of blocks A and B are known -- without touching the data.
     * <p>
     * To be precise, given CRC(A, |A|, startA) and CRC(B, |B|, 0),
     * returns CRC(AB, |AB|, startA).
     */
    @Override
    public long concatenate(long crc_A, long crc_B, long bytes_B) {
        return (long) changeStartValue((int) crc_B, bytes_B, init ^ xorOut/* start_B */, (int) crc_A) & 0xFFFFFFFFL;
    }

    /**
     * Returns CRC of sequence of zeroes -- without touching the data.
     */
    @Override
    public long crcOfZeroes(long bytes, long start) {
        int tmp = multiply((int) (start ^ this.xorOut), Xpow8N(bytes));
        return (tmp ^ this.xorOut) & 0xFFFFFFFFL;
    }

    /**
   	 * Returns expected CRC value of {@code }CRC(Message,CRC(Message))
   	 * when CRC is stored after the message. This value is fixed
   	 * and does not depend on the message or CRC start value.
   	 * This is also called <b>residue</b>.
   	 */
    @Override
    public long getCrcOfCrc() {
        return this.crcOfCrc;
    }

    /**
     * Returns (x ** (8 * n) mod P).
     */
    private int Xpow8N(long n) {
        return XpowN(n << 3);
    }

    private int XpowN(long n) {
        int result = this.one;

        for (int i = 0; n != 0; i++, n >>>= 1) {
            if ((n & 1) != 0) {
                result = multiply(result, this.x_pow_2n[i]);
            }
        }
        return result;
    }

    private int multiply(int aa, int bb) {
        int a = aa;
        int b = bb;
        if (compareUnsigned(a ^ (a - 1), b ^ (b - 1)) < 0) {
            int temp = a;
            a = b;
            b = temp;
        }

        if (a == 0) {
            return a;
        }

        int product = 0;
        int one = this.one;
        for (; a != 0; a <<= 1) {
            if ((a & one) != 0) {
                product ^= b;
                a ^= one;
            }
            b = (b >>> 1) ^ this.normalize[b & 1];
        }
        return product;
    }
}
