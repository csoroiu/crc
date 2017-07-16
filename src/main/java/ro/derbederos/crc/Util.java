package ro.derbederos.crc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Util {

    static int roundToByte(int bits) {
        return (bits + 7) / 8 * 8;
    }

    static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(x);
        return buffer.array();
    }


    static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(x);
        return buffer.array();
    }

    static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES).order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(x);
        return buffer.array();
    }

    static short reverseShort(int i) {
        return (short) (Integer.reverse(i) >>> 16);
    }
}
