package io.getstream.chat.ui.sample.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.databinding.FragmentSettingsBinding

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
            text = getString(R.string.settings_logout)
            setOnClickListener { viewModel.onUiAction(SettingsViewModel.UiAction.LogoutClicked) }
        }

        with(binding.deleteTextView) {
            text = getString(R.string.settings_delete)
            setOnClickListener { viewModel.onUiAction(SettingsViewModel.UiAction.DeleteAccountClicked) }
        }
    }

    private fun observeStateAndEvents() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is SettingsViewModel.State.User -> {
                    binding.nameTextView.text = it.user.name
                }
            }
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is SettingsViewModel.UIEvent.RedirectToLogout -> {

                    }
                    is SettingsViewModel.UIEvent.ReloadUserProfileImage -> {

                    }
                    is SettingsViewModel.UIEvent.RedirectToDeleteAccount -> {

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
}