package io.getstream.chat.ui.sample.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.logging.StreamLog
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.data.user.SampleUser

class SettingsViewModel: ViewModel() {

    private val _state = MutableLiveData<State>()
    private val _events = MutableLiveData<Event<UIEvent>>()
    private val logger = StreamLog.getLogger("Chat:SettingsViewModel")

    val state: LiveData<State> = _state
    val events: LiveData<Event<UIEvent>> = _events

    fun init() {
        val user = App.instance.userRepository.getUser()
        if (user != SampleUser.None) {
            displayUserProfile()
        }
    }

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.LogoutClicked -> _events.postValue(Event(UIEvent.RedirectToLogout))
            is UiAction.DeleteAccountClicked -> _events.postValue(Event(UIEvent.RedirectToDeleteAccount))
            is UiAction.UserClicked -> _events.postValue(Event(UIEvent.ChangeImageGallery))
        }
    }

    fun displayUserProfile() {

    }

    sealed class State {
        data class User(val user: SampleUser) : State()
    }

    sealed class UiAction {
        object UserClicked : UiAction()
        object LogoutClicked : UiAction()
        object DeleteAccountClicked : UiAction()
    }

    sealed class UIEvent {
        object RedirectToDeleteAccount : UIEvent()
        object RedirectToLogout : UIEvent()
        object ChangeImageGallery : UIEvent()
        data class ReloadUserProfileImage(val imageUrl: String?) : UIEvent()
        data class Error(val errorMessage: String?) : UIEvent()
    }
}