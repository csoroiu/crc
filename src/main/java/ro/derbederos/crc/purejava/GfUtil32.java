package ro.derbederos.crc.purejava;

import static java.lang.Integer.compareUnsigned;

class GfUtil32 {
    private int canonize;
    private int x_pow_2n[] = new int[Long.BYTES * 8];
    private int one;
    private int normalize[] = new int[2];

    private int generatingPolynomial;
    private int crcOfCrc;
    private int degree; //width of the CRC

    public GfUtil32(int generatingPolynomial, int degree, boolean canonical) {
        init(generatingPolynomial, degree, canonical);
    }

    private void init(int generatingPolynomial, int degree, boolean canonical) {
        int one = 1;
        one <<= degree - 1;
        this.generatingPolynomial = generatingPolynomial;
        this.degree = degree;
        this.one = one;
        if (canonical) {
            this.canonize = one | (one - 1);
        } else {
            this.canonize = 0;
        }
        this.normalize[0] = 0;
        this.normalize[1] = generatingPolynomial;

        int k = one >>> 1;

        for (int i = 0; i < Long.BYTES * 8; i++) {
            this.x_pow_2n[i] = k;
            k = multiply(k, k);
        }

        this.crcOfCrc = multiply(this.canonize,
                this.one ^ XpowN(degree));
    }

    // Returns number of bits in CRC (degree of generating polynomial).
    public int getDegree() {
        return this.degree;
    }

    // Returns start/finish adjustment constant.
    public long getCanonize() {
        return this.canonize;
    }

    // Returns normalized value of 1.
    public long one() {
        return this.one;
    }

    // Returns value of CRC(A, |A|, start_new) given known
    // crc=CRC(A, |A|, start_old) -- without touching the data.
    private int changeStartValue(int crc, long bytes, int start_old, int start_new) {
        return (crc ^ multiply(start_new ^ start_old, Xpow8N(bytes)));
    }

    // Returns CRC of concatenation of blocks A and B when CRCs
    // of blocks A and B are known -- without touching the data.
    //
    // To be precise, given CRC(A, |A|, startA) and CRC(B, |B|, 0),
    // returns CRC(AB, |AB|, startA).
    public int concatenate(int crc_A, int crc_B, long bytes_B) {
        return changeStartValue(crc_B, bytes_B, 0 /* start_B */, crc_A);
    }

    // Returns CRC of sequence of zeroes -- without touching the data.
    public int crcOfZeroes(long bytes, int start) {
        int tmp = multiply(start ^ this.canonize, Xpow8N(bytes));
        return (tmp ^ this.canonize);
    }

    // Returns expected CRC value of CRC(Message,CRC(Message))
    // when CRC is stored after the message. This value is fixed
    // and does not depend on the message or CRC start value.
    public long getCrcOfCrc() {
        return this.crcOfCrc;
    }

    // Returns (x ** (8 * n) mod P).
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
