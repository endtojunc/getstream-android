package io.getstream.chat.ui.sample.feature.delete_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.logging.StreamLog

class DeleteAccountViewModel: ViewModel() {

    private val _events = MutableLiveData<Event<UiEvent>>()
    private val logger = StreamLog.getLogger("Chat:LoginViewModel")
    var events: LiveData<Event<UiEvent>> = _events
    private val networkWorker = NetworkWorker()

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.DeleteClicked -> deleteUser(username = action.username)
        }
    }

    private fun deleteUser(username: String) {
        networkWorker.deleteAccount(username, callback = {
            if (it.isSuccess) {
                App.instance.userRepository.clearUser()
                ChatClient.instance().disconnect(true).enqueue()
                _events.postValue(Event(UiEvent.RedirectToLogin))
            } else {
                _events.postValue(Event(UiEvent.Error(errorMessage = it.errorMessage)))
            }
        })
    }

    sealed class UiAction {
        data class DeleteClicked(val username: String): UiAction()
    }

    sealed class UiEvent {
        object RedirectToLogin: UiEvent()
        data class Error(val errorMessage: String): UiEvent()
    }
}