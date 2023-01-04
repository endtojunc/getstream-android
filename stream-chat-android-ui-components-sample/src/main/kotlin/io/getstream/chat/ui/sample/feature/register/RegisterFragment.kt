package io.getstream.chat.ui.sample.feature.register

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.common.addChildFragment
import io.getstream.chat.ui.sample.common.navigateSafely
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.databinding.FragmentRegisterBinding
import io.getstream.chat.ui.sample.feature.otp.OTPFragment

class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterUserViewModel by viewModels()
    private val otpFragment = OTPFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
            navigateSafely(RegisterFragmentDirections.actionRegisterFragmentToUserCustomLoginFragment())
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val streamrole = "admin"
            val phoneNo = otpFragment.getPhoneNo()
            val countryCode = otpFragment.getCountryCode()
            val otp = otpFragment.getOtp()
            viewModel.onUiAction(RegisterUserViewModel.UiAction.registerClicked(name = name, username = username, password = password, phoneNo = phoneNo, countryCode = countryCode, generateOtp = otp, role = streamrole))
        }
    }

    private fun observeStateAndEvents() {

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterUserViewModel.State.User -> {

                }
            }
        }

        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is RegisterUserViewModel.UiEvent.RedirectToLogin -> {
                        showToast("User created successfully.")
                        navigateSafely(RegisterFragmentDirections.actionRegisterFragmentToUserCustomLoginFragment())
                    }
                    is RegisterUserViewModel.UiEvent.Error -> {
                        binding.errorTitle.text = it.errorMessage
                        binding.errorTitle.visibility = View.VISIBLE
                    }
                }
            }
        )
    }
}