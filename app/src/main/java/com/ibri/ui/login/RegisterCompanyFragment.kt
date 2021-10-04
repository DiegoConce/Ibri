package com.ibri.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.ibri.R
import com.ibri.databinding.FragmentRegisterCompanyBinding
import com.ibri.model.Company
import com.ibri.utils.DataPreloader

class RegisterCompanyFragment : Fragment() {
    private lateinit var binding: FragmentRegisterCompanyBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterCompanyBinding.inflate(inflater, container, false)

        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.registerCompanyResponse.observe(viewLifecycleOwner) {
            if (it == "email occupata") {
                binding.registerCompanyError.text = getString(R.string.email_gia_in_uso)
                binding.registerCompanyError.visibility = View.VISIBLE
            } else {
                val gson = GsonBuilder().setDateFormat("MMM dd, yyyy, hh:mm:ss a").create()
                val company = gson.fromJson(it, Company::class.java)
                DataPreloader.registerCompany(company)
                findNavController().navigate(R.id.action_registerCompany_to_bottomNavFragment)
            }
        }
    }

    private fun setListeners() {
        binding.registerCompanyBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.registerCompanySubmitBtn.setOnClickListener {
            if (checkValues()) {
                viewModel.performRegisterCompany(
                    binding.registerCompanyName.text.toString(),
                    binding.registerCompanyEmail.text.toString(),
                    binding.registerCompanyPiva.text.toString(),
                    binding.registerCompanyPassword.text.toString()
                )
            }
        }
    }

    private fun checkValues(): Boolean {
        var passed = true

        if (binding.registerCompanyName.text.isNullOrEmpty()) {
            binding.registerCompanyName.error = getString(R.string.inserisci_il_tuo_nome)
            passed = false
        }

        /*
        if (!binding.registerCompanyEmail.text.isNullOrEmpty()) {
            val pattern: Pattern =
                Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
            val mat: Matcher = pattern.matcher(binding.registerCompanyEmail.text.toString())
            if (!mat.matches()) {
                binding.registerCompanyEmail.error = getString(R.string.verifica_email)
                passed = false
            } else {
                binding.registerCompanyEmail.error = getString(R.string.inserisci_email)
                passed = false
            }
        }*/

        if (binding.registerCompanyPiva.text.isNullOrEmpty()) {
            binding.registerCompanyPiva.error = getString(R.string.inserisci_piva)
            passed = false
        }

        if (binding.registerCompanyPiva.text?.length != 11) {
            binding.registerCompanyPiva.error = getString(R.string.verifica_dati_inseriti)
            passed = false
        }

        /*
        if (!binding.registerCompanyPassword.text.isNullOrEmpty())
            if (!binding.registerCompanyConfirmPassword.text.isNullOrEmpty()) {
                val pass = binding.registerCompanyPassword.text.toString().trim()
                val passConfirmation =
                    binding.registerCompanyConfirmPassword.text.toString().trim()
                if (pass != passConfirmation) {
                 //   binding.registerError.text = getString(R.string.password_non_corrispondono)
                //    binding.registerError.visibility = View.VISIBLE
                    passed = false
                } else {
                //    binding.registerError.text = getString(R.string.inserisci_password)
                 //   binding.registerError.visibility = View.VISIBLE
                }
            }*/

        return passed
    }
}