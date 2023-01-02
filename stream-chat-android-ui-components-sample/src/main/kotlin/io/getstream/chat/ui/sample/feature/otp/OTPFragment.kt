package io.getstream.chat.ui.sample.feature.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.databinding.FragmentOtpBinding

class OTPFragment: Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OTPViewModel by viewModels()
    private var countryCode = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupView()
        observeStateAndEvents()
    }

    private fun setupView() {
        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val countryCode = binding.ccp.selectedCountryCode

        binding.getCodeButton.setOnClickListener { viewModel.onUiAction(OTPViewModel.UiAction.getCodeClicked(phoneNumber = phoneNumber, countryCode = countryCode)) }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is OTPViewModel.UiEvent.ShowToast -> {
                        showToast(it.message)
                    }
                }
            }
        )
    }

    public fun getOtp(): String {
        return binding.otpEditText.text.toString()
    }
}