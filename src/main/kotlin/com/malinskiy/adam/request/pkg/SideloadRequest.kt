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

package com.malinskiy.adam.request.pkg

import com.malinskiy.adam.Const
import com.malinskiy.adam.request.ComplexRequest
import com.malinskiy.adam.request.ValidationResponse
import com.malinskiy.adam.transport.Socket
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File

class SideloadRequest(
    private val pkg: File,
    private var blockSize: Int = Const.MAX_FILE_PACKET_LENGTH
) : ComplexRequest<Boolean>() {
    val buffer = ByteArray(blockSize)

    override suspend fun readElement(socket: Socket): Boolean {
        var pkgChannel: ByteReadChannel? = null
        try {
            pkgChannel = pkg.readChannel()
            var currentOffset = 0L

            while (true) {
                socket.readFully(buffer, 0, 8)
                val bytes = buffer.copyOfRange(0, 8)
                when {
                    bytes.contentEquals(Const.Message.DONEDONE) -> return true
                    bytes.contentEquals(Const.Message.FAILFAIL) -> return false
                    else -> {
                        val blockId = String(bytes, 0, 8, Const.DEFAULT_TRANSPORT_ENCODING).toLong()
                        val offset = blockId * blockSize
                        if (offset != currentOffset) {
                            //We can't seek on the channels. Recreating the channel again
                            pkgChannel?.cancel()
                            pkgChannel = pkg.readChannel(start = offset)
                            currentOffset = offset
                        }

                        val expectedLength = if (pkg.length() - currentOffset < blockSize) {
                            pkg.length() - currentOffset
                        } else {
                            blockSize.toLong()
                        }
                        pkgChannel?.readFully(buffer, 0, expectedLength.toInt())
                        socket.writeFully(buffer, 0, expectedLength.toInt())
                        currentOffset += expectedLength
                    }
                }
            }
        } finally {
            pkgChannel?.cancel()
        }
    }

    override fun validate(): ValidationResponse {
        val message =
            if (!pkg.exists()) {
                ValidationResponse.packageShouldExist(pkg)
            } else if (!pkg.isFile) {
                ValidationResponse.packageShouldBeRegularFile(pkg)
            } else {
                null
            }

        return ValidationResponse(message == null, message)
    }

    override fun serialize() = createBaseRequest("sideload-host:${pkg.length()}:${blockSize}")
}
