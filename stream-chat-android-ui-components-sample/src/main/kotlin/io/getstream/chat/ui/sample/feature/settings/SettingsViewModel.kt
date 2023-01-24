package io.getstream.chat.ui.sample.feature.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.setup.state.ClientState
import io.getstream.chat.android.livedata.utils.Event
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.common.NetworkWorker
import io.getstream.chat.ui.sample.data.user.SampleUser
import io.getstream.logging.StreamLog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SettingsViewModel(
    private val chatClient: ChatClient = ChatClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
): ViewModel() {

    private val _state: MediatorLiveData<UiState> = MediatorLiveData()
    private val _events = MutableLiveData<Event<UIEvent>>()
    private val logger = StreamLog.getLogger("Chat:SettingsViewModel")

    val state: LiveData<UiState> = Transformations.distinctUntilChanged(_state)
    val events: LiveData<Event<UIEvent>> = _events

    val networkWorker = NetworkWorker()

    fun init() {
        _state.addSource(clientState.user.asLiveData()) { user ->
            setState { copy(user = user ?: User()) }
        }
    }

    private fun setState(reducer: UiState.() -> UiState) {
        _state.value = reducer(_state.value ?: UiState())
    }

    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.LogoutClicked -> {
                viewModelScope.launch {
                    chatClient.disconnect(true).await()
                    App.instance.userRepository.clearUser()
                    _events.postValue(Event(UIEvent.RedirectToLogout))
                }
            }
            is UiAction.DeleteAccountClicked -> _events.postValue(Event(UIEvent.RedirectToDeleteAccount))
            is UiAction.UserClicked -> _events.postValue(Event(UIEvent.ChangeImageGallery))
            is UiAction.UploadImage -> {
                val file = File(getRealPathFromURI(action.imageUri, action.context))
                networkWorker.uploadFile(file, callback = {
                    var currentUser = _state.value?.user
                    it.response?.get("imageUrl")?.let { image ->
                        if (currentUser != null) {
                            currentUser.image = image as String
                            chatClient.updateUser(currentUser).enqueue {
                                if(it.isSuccess) {
                                    val updatedImage = it.data().image
                                    val storedUser = App.instance.userRepository.getUser()
                                    App.instance.userRepository.setUser(
                                        SampleUser(
                                            apiKey = storedUser.apiKey,
                                            id = storedUser.id,
                                            name = storedUser.name,
                                            token = storedUser.token,
                                            image = updatedImage
                                        )
                                    )
                                    setState { copy(it.data()) }
                                    _events.postValue(Event(UIEvent.ReloadUserProfileImage(user = it.data())))
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    data class UiState(val user: User = User())

    sealed class UiAction {
        object UserClicked : UiAction()
        object LogoutClicked : UiAction()
        object DeleteAccountClicked : UiAction()
        data class UploadImage(val imageUri: Uri, val context: Context): UiAction()
    }

    sealed class UIEvent {
        object RedirectToDeleteAccount : UIEvent()
        object RedirectToLogout : UIEvent()
        object ChangeImageGallery : UIEvent()
        data class ReloadUserProfileImage(val user: User) : UIEvent()
        data class Error(val errorMessage: String?) : UIEvent()
    }

    fun getRealPathFromURI(uri: Uri, context: Context): String? {
        val file = File(context.filesDir, "uploadImage.png")
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)

        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }
}