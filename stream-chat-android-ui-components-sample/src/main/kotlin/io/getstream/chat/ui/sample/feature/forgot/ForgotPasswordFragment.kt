package io.getstream.chat.ui.sample.feature.forgot

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.common.addChildFragment
import io.getstream.chat.ui.sample.databinding.FragmentForgotPasswordBinding
import io.getstream.chat.ui.sample.feature.otp.OTPFragment

class ForgotPasswordFragment: Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val otpFragment = OTPFragment()

    private val viewModel: ForgotUserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        addChildFragment(binding.otpView.id, otpFragment)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeStateAndEvents()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_delete)
        binding.toolbar.setNavigationOnClickListener {

        }

        binding.registerButton.setOnClickListener {
            val newPassword = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            binding.registerButton.setOnClickListener { viewModel.onUiAction(ForgotUserViewModel.UiAction.resetClicked(username = "", password = newPassword, confirmPasssword = confirmPassword, generateOtp = "")) }
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is ForgotUserViewModel.UiEvent.RedirectToLogin -> {}
                }
            }
        )
    }
}