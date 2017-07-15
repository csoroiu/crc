package ro.derbederos.crc;

public class CRCParameters {
    private final String name;
    private final int width;
    private final long poly;
    private final long initialValue;
    private final boolean refIn;
    private final boolean refOut;
    private final long xorOut;
    private final long check;
    private final long residue;

    public CRCParameters(String name, int width, long poly, long initialValue, boolean refIn, boolean refOut, long xorOut, long check, long residue) {
        this.name = name;
        this.width = width;
        this.poly = poly;
        this.initialValue = initialValue;
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

    public long getInitialValue() {
        return initialValue;
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
}
