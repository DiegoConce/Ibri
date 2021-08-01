package com.ibri.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ibri.MainActivity
import com.ibri.R
import com.ibri.databinding.FragmentProfileSettingsBinding
import com.ibri.model.Media
import com.ibri.model.enum.Role
import com.ibri.ui.viewmodel.ProfileViewModel
import com.ibri.utils.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileSettingsFragment : Fragment() {
    private lateinit var binding: FragmentProfileSettingsBinding
    private lateinit var pref: SharedPreferences
    private val viewModel: ProfileViewModel by activityViewModels()
    private val resultLoadImg: Int = 1
    private lateinit var volley: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        pref = PreferenceManager.getSharedPreferences(requireContext())
        volley = Volley.newRequestQueue(requireContext())
        viewModel.clearFields()
        restoreData()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.editUserResponse.observe(viewLifecycleOwner) {
            if (it == "success") {
                Log.wtf(LOG_TEST,"Modifica effettuata con successo")
                DataPreloader.loadPersonalInfo()
                requireActivity().onBackPressed()
            }
        }

        viewModel.editCompanyResponse.observe(viewLifecycleOwner) {
            if (it == "success") {
                Log.wtf(LOG_TEST,"Modifica effettuata con successo")
                DataPreloader.loadPersonalInfo()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setListeners() {
        binding.settingsBackButton.setOnClickListener { requireActivity().onBackPressed() }
        binding.fabEditAvatar.setOnClickListener { changeAvatar() }
        binding.settingsLogoutButton.setOnClickListener { logout() }
        binding.settingsSaveButton.setOnClickListener { saveChanges() }
    }

    private fun saveChanges() {
        if (pref.contains(PreferenceManager.ACCOUNT_ID)) {
            val accountId = pref.getString(PreferenceManager.ACCOUNT_ID, "")
            viewModel.inputName = binding.profileEditName.text.toString()
            viewModel.inputSurname = binding.profileEditSurname.text.toString()
            viewModel.inputBio = binding.profileEditBio.text.toString()
            viewModel.userId = accountId!!

            if (viewModel.isCompany.value == true)
                viewModel.performEditCompany()
            else
                viewModel.performEditUser()
        }
    }

    private fun checkValues(): Boolean {
        var passed = true

        if (binding.profileEditName.text.isNullOrEmpty()) {
            binding.nameField.error = "Non puoi inserire un nome vuoto"
            passed = false
        }

        if (binding.profileEditSurname.text.isNullOrEmpty()) {
            binding.surnameField.error = "Non puo inserire un cognome vuoto"
            passed = false
        }

        return passed
    }

    private fun changeAvatar() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, resultLoadImg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == resultLoadImg) {
                try {
                    val imageUri: Uri? = data?.data
                    val imageStream: InputStream? = imageUri?.let {
                        requireActivity().contentResolver.openInputStream(it)
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val req = object : VolleyMultipartRequest(
                        Request.Method.POST,
                        UPLOAD_MEDIA_ENDPOINT.toString(),
                        {
                            registerMedia(it)
                        },
                        {
                            it
                        }) {
                        override fun getByteData(): MutableMap<String, DataPart> {
                            val map = HashMap<String, DataPart>()
                            if (imageUri != null) {
                                val fileName = getFileName(imageUri)
                                map["file"] = DataPart(
                                    fileName,
                                    getFileDataFromDrawable(selectedImage)
                                )
                            }
                            return map
                        }
                    }
                    volley.add(req)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun registerMedia(it: NetworkResponse?) {
        if (it?.data != null) {
            val media = Gson().fromJson(String(it.data), Media::class.java)
            if (media != null) {
                with(pref.edit()) {
                    putString(PreferenceManager.ACCOUNT_AVATAR, media.url)
                    putString(PreferenceManager.ACCOUNT_AVATAR_ID, media.id)
                    apply()
                }
                updateProfile(media)
            }
        }
    }

    private fun updateProfile(media: Media) {
        val req = object : StringRequest(Request.Method.POST,
            MEDIA_UPDATE_ENDPOINT.toString(),
            {
                if (!it.equals("success")) {
                    //todo: handle server error
                } else {
                    DataPreloader.loadPersonalInfo()
                }
            },
            {
                //todo: handle connection/server error
            }) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["media"] = media.id
                map["user_id"] =
                    pref.getString(PreferenceManager.ACCOUNT_ID, "").toString()
                return map
            }
        }
        volley.add(req)
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                requireActivity().contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun restoreData() {
        if (pref.contains(PreferenceManager.ACCOUNT_ID)) {
            setAvatar()

            binding.profileEditName.setText(pref.getString(PreferenceManager.ACCOUNT_NAME, ""))
            if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") == Role.USER.toString()) {
                binding.profileEditSurname.setText(
                    pref.getString(PreferenceManager.USER_SURNAME, "")
                )
                binding.profileEditBio.setText(pref.getString(PreferenceManager.ACCOUNT_BIO, ""))
            } else if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") == Role.COMPANY.toString()
            ) {
                binding.profileEditBio.setText(pref.getString(PreferenceManager.ACCOUNT_BIO, ""))
                binding.surnameField.visibility = View.GONE
            }
        }
    }

    private fun setAvatar() {
        val path = pref.getString(PreferenceManager.ACCOUNT_AVATAR, "")

        if (path.isNullOrEmpty())
            binding.settingsAvatar.setImageResource(R.drawable.default_avatar)

        val url = "$GET_MEDIA_ENDPOINT/$path"
        Glide.with(requireContext())
            .load(url)
            .error(R.drawable.default_avatar)
            .into(binding.settingsAvatar)
    }

    private fun logout() {
        with(pref.edit()) {
            clear()
            apply()
        }
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}