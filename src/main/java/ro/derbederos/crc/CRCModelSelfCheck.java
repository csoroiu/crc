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

package ro.derbederos.crc;

import ro.derbederos.crc.purejava.CRC64SlicingBy16;

public class CRCModelSelfCheck {
    private static final byte[] testInput = "123456789".getBytes();

    public static void validateCRCModelParams(CRCModel crcModel) {
        long mask = 0xFFFFFFFFFFFFFFFFL >>> 64 - crcModel.getWidth();

        if ((crcModel.getPoly() & ~mask) != 0) {
            throw new IllegalArgumentException("Poly width too large.");
        }
        if ((crcModel.getInit() & ~mask) != 0) {
            throw new IllegalArgumentException("Init width too large.");
        }
        if ((crcModel.getXorOut() & ~mask) != 0) {
            throw new IllegalArgumentException("XorOut width too large.");
        }
        if (crcModel.getRefIn() && !crcModel.getRefOut()) {
            throw new IllegalArgumentException("Model seems to be strange (refIn=true, refOut=false).");
        }
        if (crcModel.getWidth() < 3 || crcModel.getWidth() > 64) {
            throw new IllegalArgumentException("width < 3 and width > 64 unsupported in current version.");
        }
    }

    public static boolean validateCRCValue(CRCModel crcModel) {
        CRC crc = new CRC64SlicingBy16(crcModel);
        crc.update(testInput, 0, testInput.length);
        long value = crc.getValue();
        return value == crcModel.getCheck();
    }

    public static boolean validateCRCResidue(CRCModel crcModel) {
        CRC crc = new CRC64SlicingBy16(crcModel);
        return computeResidue(crc, crcModel) == crcModel.getResidue();
    }

    static long computeResidue(CRC crc, CRCModel crcModel) {
        long input = crc.getValue();
        // reflect the input
        if (crcModel.getRefOut() != crcModel.getRefIn()) {
            input = Long.reverse(input << 64 - crcModel.getWidth());
        }
        crc.updateBits(input, crcModel.getWidth());
        // xor to get the residual value of the crc register
        return crc.getValue() ^ crcModel.getXorOut();
    }
}
