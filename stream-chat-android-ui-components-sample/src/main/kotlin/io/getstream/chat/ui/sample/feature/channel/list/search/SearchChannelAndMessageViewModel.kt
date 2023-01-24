package io.getstream.chat.ui.sample.feature.channel.list.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.core.internal.coroutines.DispatcherProvider
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.logging.StreamLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext

class SearchChannelAndMessageViewModel: ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData(State())

    private val _events = MutableLiveData<Event<UiEvent>>()

    var events: LiveData<Event<UiEvent>> = _events

    public val state: LiveData<State> = _state

    private val scope = MainScope()

    private var job: Job? = null

    private val logger = StreamLog.getLogger("Chat:SearchChannelAndMessageViewModel")

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.ResultItemClicked -> _events.postValue(Event(UiEvent.RedirectToChat(cid = action.cid)))
        }
    }

    fun search(query: String) {
        job?.cancel()

        if (query.isEmpty()) {
            _state.value = State(
                query = query,
                canLoadMore = false,
                isLoading = false,
                isLoadingMore = false
            )
        } else {
            _state.value = State(
                query = query,
                canLoadMore = true,
                isLoading = true,
                isLoadingMore = false
            )
        }
        searchMessage()
    }

    private fun searchMessage() {

    }

    public data class State(
        val query: String = "",
        val canLoadMore: Boolean = true,
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false
    )

    private companion object {
        private const val QUERY_LIMIT = 30
    }

    sealed class UiAction {
        data class ResultItemClicked(val cid: String): UiAction()
    }

    sealed class UiEvent {
        data class RedirectToChat(val cid: String): UiEvent()
        data class Error(val errorMessage: String): UiEvent()
    }
}