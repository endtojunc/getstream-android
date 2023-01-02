package io.getstream.chat.ui.sample.feature.user_custom_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.databinding.FragmentLoginBinding

class UserCustomLoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserCustomLoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeStateAndEvents()
    }

    private fun setupViews() {
        binding.forgotTextView.setOnClickListener { viewModel.onUiAction(UserCustomLoginViewModel.UiAction.forgotClicked) }
        binding.registerTextView.setOnClickListener { viewModel.onUiAction(UserCustomLoginViewModel.UiAction.registerClicked) }
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.onUiAction(UserCustomLoginViewModel.UiAction.loginClicked(username, password)) }
    }

    private fun observeStateAndEvents() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is UserCustomLoginViewModel.State.User -> {
                    
                }
            }
        }

        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is UserCustomLoginViewModel.UiEvent.RedirectToRegister -> {
                        navigateSafely(UserCustomLoginFragmentDirections.actionUserCustomLoginFragmentToRegisterFragment())
                    }
                    is UserCustomLoginViewModel.UiEvent.RedirectToChannelList -> {
                        navigateSafely(UserCustomLoginFragmentDirections.actionUserLoginFragmentToHomeFragment())
                    }
                    is UserCustomLoginViewModel.UiEvent.RedirectToForgot -> {
                        navigateSafely(UserCustomLoginFragmentDirections.actionUserCustomLoginFragmentToForgotPasswordFragment())
                    }
                    is UserCustomLoginViewModel.UiEvent.Error -> {
                        binding.errorTitle.visibility = View.VISIBLE
                        binding.errorTitle.text = it.errorMessage
                    }
                }
            }
        )
    }
}