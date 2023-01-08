package io.getstream.chat.ui.sample.feature.forgot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.chat.ui.sample.feature.user_custom_login.UserCustomLoginViewModel
import io.getstream.logging.StreamLog

class ForgotUserViewModel: ViewModel() {

    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _state = MutableLiveData<UserCustomLoginViewModel.State>()

    private val logger = StreamLog.getLogger("Chat:LoginViewModel")
    private val networkWorker = NetworkWorker()

    var state: LiveData<UserCustomLoginViewModel.State> = _state
    var events: LiveData<Event<UiEvent>> = _events

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.resetClicked -> resetPassword(action.username, action.password, action.confirmPasssword, action.generateOtp)
        }
    }

    fun resetPassword(username: String, password: String, confirmPassword: String, generateOtp: String) {
        networkWorker.resetPassword(username, password, confirmPassword, generateOtp, callback = {
            if (it.isSuccess) {
                _events.postValue(Event(UiEvent.RedirectToLogin))
            } else {
                _events.postValue(Event(UiEvent.Error(errorMessage = it.errorMessage)))
            }
        })
    }

    sealed class State {
        data class User(val user: SampleUser) : State()
    }

    sealed class UiAction {
        data class resetClicked(val username: String, val password: String, val confirmPasssword: String, val generateOtp: String): UiAction()
    }

    sealed class UiEvent {
        object RedirectToLogin: UiEvent()
        data class Error(val errorMessage: String): UiEvent()
    }
}