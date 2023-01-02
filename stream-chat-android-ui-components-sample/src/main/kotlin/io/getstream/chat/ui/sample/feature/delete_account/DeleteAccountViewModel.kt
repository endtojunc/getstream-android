package io.getstream.chat.ui.sample.feature.delete_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.logging.StreamLog

class DeleteAccountViewModel: ViewModel() {

    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _state = MutableLiveData<State>()

    private val logger = StreamLog.getLogger("Chat:LoginViewModel")

    var state: LiveData<State> = _state
    var events: LiveData<Event<UiEvent>> = _events

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.DeleteClicked -> deleteUser(username = action.username)
        }
    }

    private fun deleteUser(username: String) {

    }

    sealed class State {
        data class DeletedUser(val user: SampleUser): State()
    }

    sealed class UiAction {
        data class DeleteClicked(val username: String): UiAction()
    }

    sealed class UiEvent {
        object RedirectToLogin: UiEvent()
    }
}