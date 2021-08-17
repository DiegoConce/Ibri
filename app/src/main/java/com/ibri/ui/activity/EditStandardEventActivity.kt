package com.ibri.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.ibri.R
import com.ibri.databinding.ActivityEditStandardEventBinding
import com.ibri.model.Media
import com.ibri.model.events.StandardEvent
import com.ibri.ui.viewmodel.StandardEventViewModel
import com.ibri.utils.DataPart
import com.ibri.utils.GET_MEDIA_ENDPOINT
import com.ibri.utils.UPLOAD_MEDIA_ENDPOINT
import com.ibri.utils.VolleyMultipartRequest
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditStandardEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditStandardEventBinding
    private val viewModel: StandardEventViewModel by viewModels()
    private lateinit var standEvent: StandardEvent
    private lateinit var volley: RequestQueue

    private var calendar: Calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val hoursMinutesDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var description: String
    private lateinit var startSubscription: Date
    private lateinit var selectedMedia: String
    private lateinit var eventDay: Date
    private var maxSubscribers: Int = 0
    private lateinit var lat: String
    private lateinit var lon: String
    private lateinit var address: String
    private lateinit var city: String
    private var eventHour: Int = 0
    private var eventMinute: Int = 0
    private var isPrivate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStandardEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        volley = Volley.newRequestQueue(this)
        standEvent = intent.getParcelableExtra(EDIT_STAND_EVENT)!!

        prepareStage()
        setObservableVM()
        setListeners()
    }

    private fun setObservableVM() {
        viewModel.deleteStandEventResponse.value = ""
        viewModel.deleteStandEventResponse.observe(this) {
            if (it == "ok") {
                setResult(RESULT_CANCELED, intent)
                finish()
            }
        }

        viewModel.editStandEventResponse.value = ""
        viewModel.editStandEventResponse.observe(this) {
            if (it == "ok") {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setListeners() {
        binding.editStandBackButton.setOnClickListener { onBackPressed() }
        binding.editStandSubmitButton.setOnClickListener { checkDataAndCommit() }
        binding.editStandDeleteLayout.setOnClickListener { deleteEvent() }
        binding.editStandPlaceLayout.setOnClickListener { pickLocation() }
        binding.editStandDateLayout.setOnClickListener { pickDate() }
        binding.editStandTimeLayout.setOnClickListener { pickTime() }
        binding.editStandSubscribersLayout.setOnClickListener { pickMaxSubscribers() }
        binding.editStandAddImage.setOnClickListener { pickImage() }

        binding.editStandDescriptionInput.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }

        binding.toggleButtons.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.edit_stand_public_button -> isPrivate = false
                    R.id.edit_stand_private_button -> isPrivate = true
                }
            }
        }
    }

    private fun prepareStage() {
        binding.itemStandardEvent.standEventCreator.text = standEvent.creator.name
        binding.itemStandardEvent.standEventTitle.text = standEvent.title
        binding.itemStandardEvent.standEventLocation.text = standEvent.city
        binding.itemStandardEvent.standEventMembers.text =
            "${standEvent.guests} / ${standEvent.maxGuests}"
        binding.itemStandardEvent.standEventDay.text =
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        binding.itemStandardEvent.standEventMonth.text =
            SimpleDateFormat("MMM", Locale.getDefault())
                .format(calendar.time)

        showImages()

        binding.editStandDescriptionInput.setText(standEvent.description)
        binding.standEventPlace.text = standEvent.address
        binding.eventMaxPersonSelected.text = "${standEvent.guests} / ${standEvent.maxGuests}"
        binding.eventTimeSelected.text = hoursMinutesDateFormat.format(standEvent.eventDay)

        maxSubscribers = standEvent.maxGuests
        isPrivate = standEvent.private
        if (isPrivate) {
            binding.editStandPrivateButton.isChecked = true
            binding.editStandPublicButton.isChecked = false
        } else {
            binding.editStandPublicButton.isChecked = true
            binding.editStandPrivateButton.isChecked = false
        }
    }

    private fun showImages() {
        if (standEvent.media != null) {
            val path = standEvent.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(this)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.itemStandardEvent.standEventImage)
        }

        if (standEvent.creator.avatar != null) {
            val path = standEvent.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(this)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.itemStandardEvent.standEventCreatorAvatar)
        } else {
            binding.itemStandardEvent.standEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        }
    }


    private fun pickLocation() {
        val intent = Intent(this, MapActivity::class.java)
        launcherMapActivity.launch(intent)
    }

    private fun pickDate() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Scegli date")
                .setSelection(Pair.create(calendar.timeInMillis, calendar.timeInMillis))
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            startSubscription = Date(dateRangePicker.selection?.first!!)
            eventDay = Date(dateRangePicker.selection?.second!!)
            calendar.time = eventDay
            binding.itemStandardEvent.standEventDay.text = "${calendar.get(Calendar.DAY_OF_MONTH)}"
            binding.itemStandardEvent.standEventMonth.text =
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
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
            binding.itemStandardEvent.standEventMembers.text =
                "${standEvent.guests}/ ${maxSubscribers}"
            binding.eventMaxPersonSelected.text = "${standEvent.guests}/ ${maxSubscribers}"
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


    private fun checkDataAndCommit() {
        if (!this::lat.isInitialized)
            lat = standEvent.lat

        if (!this::lon.isInitialized)
            lon = standEvent.lon

        if (!this::address.isInitialized)
            address = standEvent.address

        if (!this::city.isInitialized)
            city = standEvent.city

        if (!this::description.isInitialized)
            description = standEvent.description

        if (!this::eventDay.isInitialized)
            eventDay = standEvent.eventDay

        if (!this::startSubscription.isInitialized)
            startSubscription = standEvent.startSubscription

        if (!this::selectedMedia.isInitialized)
            selectedMedia = ""

        commitData()
    }

    private fun commitData() {
        viewModel.editStandardEvent(
            description,
            simpleDateFormat.format(startSubscription),
            simpleDateFormat.format(eventDay),
            maxSubscribers,
            lat,
            lon,
            address,
            city,
            isPrivate,
            selectedMedia,
            standEvent.id
        )
    }

    private fun deleteEvent() {
        val builder = MaterialAlertDialogBuilder(this)
        if (isItPossibleToDelete()) {
            builder.setMessage(getString(R.string.sei_sicuro_di_voler_cancellare_questo_evento))
                .setCancelable(false)
                .setPositiveButton("SI") { _, _ ->
                    viewModel.deleteStandardEvent(standEvent.id)
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
            builder.show()
        } else {
            builder.setMessage(
                "Purtroppo non puoi cancellare un evento se mancano " +
                        "meno di 2 giorni all'evento"
            )
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
            builder.show()
        }
    }

    private fun isItPossibleToDelete(): Boolean {
        return getDifferenceDays(Date(), standEvent.eventDay) >= 2
    }

    private fun getDifferenceDays(today: Date, eDate: Date): Long {
        val diff = eDate.time - today.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
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

    private val launcherImagePickerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

    companion object {
        val EDIT_STAND_EVENT = "EDIT_STAND_EVENT"
    }
}