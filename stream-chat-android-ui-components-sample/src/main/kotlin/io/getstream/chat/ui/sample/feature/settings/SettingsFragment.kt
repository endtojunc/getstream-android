package io.getstream.chat.ui.sample.feature.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.databinding.FragmentSettingsBinding
import io.getstream.chat.ui.sample.feature.home.HomeFragmentDirections
import io.getstream.chat.ui.sample.feature.register.RegisterFragmentDirections

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeStateAndEvents()
        viewModel.init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        with(binding.logoutTextView) {
            text = getString(R.string.home_drawer_sign_out)
            setOnClickListener { viewModel.onUiAction(SettingsViewModel.UiAction.LogoutClicked) }
        }

        with(binding.deleteTextView) {
            text = getString(R.string.delete_account_title)
            setOnClickListener { viewModel.onUiAction(SettingsViewModel.UiAction.DeleteAccountClicked) }
        }

        with(binding.userAvatar) {
            setOnClickListener {
                openImagePicker()
            }
        }
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    private fun observeStateAndEvents() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.UiState -> {
                    binding.nameTextView.text = it.user.name
                    binding.userAvatar.setUserData(it.user)
                }
            }
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is SettingsViewModel.UIEvent.RedirectToLogout -> {
                        requireActivity().findNavController(R.id.hostFragmentContainer)
                            .navigateSafely(HomeFragmentDirections.actionToUserCustomLoginFragment())
                    }
                    is SettingsViewModel.UIEvent.ReloadUserProfileImage -> {
                        binding.userAvatar.setUserData(it.user)
                    }
                    is SettingsViewModel.UIEvent.RedirectToDeleteAccount -> {
                        requireActivity().findNavController(R.id.hostFragmentContainer)
                            .navigateSafely(HomeFragmentDirections.actionToDeleteAccountFragment())
                    }
                    is SettingsViewModel.UIEvent.ChangeImageGallery -> {

                    }
                    is SettingsViewModel.UIEvent.Error -> {
                        showToast(it.errorMessage ?: getString(R.string.backend_error_info))
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            context?.let { SettingsViewModel.UiAction.UploadImage(imageUri = uri, context = it) }
                ?.let { viewModel.onUiAction(it) }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            showToast(ImagePicker.getError(data))
        } else {
            showToast("Task Cancelled")
        }
    }


}