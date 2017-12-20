package ro.derbederos.crc.purejava;

class GfUtilUnreflected implements GfUtil {
    private final GfUtil delegate;
    private final int width;
    private final long crcOfCrc;

    GfUtilUnreflected(GfUtil delegate, int width) {
        this.delegate = delegate;
        this.width = width;
        this.crcOfCrc = reflect(delegate.getCrcOfCrc());
    }

    @Override
    public long concatenate(long crc_A, long crc_B, long bytes_B) {
        return reflect(delegate.concatenate(reflect(crc_A), reflect(crc_B), bytes_B));
    }

    @Override
    public long crcOfZeroes(long bytes, long start) {
        return reflect(delegate.crcOfZeroes(bytes, reflect(start)));
    }

    @Override
    public long getCrcOfCrc() {
        return crcOfCrc;
    }

    private long reflect(long value) {
        return Long.reverse(value) >>> (64 - width);
    }
}
