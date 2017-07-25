package ro.derbederos.crc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Util {

    static int roundToByte(int bits) {
        return (bits + 7) >>> 3 << 3;
    }

    static byte[] longToBytes(long x, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(order);
        buffer.putLong(x);
        return buffer.array();
    }
}
