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

package io.getstream.chat.android.client.api

import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.core.internal.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ErrorCall<T : Any>(
    private val scope: CoroutineScope,
    private val e: ChatError,
) : Call<T> {
    override fun cancel() {
        // Not supported
    }

    override fun execute(): Result<T> {
        return Result(e)
    }

    override fun enqueue(callback: Call.Callback<T>) {
        scope.launch(DispatcherProvider.Main) {
            callback.onResult(Result(e))
        }
    }

    override suspend fun await(): Result<T> = withContext(scope.coroutineContext) {
        Result(e)
    }
}
