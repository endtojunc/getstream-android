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

package io.getstream.chat.android.offline.plugin.listener.internal

import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.test.randomMessage
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.client.utils.SyncStatus
import io.getstream.chat.android.offline.plugin.logic.channel.internal.ChannelLogic
import io.getstream.chat.android.offline.plugin.logic.internal.LogicRegistry
import io.getstream.chat.android.test.randomCID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
internal class ShuffleGiphyListenerStateTest {

    private val channelLogic: ChannelLogic = mock()
    private val logic: LogicRegistry = mock {
        on(it.channelFromMessage(any())) doReturn channelLogic
    }
    private val shuffleGiphyListenerState = ShuffleGiphyListenerState(logic)

    @Test
    fun `when shuffling giphys and request succeeds, it should be upserted`() = runTest {
        val testMessage = randomMessage()

        shuffleGiphyListenerState.onShuffleGiphyResult(randomCID(), Result.success(testMessage))

        verify(channelLogic).upsertMessage(testMessage.copy(syncStatus = SyncStatus.COMPLETED))
    }

    @Test
    fun `when shuffling giphys and request fails, it should NOT be upserted`() = runTest {
        shuffleGiphyListenerState.onShuffleGiphyResult(randomCID(), Result.error(ChatError()))

        verify(channelLogic, never()).upsertMessage(any())
    }
}
