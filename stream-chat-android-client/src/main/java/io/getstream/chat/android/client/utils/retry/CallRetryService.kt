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

package io.getstream.chat.android.client.utils.retry

import io.getstream.chat.android.client.extensions.isPermanent
import io.getstream.chat.android.client.utils.Result
import io.getstream.logging.StreamLog
import kotlinx.coroutines.delay

/**
 * Service that allows retrying calls based on [RetryPolicy].
 *
 * @param retryPolicy The policy used for determining if the call should be retried.
 */
internal class CallRetryService(private val retryPolicy: RetryPolicy) {

    private val logger = StreamLog.getLogger("Chat:CallRetryService")

    /**
     * Runs the task and retries based on [RetryPolicy].
     *
     * @param task The task to be run.
     */
    @Suppress("LoopWithTooManyJumpStatements")
    suspend fun <T : Any> runAndRetry(task: suspend () -> Result<T>): Result<T> {
        var attempt = 1
        var result: Result<T>
        while (true) {
            result = task()
            if (result.isSuccess || result.error().isPermanent()) {
                break
            }
            val shouldRetry = retryPolicy.shouldRetry(attempt, result.error())
            val timeout = retryPolicy.retryTimeout(attempt, result.error())

            if (shouldRetry) {
                // temporary failure, continue
                logger.i {
                    "API call failed (attempt $attempt), retrying in $timeout seconds. Error was ${result.error()}"
                }
                delay(timeout.toLong())
                attempt += 1
            } else {
                logger.i {
                    "API call failed (attempt $attempt). Giving up for now, will retry when connection recovers. " +
                        "Error was ${result.error()}"
                }
                break
            }
        }
        return result
    }
}
