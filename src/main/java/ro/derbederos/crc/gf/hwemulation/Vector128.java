package ro.derbederos.crc.gf.hwemulation;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class Vector128 {
    private final byte[] bytes = new byte[16];
    private final MutableDirectBuffer buffer = new UnsafeBuffer(bytes);

    public Vector128() {
        this(0, 0);
    }

    public Vector128(long low, long high) {
        setLong(0, low);
        setLong(1, high);
    }

    public Vector128(int dw0, int dw1, int dw2, int dw3) {
        setInt(0, dw0);
        setInt(1, dw1);
        setInt(2, dw2);
        setInt(3, dw3);
    }

    public void setLong(int index, long value) {
        buffer.putLong(index * Long.BYTES, value);
    }

    public long getLong(int index) {
        return buffer.getLong(index * Long.BYTES);
    }

    public void setInt(int index, int value) {
        buffer.putInt(index * Integer.BYTES, value);
    }

    public int getInt(int index) {
        return buffer.getInt(index * Integer.BYTES);
    }

    public void setShort(int index, short value) {
        buffer.putShort(index * Short.BYTES, value);
    }

    public short getShort(int index) {
        return buffer.getShort(index * Short.BYTES);
    }

    public void setByte(int index, byte value) {
        buffer.putByte(index * Byte.BYTES, value);
    }

    public byte getByte(int index) {
        return buffer.getByte(index * Byte.BYTES);
    }
}
