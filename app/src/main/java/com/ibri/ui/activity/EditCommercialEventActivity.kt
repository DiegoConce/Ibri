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
import com.ibri.databinding.ActivityEditCommercialEventBinding
import com.ibri.model.Media
import com.ibri.model.events.CommercialEvent
import com.ibri.ui.viewmodel.CommercialEventViewModel
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

class EditCommercialEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCommercialEventBinding
    private val viewModel: CommercialEventViewModel by viewModels()
    private lateinit var comEvent: CommercialEvent
    private lateinit var volley: RequestQueue

    private var calendar: Calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val hoursMinutesDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private lateinit var description: String
    private lateinit var startSubscription: Date
    private lateinit var selectedMedia: String
    private lateinit var eventDay: Date
    private var maxSubscribers: Int = 0
    private var maxRooms: Int = 0
    private lateinit var lat: String
    private lateinit var lon: String
    private lateinit var address: String
    private lateinit var city: String
    private var eventHour: Int = 0
    private var eventMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCommercialEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        volley = Volley.newRequestQueue(this)
        comEvent = intent.getParcelableExtra(EDIT_COM_EVENT)!!

        prepareStage()
        setObservableVM()
        setListeners()
    }

    private fun setObservableVM() {
        viewModel.deleteComEventResponse.value = ""
        viewModel.deleteComEventResponse.observe(this) {
            if (it == "ok") {
                setResult(RESULT_CANCELED, intent)
                finish()
            }
        }

        viewModel.editComEventResponse.value = ""
        viewModel.editComEventResponse.observe(this) {
            if (it == "ok") {
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setListeners() {
        binding.editComBackButton.setOnClickListener { onBackPressed() }
        binding.editComSubmitButton.setOnClickListener { checkDataAndCommit() }
        binding.editComDeleteLayout.setOnClickListener { deleteEvent() }
        binding.editComAddImage.setOnClickListener { pickImage() }
        binding.editComPlaceLayout.setOnClickListener { pickLocation() }
        binding.editComDateLayout.setOnClickListener { pickDate() }
        binding.editComTimeLayout.setOnClickListener { pickTime() }
        binding.editComSubscribersLayout.setOnClickListener { pickMaxSubscribers() }
        binding.editComMaxRoomsLayout.setOnClickListener { pickMaxRooms() }

        binding.editComInputDescription.doOnTextChanged { text, start, before, count ->
            description = text.toString()
        }
    }

    private fun prepareStage() {
        calendar.time = comEvent.eventDay
        binding.itemComEvent.comEventCreator.text = comEvent.creator.name
        binding.itemComEvent.comEventTitle.text = comEvent.title
        binding.itemComEvent.comEventCity.text = comEvent.city
        binding.itemComEvent.comEventLocation.text = comEvent.address
        binding.itemComEvent.comEventMembers.text = "${comEvent.guests} / ${comEvent.maxGuests}"
        binding.itemComEvent.comEventDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        binding.itemComEvent.comEventMonth.text = SimpleDateFormat("MMM", Locale.getDefault())
            .format(calendar.time)
        binding.itemComEvent.comEventRooms.text = "${comEvent.rooms.size} / ${comEvent.maxRooms}"
        showImages()

        binding.editComInputDescription.setText(comEvent.description)
        binding.comEventPlace.text = comEvent.address
        binding.eventMaxPersonSelected.text = "${comEvent.guests} / ${comEvent.maxGuests}"
        binding.eventTimeSelected.text = hoursMinutesDateFormat.format(comEvent.eventDay)
        maxSubscribers = comEvent.maxGuests
        maxRooms = comEvent.maxRooms

    }

    private fun showImages() {
        if (comEvent.media != null) {
            val path = comEvent.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(this)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.itemComEvent.comEventImage)
        }

        if (comEvent.creator.avatar != null) {
            val path = comEvent.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(this)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(binding.itemComEvent.comEventCreatorAvatar)
        } else {
            binding.itemComEvent.comEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
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
            binding.itemComEvent.comEventDay.text = "${calendar.get(Calendar.DAY_OF_MONTH)}"
            binding.itemComEvent.comEventMonth.text =
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
            binding.itemComEvent.comEventMembers.text =
                "${comEvent.guests}/ ${maxSubscribers}"
            binding.eventMaxPersonSelected.text = "${comEvent.guests}/ ${maxSubscribers}"
        }
        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun pickMaxRooms() {
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = 500
        numberPicker.minValue = 2
        numberPicker.value = 5

        val builder = AlertDialog.Builder(this)
        builder.setView(numberPicker)
        builder.setTitle(R.string.numero_massimo_di_persone)
        builder.setMessage("Scegli il numero")
        builder.setPositiveButton("OK") { dialog, which ->
            maxRooms = numberPicker.value
            binding.itemComEvent.comEventRooms.text = "0/${maxRooms}"
            binding.eventMaxRoomsSelected.text = "0/${maxRooms}"
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
        //maxSubs maxRooms?
        if (!this::lat.isInitialized)
            lat = comEvent.lat

        if (!this::lon.isInitialized)
            lon = comEvent.lon

        if (!this::address.isInitialized)
            address = comEvent.address

        if (!this::city.isInitialized)
            city = comEvent.city

        if (!this::description.isInitialized)
            description = comEvent.description

        if (!this::eventDay.isInitialized)
            eventDay = comEvent.eventDay

        if (!this::startSubscription.isInitialized)
            startSubscription = comEvent.startSubscription

        if (!this::selectedMedia.isInitialized)
            selectedMedia = ""

        commitData()
    }

    private fun commitData() {
        viewModel.editCommercialEvent(
            description,
            simpleDateFormat.format(startSubscription),
            simpleDateFormat.format(eventDay),
            maxSubscribers,
            maxRooms,
            lat,
            lon,
            address,
            city,
            selectedMedia,
            comEvent.id
        )
    }

    private fun deleteEvent() {
        val builder = MaterialAlertDialogBuilder(this)
        if (isItPossibleToDelete()) {
            builder.setMessage(getString(R.string.sei_sicuro_di_voler_cancellare_questo_evento))
                .setCancelable(false)
                .setPositiveButton("SI") { _, _ ->
                    viewModel.deleteCommercialEvent(comEvent.id)
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
        return getDifferenceDays(Date(), comEvent.eventDay) >= 2
    }

    private fun getDifferenceDays(today: Date, eDate: Date): Long {
        val diff = eDate.time - today.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    private var launcherMapActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    address = it.data!!.getStringExtra(MapActivity.SELECTED_ADDRESS).toString()
                    binding.comEventPlace.text = address
                    binding.itemComEvent.comEventLocation.text = address

                    city = it.data!!.getStringExtra(MapActivity.SELECTED_CITY).toString()
                    binding.itemComEvent.comEventCity.text = city
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
                    binding.itemComEvent.comEventImage.setImageBitmap(selectedImage)
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
        val EDIT_COM_EVENT = "EDIT_COM_EVENT"
    }
}