package io.getstream.chat.ui.sample.feature.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.common.NetworkWorker

class OTPViewModel: ViewModel() {

    private val _events = MutableLiveData<Event<UiEvent>>()
    val events: LiveData<Event<UiEvent>> = _events
    val networkWorker = NetworkWorker()

    fun onUiAction(action: UiAction) {
        when(action) {
            is UiAction.getCodeClicked -> getOtp(action.phoneNumber, action.countryCode)
            is UiAction.countryCodeClicked -> {}
        }
    }

    fun getOtp(phoneNumber: String, countryCode: String) {
        networkWorker.generateOTP(phoneNumber, countryCode, callback = {
            if (it.isSuccess) {
                _events.postValue(Event(UiEvent.ShowToast(isSuccess = true, message = "OTP successfully sent")))
            } else {
                _events.postValue(Event(UiEvent.ShowToast(isSuccess = false, message = "Error")))
            }
        })
    }

    sealed class UiAction {
        data class getCodeClicked(val phoneNumber: String, val countryCode: String): UiAction()
        object countryCodeClicked: UiAction()
    }

    sealed class UiEvent {
        data class ShowToast(val isSuccess: Boolean, val message: String): UiEvent()
    }
}