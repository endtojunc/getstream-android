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

import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.extensions.cidToTypeAndId
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.Member
import io.getstream.chat.android.client.persistance.repository.ChannelRepository
import io.getstream.chat.android.client.persistance.repository.UserRepository
import io.getstream.chat.android.client.test.randomMember
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.test.randomCID
import io.getstream.chat.android.test.randomInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

@OptIn(ExperimentalCoroutinesApi::class)
internal class QueryMembersListenerDatabaseTest {

    private val userRepository: UserRepository = mock()
    private val channelRepository: ChannelRepository = mock()
    private val queryMembersListenerDatabase = QueryMembersListenerDatabase(userRepository, channelRepository)

    @Test
    fun `when query members is successful database should be updated`() = runTest {
        val memberList = randomMember().let(::listOf)
        val cid = randomCID()
        val (type, id) = cid.cidToTypeAndId()

        queryMembersListenerDatabase.onQueryMembersResult(
            result = Result.success(memberList),
            channelType = type,
            channelId = id,
            offset = randomInt(),
            limit = randomInt(),
            filter = Filters.neutral(),
            sort = QuerySortByField.descByName("name"),
            members = memberList,
        )

        verify(userRepository).insertUsers(memberList.map(Member::user))
        verify(channelRepository).updateMembersForChannel(cid, memberList)
    }

    @Test
    fun `when query members fails database should not be updated`() = runTest {
        reset(userRepository, channelRepository)

        val memberList = randomMember().let(::listOf)
        val cid = randomCID()
        val (type, id) = cid.cidToTypeAndId()

        queryMembersListenerDatabase.onQueryMembersResult(
            result = Result.error(ChatError()),
            channelType = type,
            channelId = id,
            offset = randomInt(),
            limit = randomInt(),
            filter = Filters.neutral(),
            sort = QuerySortByField.descByName("name"),
            members = memberList,
        )

        verifyNoInteractions(userRepository, channelRepository)
    }
}
