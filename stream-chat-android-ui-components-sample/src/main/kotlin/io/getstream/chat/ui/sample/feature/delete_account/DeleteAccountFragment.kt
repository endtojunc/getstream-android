package io.getstream.chat.ui.sample.feature.delete_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.application.App
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.data.user.UserRepository
import io.getstream.chat.ui.sample.databinding.FragmentDeleteAccountBinding
import io.getstream.chat.ui.sample.feature.home.HomeFragmentDirections

class DeleteAccountFragment: Fragment() {

    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeleteAccountViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeStateAndEvents()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            navigateSafely(DeleteAccountFragmentDirections.actionToSettingsFragment())
        }

        binding.deleteButton.setOnClickListener {
            MessageDialog.show(
                getString(R.string.delete_account_message_dialog_title),
                getString(R.string.delete_account_message_dialog_message),
                getString(R.string.delete_account_message_dialog_yes),
                getString(R.string.delete_account_message_dialog_no)
            ).setOkButton { _, _ -> Boolean
                WaitDialog.show("Please wait")
                val username = binding.usernameEditText.text.toString()
                val user = App.instance.userRepository.getUser()
                if (user.id != username) {
                    showToast(getString(R.string.delete_account_username_mismatch))
                } else {
                    viewModel.onUiAction(DeleteAccountViewModel.UiAction.DeleteClicked(username = username))
                }
                false
            }
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(viewLifecycleOwner,
            EventObserver {
                WaitDialog.dismiss()
                when(it) {
                    is DeleteAccountViewModel.UiEvent.RedirectToLogin -> {
                        navigateSafely(HomeFragmentDirections.actionToUserCustomLoginFragment())
                    }
                    is DeleteAccountViewModel.UiEvent.Error -> {
                        binding.errorTitle.text = it.errorMessage
                        binding.errorTitle.visibility = View.VISIBLE
                    }
                }
            }
        )
    }
}