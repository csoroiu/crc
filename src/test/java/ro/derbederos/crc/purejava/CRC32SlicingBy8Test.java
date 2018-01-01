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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ro.derbederos.crc.AbstractCRCTest;
import ro.derbederos.crc.CRCFactory;
import ro.derbederos.crc.CRCModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class CRC32SlicingBy8Test extends AbstractCRCTest {

    public CRC32SlicingBy8Test(CRCModel crcModel) {
        super(crcModel, CRC32SlicingBy8::new);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<CRCModel> getCRCParameters() {
        return Arrays.stream(CRCFactory.getDefinedModels())
                .filter(crcModel -> crcModel.getWidth() <= 32)
                .collect(Collectors.toList());
    }
}
