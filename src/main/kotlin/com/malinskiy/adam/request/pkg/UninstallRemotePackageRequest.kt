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

import com.malinskiy.adam.request.shell.v1.ShellCommandRequest

/**
 * @param keepData keep the data and cache directories around after package removal
 *
 * There is no way to remove the remaining data.
 * You will have to reinstall the application with the same signature, and fully uninstall it.
 */
class UninstallRemotePackageRequest(
    packageName: String,
    keepData: Boolean = false
) : ShellCommandRequest(
    cmd = StringBuilder().apply {
        append("pm uninstall ")

        if (keepData) {
            append("-k ")
        }

        append(packageName)
    }.toString()
)
