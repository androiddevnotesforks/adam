/*
 * Copyright (C) 2021 Anton Malinskiy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.malinskiy.adam.request.reverse

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.malinskiy.adam.Const
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RemoveAllReversePortForwardsRequestTest {
    @Test
    fun testSerializeDefault() {
        val bytes = RemoveAllReversePortForwardsRequest().serialize()

        assertThat(String(bytes, Const.DEFAULT_TRANSPORT_ENCODING))
            .isEqualTo("0017reverse:killforward-all")
    }

    @Test
    fun testDummy() {
        runBlocking {
            assertThat(RemoveAllReversePortForwardsRequest().transform()).isEqualTo(Unit)
            assertThat(RemoveAllReversePortForwardsRequest().process(ByteArray(1), 0, 1)).isEqualTo(Unit)
        }
    }
}
