package ro.derbederos.crc.purejava.crc32;

import java.util.zip.CRC32;

public class CRC32_JAMCRC extends CRC32 {
    @Override
    public long getValue() {
        return super.getValue() ^ 0xFFFFFFFFL;
    }
}
