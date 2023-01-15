package io.getstream.chat.ui.sample.feature.user_custom_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.ConnectionData
import io.getstream.chat.android.client.utils.Result
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.application.AppConfig
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.logging.StreamLog
import io.getstream.chat.android.client.models.User as ChatUser

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
            setUserToStream(user)
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
            if (it.isSuccess) {
                setUserToStream(SampleUser(
                    id = String.format("%s", it.response?.get("username") as String),
                    name = it.response?.get("name") as String,
                    token = it.response?.get("token") as String,
                    apiKey = AppConfig.apiKey,
                    image = ""
                ))
            } else {
                _events.postValue(Event(UiEvent.Error(errorMessage = it.errorMessage)))
            }
        })
    }

    private fun setUserToStream(user: SampleUser) {
        App.instance.userRepository.setUser(user)

        val chatUser = ChatUser().apply {
            id = user.id
            image = user.image
            name = user.name
        }

        ChatClient.instance().run {
            if (getCurrentUser() == null) {
                connectUser(chatUser, user.token).enqueue(::handleLoginResult)
            }
        }
    }

    fun handleLoginResult(result: Result<ConnectionData>) {
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