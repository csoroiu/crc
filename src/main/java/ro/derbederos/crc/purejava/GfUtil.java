package ro.derbederos.crc.purejava;

interface GfUtil {

    /**
     * Returns CRC of concatenation of blocks A and B when CRCs
     * of blocks A and B are known -- without touching the data.
     * <p>
     * To be precise, given CRC(A, |A|, startA) and CRC(B, |B|, 0),
     * returns CRC(AB, |AB|, startA).
     */
    long concatenate(long crc_A, long crc_B, long bytes_B);

    /**
     * Returns CRC of sequence of zeroes -- without touching the data.
     */
    long crcOfZeroes(long bytes, long start);

    /**
     * Returns expected CRC value of {@code }CRC(Message,CRC(Message))
     * when CRC is stored after the message. This value is fixed
     * and does not depend on the message or CRC start value.
     * This is also called <b>residue</b>.
     */
    long getCrcOfCrc();
}
