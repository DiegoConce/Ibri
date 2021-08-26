package com.ibri.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.R
import com.ibri.databinding.ActivityNewRoomBinding
import com.ibri.model.Media
import com.ibri.model.events.CommercialEvent
import com.ibri.ui.viewmodel.RoomViewModel
import com.ibri.utils.DataPart
import com.ibri.utils.PreferenceManager
import com.ibri.utils.UPLOAD_MEDIA_ENDPOINT
import com.ibri.utils.VolleyMultipartRequest
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.HashMap

class NewRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRoomBinding
    private val viewModel: RoomViewModel by viewModels()

    private lateinit var volley: RequestQueue
    private lateinit var pref: SharedPreferences
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var selectedMedia: String
    private lateinit var eventId: String
    private var maxMembers = 5
    private var eventMaxMembers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        volley = Volley.newRequestQueue(this)
        pref = PreferenceManager.getSharedPreferences(this)

        eventMaxMembers = intent.getIntExtra(MAX_MEMBERS, 0)
        eventId = intent.getStringExtra(EVENT_ID)!!

        setObservableVM()
        setListeners()
    }

    private fun setObservableVM() {
        viewModel.newRoomResponse.value = ""
        viewModel.newRoomResponse.observe(this) {
            if (it == "ok") {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setListeners() {
        binding.newRoomBackButton.setOnClickListener { onBackPressed() }
        binding.newRoomSubmitButton.setOnClickListener { checkDataAndCommit() }
        binding.newRoomAddImageLayout.setOnClickListener { pickImage() }
        binding.newRoomMaxMembersLayout.setOnClickListener { pickMaxMembers() }

        binding.inputNewRoomTitle.doOnTextChanged { text, start, before, count ->
            name = text.toString()
        }

        binding.inputNewRoomDescription.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }
    }

    private fun checkDataAndCommit() {
        if ((!this::name.isInitialized) or
            (!this::description.isInitialized) or
            (!this::selectedMedia.isInitialized)
        ) {
            binding.newRoomError.visibility = View.VISIBLE
        } else {
            commitData()
        }
    }

    private fun commitData() {
        val userId = pref.getString(PreferenceManager.ACCOUNT_ID, "")!!
        viewModel.createRoom(eventId, name, description, maxMembers, userId, selectedMedia)
    }

    private fun pickMaxMembers() {
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = eventMaxMembers
        numberPicker.minValue = 2
        numberPicker.value = 5

        val builder = AlertDialog.Builder(this)
        builder.setView(numberPicker)
        builder.setTitle("Max. persone")
        builder.setMessage("Scegli numero")
        builder.setPositiveButton("OK") { dialog, which ->
            maxMembers = numberPicker.value
            binding.newRoomMaxMembersPreview.text = "1/${maxMembers}"
        }
        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


    private fun pickImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        launcherImagePickerActivity.launch(photoPickerIntent)
    }

    private var launcherImagePickerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri: Uri? = it.data?.data
                    val imageStream: InputStream? = imageUri?.let { uri ->
                        contentResolver.openInputStream(uri)
                    }

                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    val req = object : VolleyMultipartRequest(
                        Method.POST,
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
                    binding.roomDetailsImage.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun registerMedia(it: NetworkResponse?) {
        if (it?.data != null) {
            val media = Gson().fromJson(String(it.data), Media::class.java)
            selectedMedia = media.id
//            if (media != null) {
//                Glide.with(this)
//                    .load(ServerRequests.MEDIA_GET_ENDPOINT.toString() + "/" + media.url)
//                    .error(R.drawable.background_test)
//                    .into(event_preview_image)
//                event_preview_add_image_btn.visibility = View.GONE
//                event_preview_image.visibility = View.VISIBLE
//            }
        }
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
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

    companion object {
        val EVENT_ID = "EVENT_ID"
        val MAX_MEMBERS = "MAX_MEMBERS"
    }
}