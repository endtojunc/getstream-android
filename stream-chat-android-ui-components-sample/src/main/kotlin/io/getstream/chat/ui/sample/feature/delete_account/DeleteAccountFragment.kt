package io.getstream.chat.ui.sample.feature.delete_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kongzue.dialogx.dialogs.MessageDialog
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.databinding.FragmentDeleteAccountBinding
import io.getstream.chat.ui.sample.feature.forgot.ForgotPasswordFragmentDirections
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
            navigateSafely(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToUserCustomLoginFragment())
        }

        binding.deleteButton.setOnClickListener {
            MessageDialog.show(
                getString(R.string.delete_account_message_dialog_title),
                getString(R.string.delete_account_message_dialog_message),
                getString(R.string.delete_account_message_dialog_yes),
                getString(R.string.delete_account_message_dialog_no)
            ).setOkButton { _, _ -> Boolean
                val username = binding.usernameEditText.text.toString()
                viewModel.onUiAction(DeleteAccountViewModel.UiAction.DeleteClicked(username = username))
                false
            }
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(viewLifecycleOwner,
            EventObserver {
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