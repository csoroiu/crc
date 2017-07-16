package ro.derbederos.crc;

/**
 * Ross Williams compatible CRC definition model.
 */
public final class CRCModel {
    private final String name;
    private final int width;
    private final long poly;
    private final long init;
    private final boolean refIn;
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
    public String toString() {
        return "CRCParameters{" +
                "name='" + name + '\'' +
                '}';
    }
}
