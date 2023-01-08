package io.getstream.chat.ui.sample.feature.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kongzue.dialogx.dialogs.WaitDialog
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.common.addChildFragment
import io.getstream.chat.ui.sample.common.navigateSafely
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
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            navigateSafely(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToUserCustomLoginFragment())
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val newPassword = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            WaitDialog.show("Please wait")
            viewModel.onUiAction(ForgotUserViewModel.UiAction.resetClicked(username = username,
                password = newPassword,
                confirmPasssword = confirmPassword,
                generateOtp = ""))
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                WaitDialog.dismiss()
                when (it) {
                    is ForgotUserViewModel.UiEvent.RedirectToLogin -> {
                        navigateSafely(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToUserCustomLoginFragment())
                    }
                    is ForgotUserViewModel.UiEvent.Error -> {
                        binding.errorTitle.visibility = View.VISIBLE
                        binding.errorTitle.text = it.errorMessage
                    }
                }
            }
        )
    }
}