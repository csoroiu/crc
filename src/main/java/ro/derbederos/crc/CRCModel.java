package ro.derbederos.crc;

import java.util.Objects;

/**
 * Ross N. Williams compatible CRC definition model (http://www.ross.net/crc/download/crc_v3.txt)
 */
public final class CRCModel {
    private final String name;
    private final int width;
    private final long poly;
    private final long init;
    private final boolean refIn; // ByteOrder.BIG_ENDIAN(false) vs ByteOrder.LITTLE_ENDIAN(true)
    private final boolean refOut;
    private final long xorOut;
    private final long check;
    private final long residue;

    public CRCModel(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut, long check, long residue) {
        this.name = name;
        this.width = width;
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
        this.check = check;
        this.residue = residue;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public long getPoly() {
        return poly;
    }

    public long getInit() {
        return init;
    }

    public boolean getRefIn() {
        return refIn;
    }

    public boolean getRefOut() {
        return refOut;
    }

    public long getXorOut() {
        return xorOut;
    }

    public long getCheck() {
        return check;
    }

    public long getResidue() {
        return residue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CRCModel crcModel = (CRCModel) o;
        return width == crcModel.width &&
                poly == crcModel.poly &&
                init == crcModel.init &&
                refIn == crcModel.refIn &&
                refOut == crcModel.refOut &&
                xorOut == crcModel.xorOut;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, poly, init, refIn, refOut, xorOut);
    }

    @Override
    public String toString() {
        return "CRCModel{" +
                "name='" + name + '\'' +
                ", width=" + width +
                ", poly=" + "0x" + Long.toHexString(poly) +
                ", init=" + "0x" + Long.toHexString(init) +
                ", refIn=" + refIn +
                ", refOut=" + refOut +
                ", xorOut=" + "0x" + Long.toHexString(xorOut) +
                ", check=" + "0x" + Long.toHexString(check) +
                ", residue=" + "0x" + Long.toHexString(residue) +
                '}';
    }
}
