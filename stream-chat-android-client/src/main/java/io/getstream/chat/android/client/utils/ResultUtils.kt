/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.chat.android.client.utils

import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.errors.ChatNetworkError
import io.getstream.chat.android.core.internal.InternalStreamChatApi

/**
 * Converts [Result] into human-readable string.
 */
@InternalStreamChatApi
public inline fun <T : Any> Result<T>.stringify(toString: (data: T) -> String): String {
    return when {
        isSuccess -> toString(data())
        isError -> error().stringify()
        else -> "Result(Empty)"
    }
}

/**
 * Converts [ChatError] into human-readable string.
 */
@InternalStreamChatApi
public fun ChatError.stringify(): String {
    return when {
        this is ChatNetworkError -> toString()
        message != null && cause != null -> "ChatError(message=$message, cause=$cause)"
        message != null -> "ChatError(message=$message)"
        cause != null -> "ChatError(cause=$cause)"
        else -> "ChatError(Empty)"
    }
}
