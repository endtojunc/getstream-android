package io.getstream.chat.ui.sample.feature.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.logging.StreamLog

class RegisterUserViewModel: ViewModel() {

    private val _state = MutableLiveData<State>()
    private val _events = MutableLiveData<Event<UiEvent>>()
    private val networkWorker = NetworkWorker()
    private val logger = StreamLog.getLogger("Chat:RegisterViewModel")

    val state: LiveData<State> = _state
    val events: LiveData<Event<UiEvent>> = _events

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.registerClicked -> {
                registerUser(name = action.name, username = action.username, password = action.password, phoneNo = action.phoneNo, countryCode = action.countryCode, generateOtp = action.generateOtp, role = action.role)
            }
        }
    }

    fun registerUser(name: String, username: String, password: String, phoneNo: String, countryCode: String, generateOtp: String, role: String) {
        networkWorker.registerUser(username = username, name = name, password = password, phoneNo = phoneNo, countryCode = countryCode, generateOtp = generateOtp, streamRole = role, callback = {
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
        data class registerClicked(val name: String, val username: String, val password: String, val phoneNo: String, val countryCode: String, val generateOtp: String, val role: String): UiAction()
    }

    sealed class UiEvent {
        object RedirectToLogin: UiEvent()
        data class Error(val errorMessage: String?) : UiEvent()
    }
}