package ro.derbederos.crc.purejava;

import ro.derbederos.crc.CRCModel;

import static java.lang.Integer.compareUnsigned;
import static java.lang.Integer.toUnsignedLong;
import static java.lang.Long.reverse;

/**
 * Andrew Kadatch's and Bob Jenkins's gf_util functions from crcutil library
 * (https://code.google.com/archive/p/crcutil/downloads).
 */
class GfUtil32Reflected implements GfUtil {

    private final int degree;
    private final int init;
    private final int canonize;
    private int x_pow_2n[] = new int[Long.BYTES * 8];
    private int one;
    private int normalize[] = new int[2];

    private long crcOfCrc;

    GfUtil32Reflected(CRCModel crcModel) {
        degree = crcModel.getWidth();
        int poly = (int) (reverse(crcModel.getPoly()) >>> (64 - degree));
        init = (int) (reverse(crcModel.getInit()) >>> (64 - degree));
        canonize = (int) (reverse(crcModel.getXorOut()) >>> (64 - degree));
        init(poly);
    }

    /**
     * Initializes the tables given generating polynomial of degree (degree).
     * If "canonical" is true, starting CRC value and computed CRC value will be
     * XOR-ed with 111...111.
     */
    private void init(int poly) {
        int one = 1;
        one <<= degree - 1;
        this.one = one;

        this.normalize[0] = 0;
        this.normalize[1] = poly;

        int k = one >>> 1;

        for (int i = 0; i < x_pow_2n.length; i++) {
            this.x_pow_2n[i] = k;
            k = multiply(k, k);
        }

        this.crcOfCrc = toUnsignedLong(multiply(this.canonize, this.one ^ XpowN(degree)));
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
        int result = changeStartValue((int) crc_B, bytes_B, init ^ canonize/* start_B */, (int) crc_A);
        return toUnsignedLong(result);
    }

    /**
     * Returns CRC of sequence of zeroes -- without touching the data.
     */
    @Override
    public long crcOfZeroes(long bytes, long start) {
        int tmp = multiply((int) (start ^ this.canonize), Xpow8N(bytes));
        return toUnsignedLong(tmp ^ this.canonize);
    }

    /**
     * Returns expected CRC value of {@code CRC(Message,CRC(Message))}
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
        //works for N < 0x2000000000000000L
        return XpowN(n << 3);
    }

    /**
     * Returns (x ** n mod P).
     */
    private int XpowN(long n) {
        int result = this.one;

        for (int i = 0; n != 0; i++, n >>>= 1) {
            if ((n & 1) != 0) {
                result = multiply(result, this.x_pow_2n[i]);
            }
        }
        return result;
    }

    /**
     * Returns ((a * b) mod P) where "a" and "b" are of degree <= (D-1).
     */
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

    /**
     * Returns ((unnorm * m) mod P) where degree of m is <= (D-1)
     * and degree of value "unnorm" is provided explicitly.
     */
    private int multiplyUnnormalized(int unnorm, int degree, int m) {
        int ones = this.one | (this.one - 1);
        int v = unnorm;
        int result = 0;
        while (degree > this.degree) {
            degree -= this.degree;
            int value = v & ones;
            result ^= multiply(value, multiply(m, XpowN(degree)));
            v >>>= this.degree;
        }
        result ^= multiply(v << (this.degree - degree), m);
        return result;
    }
}
