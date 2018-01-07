/*
 * Copyright (c) 2017-2018 Claudiu Soroiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @Override
    public long XpowN(long n) {
        return reflect(delegate.XpowN(n));
    }

    private long reflect(long value) {
        return Long.reverse(value) >>> (64 - width);
    }
}
