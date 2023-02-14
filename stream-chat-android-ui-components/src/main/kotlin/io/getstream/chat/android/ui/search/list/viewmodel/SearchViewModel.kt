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

package io.getstream.chat.android.ui.search.list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.ChannelInfo
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.client.utils.map
import io.getstream.chat.android.client.utils.toResult
import io.getstream.chat.android.core.internal.coroutines.DispatcherProvider
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.android.offline.extensions.queryChannelsAsState
import io.getstream.logging.StreamLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for searching for messages that match a particular search query.
 */
public class SearchViewModel : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData(State())

    /**
     * The current state of the search screen.
     */
    public val state: LiveData<State> = _state

    private val _errorEvents: MutableLiveData<Event<Unit>> = MutableLiveData()

    /**
     * One shot error events when search fails.
     */
    public val errorEvents: LiveData<Event<Unit>> = _errorEvents

    /**
     * Coroutine scope tied to this [ViewModel].
     */
    private val scope = CoroutineScope(DispatcherProvider.Main)

    /**
     * Represents an ongoing search network request.
     */
    private var job: Job? = null

    private val logger = StreamLog.getLogger("Chat:SearchViewModel")

    /**
     * Changes the current query state. An empty search query
     */
    public fun setQuery(query: String) {
        job?.cancel()

        if (query.isEmpty()) {
            _state.value = State(
                query = query,
                results = emptyList(),
                canLoadMore = false,
                isLoading = false,
                isLoadingMore = false,
            )
        } else {
            _state.value = State(
                query = query,
                results = emptyList(),
                canLoadMore = true,
                isLoading = true,
                isLoadingMore = false,
            )

            if (query.first() == '@') {
                searchChannels()
            } else {
                searchMessages()
            }
        }
    }

    /**
     * Loads more data when the user reaches the end of the found message list.
     *
     * Does nothing of the end of the search result list has already been reached or loading
     * is already in progress.
     */
    public fun loadMore() {
        job?.cancel()

        val currentState = _state.value!!
        if (currentState.canLoadMore && !currentState.isLoading && !currentState.isLoadingMore) {
            _state.value = currentState.copy(isLoadingMore = true)
            searchMessages()
        }
    }

    /**
     * Cancels the scope tied to this [ViewModel].
     */
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    /**
     * Performs message search based on the current state.
     */
    private fun searchMessages() {
        job = scope.launch {
            val currentState = _state.value!!
            val result = searchMessages(
                query = currentState.query,
                offset = currentState.results.size
            )
            if (result.isSuccess) {
                handleSearchMessageSuccess(result.data())
            } else {
                handleSearchMessagesError(result.error())
            }
        }
    }

    /**
     * Performs channel search based on the current state.
     */
    private fun searchChannels() {
        job = scope.launch {
            val currentState = _state.value!!
            val currentUser = requireNotNull(ChatClient.instance().getCurrentUser())
            val filter = Filters.and(
                Filters.`in`("members", listOf(currentUser.id)),
                Filters.autocomplete("member.user.name", currentState.query),
                Filters.eq("member_count", 2)
            )

            val query = QueryChannelsRequest(filter, limit = 100)
            query.state = true

            val result = ChatClient.instance().queryChannels(query).await()

            if (result.isSuccess) {
                val first = result.data().first()
                val response = ChatClient.instance().queryChannel(first.type, first.id, QueryChannelRequest().withMessages(1).withMembers(1, offset = 0)).await()
                if (response.isSuccess) {
                    handleSearchChannelSuccess(response.data())
                } else {
                    handleSearchMessagesError(result.error())
                }
            } else {
                handleSearchMessagesError(result.error())
            }
        }
    }

    private fun handleSearchChannelSuccess(channel: Channel) {
        val currentState = _state.value!!

        var message = Message()
        val member = channel.members.first().user
        var text = channel.messages.first().text
        message.user.name = member.name
        var image = if (channel.image != "") {
            channel.image
        } else {
            member.image
        }
        message.cid = channel.cid
        message.channelInfo = ChannelInfo(channel.cid, name = channel.name, image = image)
        message.text = text

        _state.value = currentState.copy(
            isLoading = false,
            isLoadingMore = false,
            results = listOf(message),
            canLoadMore = false
        )
    }

    /**
     * Notifies the UI about the search results and enables the pagination.
     */
    private fun handleSearchMessageSuccess(messages: List<Message>) {
        logger.d { "Found messages: ${messages.size}" }
        val currentState = _state.value!!
        _state.value = currentState.copy(
            results = currentState.results + messages,
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = messages.size == QUERY_LIMIT
        )
    }

    /**
     * Notifies the UI about the error and enables the pagination.
     */
    private fun handleSearchMessagesError(chatError: ChatError) {
        logger.d { "Error searching messages: ${chatError.message}" }
        _state.value = _state.value!!.copy(
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = true,
        )
        _errorEvents.value = Event(Unit)
    }

    /**
     * Searches messages containing [query] text across channels where the current user is a member.
     *
     * @param query The search query.
     * @param offset The pagination offset offset.
     */
    private suspend fun searchMessages(query: String, offset: Int): Result<List<Message>> {
        logger.d { "Searching for \"$query\" with offset: $offset" }
        val currentUser = requireNotNull(ChatClient.instance().getCurrentUser())
        // TODO: use the pagination based on "limit" nad "next" params here
        val messages =  ChatClient.instance()
            .searchMessages(
                channelFilter = Filters.`in`("members", listOf(currentUser.id)),
                messageFilter = Filters.autocomplete("text", query),
                offset = offset,
                limit = QUERY_LIMIT,
            )
            .await()
            .map { it.messages }
        return messages
    }

    /**
     * Represents the search screen state, used to render the required UI.
     *
     * @param query The current search query value.
     * @param results The found messages to render.
     * @param canLoadMore If we've reached the end of messages, to stop triggering pagination.
     * @param isLoading If we're currently loading data (initial load).
     * @param isLoadingMore If we're loading more items (pagination).
     */
    public data class State(
        val query: String = "",
        val canLoadMore: Boolean = true,
        val results: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
    )

    private companion object {
        private const val QUERY_LIMIT = 30
    }
}
