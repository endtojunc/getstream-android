package io.getstream.chat.ui.sample.feature.otp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kongzue.dialogx.dialogs.WaitDialog
import io.getstream.chat.android.livedata.utils.EventObserver
import io.getstream.chat.ui.sample.R
import io.getstream.chat.ui.sample.common.showToast
import io.getstream.chat.ui.sample.databinding.FragmentOtpBinding

class OTPFragment: Fragment() {

    private var _binding: FragmentOtpBinding? = null
    protected val binding get() = _binding!!

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
        binding.getCodeButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val countryCode = binding.ccp.selectedCountryCode

            WaitDialog.show("Please wait")
            viewModel.onUiAction(OTPViewModel.UiAction.getCodeClicked(phoneNumber = phoneNumber, countryCode = countryCode))
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                WaitDialog.dismiss()
                when (it) {
                    is OTPViewModel.UiEvent.ShowToast -> {
                        showToast(it.message)
                        if (it.isSuccess) {
                            binding.getCodeButton.isEnabled = false
                            val timer = object: CountDownTimer(60000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    binding.getCodeButton.text = String.format("%ds", millisUntilFinished / 1000)
                                }

                                override fun onFinish() {
                                    binding.getCodeButton.text = getString(R.string.otp_get_code)
                                    binding.getCodeButton.isEnabled = true
                                }
                            }
                            timer.start()
                        }
                    }
                }
            }
        )
    }

    fun getCountryCode(): String {
        return binding.ccp.selectedCountryCode
    }

    fun getPhoneNo(): String {
        return binding.phoneNumberEditText.text.toString()
    }

    fun getOtp(): String {
        return binding.otpEditText.text.toString()
    }
}