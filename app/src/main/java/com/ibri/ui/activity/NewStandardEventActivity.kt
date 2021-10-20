package com.ibri.ui.activity

import android.annotation.SuppressLint
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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.core.widget.doOnTextChanged
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.ibri.R
import com.ibri.databinding.ActivityNewStandardEventBinding
import com.ibri.model.Media
import com.ibri.model.Tag
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class NewStandardEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStandardEventBinding
    private lateinit var pref: SharedPreferences
    private lateinit var volley: RequestQueue
    private val viewModel: StandardEventViewModel by viewModels()
    private lateinit var eventDay: Date
    private lateinit var startSubscription: Date
    private lateinit var selectedMedia: String
    private lateinit var eventTitle: String
    private lateinit var eventDescription: String
    private lateinit var address: String
    private lateinit var city: String
    private lateinit var lat: String
    private lateinit var lon: String
    private var eventHour: Int = 12
    private var eventMinute: Int = 0
    private var maxSubscribers: Int = 5
    lateinit var tags: List<Tag>
    private var isPrivate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStandardEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = PreferenceManager.getSharedPreferences(this)
        volley = Volley.newRequestQueue(this)

        setObservableVM()
        setListeners()
        prepareStage()
    }

    private fun setObservableVM() {
        viewModel.newStandEventResponse.value = ""
        viewModel.newStandEventResponse.observe(this) {
            if (it == "ok") {
                Toast.makeText(this, "EVENTO CREATO", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }

    private fun setListeners() {
        binding.newStandEventBackButton.setOnClickListener { onBackPressed() }
        binding.newEventSubmitButton.setOnClickListener { checkDataAndCommit() }
        binding.standEventTagLayout.setOnClickListener { pickTags() }
        binding.standEventDateLayout.setOnClickListener { pickDate() }
        binding.standEventPlaceLayout.setOnClickListener { pickLocation() }
        binding.standEventTimeLayout.setOnClickListener { pickTime() }
        binding.standEventAddImageLayout.setOnClickListener { pickImage() }
        binding.standEventSubscribersLayout.setOnClickListener { pickMaxSubscribers() }

        binding.toggleButtons.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.public_event_button -> isPrivate = false
                    R.id.private_event_button -> isPrivate = true
                }
            }
        }

        binding.inputStandEventTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0)
                    binding.itemStandardEvent.standEventTitle.text = "Titolo evento"
                else
                    binding.itemStandardEvent.standEventTitle.text = s

                eventTitle = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.inputStandEventDescription.doOnTextChanged { text, start, before, count ->
            eventDescription = text.toString()
        }
    }

    private fun prepareStage() {
        binding.publicEventButton.isChecked = true
        val avatarLink = pref.getString(PreferenceManager.ACCOUNT_AVATAR, "")
        val url = "$GET_MEDIA_ENDPOINT/$avatarLink"

        if (url.isEmpty()) {
            binding.itemStandardEvent.standEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        } else {
            Glide.with(this)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.itemStandardEvent.standEventCreatorAvatar)
        }

        val name = pref.getString(PreferenceManager.ACCOUNT_NAME, "") + " " +
                pref.getString(PreferenceManager.USER_SURNAME, "")
        binding.itemStandardEvent.standEventCreator.text = name
    }

    private fun pickTags() {
        val intent = Intent(this, TagsActivity::class.java)
        launcherTagActivity.launch(intent)
    }

    private fun pickLocation() {
        val intent = Intent(this, MapActivity::class.java)
        launcherMapActivity.launch(intent)
    }

    private fun pickImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        launcherImagePickerActivity.launch(photoPickerIntent)
    }

    private fun checkDataAndCommit() {
        if ((!this::lat.isInitialized) or
            (!this::lon.isInitialized) or
            (!this::eventTitle.isInitialized) or
            (!this::eventDescription.isInitialized) or
            (!this::selectedMedia.isInitialized) or
            (!this::tags.isInitialized)
        ) {
            //binding.newEventError.visibility = View.VISIBLE
        } else {
            //binding.newEventSubmitButton.visibility = View.GONE
            binding.newEventProgressBar.visibility = View.VISIBLE
            commitData()
        }
    }

    private fun commitData() {
        val userId = pref.getString(PreferenceManager.ACCOUNT_ID, "")!!
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        startSubscription.hours = eventHour
        startSubscription.minutes = eventMinute
        eventDay.hours = eventHour
        eventDay.minutes = eventMinute

        Log.wtf(LOG_TEST,"startSubscription -> ${simpleDateFormat.format(startSubscription)}")
        Log.wtf(LOG_TEST,"eventDay -> ${simpleDateFormat.format(eventDay)}")

        viewModel.createStandardEvent(
            userId,
            eventTitle,
            eventDescription,
            simpleDateFormat.format(startSubscription),
            simpleDateFormat.format(eventDay),
            maxSubscribers,
            lat,
            lon,
            address,
            city,
            Gson().toJson(tags),
            isPrivate,
            selectedMedia
        )
    }


    @SuppressLint("SetTextI18n")
    private fun pickDate() {
        val now = Calendar.getInstance()
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Scegli date")
                .setSelection(Pair.create(now.timeInMillis, now.timeInMillis))
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            startSubscription = Date(dateRangePicker.selection?.first!!)
            eventDay = Date(dateRangePicker.selection?.second!!)
            val cal: Calendar = Calendar.getInstance()
            cal.time = eventDay
            binding.itemStandardEvent.standEventDay.text = "${cal.get(Calendar.DAY_OF_MONTH)}"
            binding.itemStandardEvent.standEventMonth.text =
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        }

        dateRangePicker.show(this.supportFragmentManager, dateRangePicker.toString())
    }


    @SuppressLint("SetTextI18n")
    private fun pickTime() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(getString(R.string.ora_dell_evento))
                .build()
        picker.addOnPositiveButtonClickListener {
            eventHour = picker.hour
            eventMinute = picker.minute
            binding.eventTimeSelected.text = "${eventHour}:${eventMinute}"
        }

        picker.show(supportFragmentManager, "Event time")
    }

    @SuppressLint("SetTextI18n")
    private fun pickMaxSubscribers() {
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = 500
        numberPicker.minValue = 2
        numberPicker.value = 5

        val builder = AlertDialog.Builder(this)
        builder.setView(numberPicker)
        builder.setTitle(R.string.numero_massimo_di_persone)
        builder.setMessage("Scegli il numero")
        builder.setPositiveButton("OK") { dialog, which ->
            maxSubscribers = numberPicker.value
            binding.itemStandardEvent.standEventMembers.text = "0/${maxSubscribers}"
            binding.eventMaxPersonSelected.text = "0/${maxSubscribers}"
        }
        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showTags() {
        binding.eventTagsHint.visibility = View.GONE
        binding.eventSelectedTags.removeAllViews()
        for (tag in tags) {
            val chip = Chip(this)
            chip.isCheckable = false
            chip.text = tag.name
            chip.setChipBackgroundColorResource(R.color.orange_200)
            binding.eventSelectedTags.addView(chip)
        }
    }

    private var launcherTagActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    tags = it.data!!.getParcelableArrayListExtra(TagsActivity.TAG_KEY)!!
                    if (tags.isNotEmpty()) {
                        showTags()
                    }
                }
            }
        }

    private var launcherMapActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    binding.itemStandardEvent.standEventLocation.text =
                        it.data!!.getStringExtra(MapActivity.SELECTED_ADDRESS)

                    address = it.data!!.getStringExtra(MapActivity.SELECTED_ADDRESS).toString()
                    binding.standEventPlace.text = address

                    city = it.data!!.getStringExtra(MapActivity.SELECTED_CITY).toString()
                    lat = it.data!!.getStringExtra(MapActivity.LOCATION_LAT).toString()
                    lon = it.data!!.getStringExtra(MapActivity.LOCATION_LON).toString()
                }
            }
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
                    binding.itemStandardEvent.standEventImage.setImageBitmap(selectedImage)
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

}



