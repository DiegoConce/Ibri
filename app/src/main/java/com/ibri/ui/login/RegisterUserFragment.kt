package com.ibri.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.GsonBuilder
import com.ibri.R
import com.ibri.databinding.FragmentRegisterUserBinding
import com.ibri.model.User
import com.ibri.model.enum.Gender
import com.ibri.utils.DataPreloader
import java.util.*

//Da mettere conferma pass
class RegisterUserFragment : Fragment() {
    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterUserBinding.inflate(inflater, container, false)

        setObservableVM()
        setListener()
        setGenderDropDown()
        return binding.root
    }

    private fun setListener() {
        binding.registerUserBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.companyRegisterLink.setOnClickListener { findNavController().navigate(R.id.action_registerUser_to_registerCompany) }
        binding.registerUserBirthday.setOnClickListener { showDatePicker() }

        binding.registerSubmitButton.setOnClickListener {
            if (checkValues()) {
                viewModel.performRegisterUser(
                    binding.registerUserName.text.toString().trim(),
                    binding.registerUserSurname.text.toString().trim(),
                    binding.registerUserBirthday.text.toString().trim(),
                    binding.registerGender.text.toString().trim(),
                    binding.registerUserEmail.text.toString().trim(),
                    binding.registerUserPassword.text.toString().trim(),
                )
            }
        }
    }

    private fun setObservableVM() {
        viewModel.registerUserResponse.observe(viewLifecycleOwner) {
            if (it == "email occupata") {
                binding.registerError.text = getString(R.string.email_gia_in_uso)
                binding.registerError.visibility = View.VISIBLE
            } else {
                val a = it
                val gson = GsonBuilder().setDateFormat("MMM dd, yyyy, hh:mm:ss a").create()
                val user = gson.fromJson(it, User::class.java)
                DataPreloader.registerUser(user)
                findNavController().navigate(R.id.action_registerUser_to_bottomNavFragment)
            }
        }
    }

    private fun setGenderDropDown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_list, Gender.values())
        binding.registerGender.setAdapter(adapter)
    }

    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.scegli_una_data))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/" +
                    "${calendar.get(Calendar.MONTH)}/" +
                    "${calendar.get(Calendar.YEAR)}"
            binding.registerUserBirthday.setText(date)
        }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun checkValues(): Boolean {
        var passed = true

        if (binding.registerUserName.text.isNullOrEmpty()) {
            binding.registerUserName.error = getString(R.string.inserisci_il_tuo_nome)
            passed = false
        }

        if (binding.registerUserSurname.text.isNullOrEmpty()) {
            binding.registerUserSurname.error = getString(R.string.inserisci_il_tuo_cognome)
            passed = false
        }

        if (binding.registerUserBirthday.text.isNullOrEmpty()) {
            binding.registerUserName.error = getString(R.string.inserisci_compleanno)
            passed = false
        }

        if (binding.registerGender.text.isNullOrEmpty())
            passed = false

        /*  if (!binding.registerUserEmail.text.isNullOrEmpty()) {
              val pattern: Pattern =
                  Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
              val mat: Matcher = pattern.matcher(binding.registerUserEmail.text.toString())
              if (!mat.matches()) {
                  binding.registerUserName.error = getString(R.string.verifica_email)
                  passed = false
              } else {
                  binding.registerUserName.error = getString(R.string.inserisci_email)
                  passed = false
              }
          }

          if (!binding.registerUserPassword.text.isNullOrEmpty())
              if (!binding.registerUserPasswordConfirmation.text.isNullOrEmpty()) {
                  val pass = binding.registerUserPassword.text.toString().trim()
                  val passConfirmation =
                      binding.registerUserPasswordConfirmation.text.toString().trim()
                  if (pass != passConfirmation) {
                      binding.registerError.text = getString(R.string.password_non_corrispondono)
                      binding.registerError.visibility = View.VISIBLE
                      passed = false
                  } else {
                      binding.registerError.text = getString(R.string.inserisci_password)
                      binding.registerError.visibility = View.VISIBLE
                  }
              }*/

        return passed
    }
}