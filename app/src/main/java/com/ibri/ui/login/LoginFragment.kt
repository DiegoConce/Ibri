package com.ibri.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.util.LogTime
import com.ibri.R
import com.ibri.databinding.FragmentLoginBinding
import com.ibri.utils.DataPreloader
import com.ibri.utils.LOG_TEST

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.company != null) {
                    DataPreloader.registerCompany(it.company)
                    viewModel.loginSuccess.value = true
                } else if (it.user != null) {
                    DataPreloader.registerUser(it.user)
                    viewModel.loginSuccess.value = true
                }
            }

            if (it == null)
                viewModel.loginSuccess.value = false
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(LoginFragmentDirections.actionLoginToBottomNavFragment())
            else {
                binding.loginEmailField.error = "Controlla se hai inserito una email valida"
                binding.loginPasswordField.error = "Controlla se hai inserito una password valida"
            }
        }
    }

    private fun setListeners() {
        binding.loginRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registerUser)
        }

        binding.loginSignInButton.setOnClickListener {
            viewModel.inputEmail = binding.loginEmail.text.toString()
            viewModel.inputPassword = binding.loginPassword.text.toString()
            if (checkValues())
                viewModel.performLogin()
        }
    }

    private fun checkValues(): Boolean {
        return if (viewModel.inputEmail.isEmpty() || viewModel.inputPassword.isEmpty()) {
            binding.loginEmailField.error = "Perfavore inserisci una email"
            binding.loginPasswordField.error = "Perfavore inserisci una password"
            false
        } else
            true
    }

}