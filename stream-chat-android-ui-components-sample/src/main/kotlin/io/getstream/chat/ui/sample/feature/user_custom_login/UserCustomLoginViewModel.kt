package io.getstream.chat.ui.sample.feature.user_custom_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.chat.ui.sample.data.user.LoginResult
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.logging.StreamLog

class UserCustomLoginViewModel: ViewModel() {

    private val _state = MutableLiveData<State>()
    private val _events = MutableLiveData<Event<UiEvent>>()
    private val logger = StreamLog.getLogger("Chat:LoginViewModel")

    private val networkWorker = NetworkWorker()

    var state: LiveData<State> = _state
    var events: LiveData<Event<UiEvent>> = _events

    fun init() {
        val user = App.instance.userRepository.getUser()
        if (user != SampleUser.None) {

        }
    }

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.loginClicked -> authenticateUser(action.username, action.password)
            is UiAction.forgotClicked -> _events.postValue(Event(UiEvent.RedirectToForgot))
            is UiAction.registerClicked -> _events.postValue(Event(UiEvent.RedirectToRegister))
        }
    }

    private fun authenticateUser(username: String, password: String) {
        networkWorker.authenticateUser(username, password, callback = {
            print(it.response)
        })
    }

    fun handleLoginResult(result: Result<LoginResult>) {
        if (result.isSuccess) {
            _events.postValue(Event(UiEvent.RedirectToChannelList))
        } else {
            _events.postValue(Event(UiEvent.Error(result.error().message)))
            logger.d { "Failed to set user ${result.error()}" }
        }
    }

    sealed class State {
        data class User(val user: SampleUser) : State()
    }

    sealed class UiAction {
        object forgotClicked: UiAction()
        object registerClicked: UiAction()
        data class loginClicked(val username: String, val password: String): UiAction()
    }

    sealed class UiEvent {
        object RedirectToChannelList: UiEvent()
        object RedirectToForgot: UiEvent()
        object RedirectToRegister: UiEvent()
        data class Error(val errorMessage: String?) : UiEvent()
    }
}