/*
 * Copyright (C) 2020 Anton Malinskiy
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

package com.malinskiy.adam.transport

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.nio.*
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class KtorSocketFactory(
    coroutineContext: CoroutineContext,
    private val connectTimeout: Long = 10_000,
    private val idleTimeout: Long = 30_000
) : SocketFactory {
    private val selectorManager: SelectorManager = ActorSelectorManager(coroutineContext)

    override suspend fun tcp(socketAddress: InetSocketAddress, connectTimeout: Long?, idleTimeout: Long?): Socket {
        return KtorSocket(aSocket(selectorManager)
                              .tcp()
                              .connect(socketAddress) {
                                  socketTimeout = idleTimeout ?: this@KtorSocketFactory.idleTimeout
                              })
    }
}