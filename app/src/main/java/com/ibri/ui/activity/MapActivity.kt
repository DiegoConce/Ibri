package com.ibri.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ibri.R
import com.ibri.databinding.ActivityMapBinding
import com.ibri.utils.LOG_TEST
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentMarkerPosition: LatLng
    private lateinit var geocoder: Geocoder

    private var mapReady = false
    private var selectedAddress = ""
    private var selectedCity = ""
    private var errorMessage = ""

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            perms.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    // Permission is granted
                } else {
                    // Permission is denied
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.map_frame_layout, mapFragment)
            .commit()
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        mapReady = true

        map.isMyLocationEnabled = true
        map.uiSettings.isCompassEnabled = false
        binding.selectedAddressTextView.visibility = View.GONE

        showMyPosition()
        setListeners()
    }

    private fun setListeners() {
        binding.mapButtonSubmit.setOnClickListener { saveLocation() }

        map.setOnCameraIdleListener {
            binding.selectedAddressTextView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            updatePosition()
        }
        map.setOnCameraMoveListener {
            binding.selectedAddressTextView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun saveLocation() {
        if (selectedAddress.isNotEmpty()) {
            val intent = Intent()
                .putExtra(SELECTED_ADDRESS, selectedAddress)
                .putExtra(SELECTED_CITY, selectedCity)
                .putExtra(LOCATION_LAT, currentMarkerPosition.latitude.toString())
                .putExtra(LOCATION_LON, currentMarkerPosition.longitude.toString())
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Scegli qualche INDIRIZ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePosition() {
        var addresses = mutableListOf<Address>()
        currentMarkerPosition = map.cameraPosition.target

        geocoder = Geocoder(this, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(
                currentMarkerPosition.latitude,
                currentMarkerPosition.longitude,
                1
            )

        } catch (e: IOException) {
            errorMessage = getString(R.string.controlla_connessione)
            binding.progressBar.visibility = View.GONE
            binding.selectedAddressTextView.visibility = View.VISIBLE
            binding.selectedAddressTextView.text = errorMessage
            Log.wtf(LOG_TEST, "errore $e")
        }

        if (addresses.isNullOrEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.nessun_indirizzo_trovato)
                binding.progressBar.visibility = View.GONE
                binding.selectedAddressTextView.visibility = View.VISIBLE
                binding.selectedAddressTextView.text = errorMessage
                Log.wtf(LOG_TEST, "addresses isNullOrEmpty $errorMessage")
            }
        } else {
            val address = addresses[0]

            for (i in 0..address.maxAddressLineIndex) {
                if (i == address.maxAddressLineIndex) {
                    selectedAddress = address.getAddressLine(i)
                    if (!address.locality.isNullOrEmpty()) {
                        selectedCity = address.locality
                    }
                } else {
                    selectedAddress = address.getAddressLine(i) + ","
                }
            }

            updateUi()
            Log.wtf(LOG_TEST, "Indirizzo trovato \n selectedAddress: $selectedAddress")
            Log.wtf(LOG_TEST, "Indirizzo trovato \n CITY: $selectedCity")
        }

    }

    private fun updateUi() {
        binding.progressBar.visibility = View.GONE
        binding.selectedAddressTextView.visibility = View.VISIBLE
        map.clear()
        binding.selectedAddressTextView.text = selectedAddress
    }

    fun showMyPosition() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener(this) { location ->
                        if (location != null) {
                            setAnimatedPosition(LatLng(location.latitude, location.longitude), 10f)
                            addMarker(location.latitude, location.longitude, "test")!!
                        }
                    }
            }
            //shouldShowRequestPermissionRationale
            else -> {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun setAnimatedPosition(pos: LatLng, zoom: Float) {
        val cameraPosition = CameraPosition.Builder()
            .target(pos)
            .zoom(zoom).build()
        //Zoom in and animate the camera.
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addMarker(
        lat: Double,
        long: Double,
        title: String,
        drawable: Drawable? = null
    ): Marker? {
        map.apply {
            val point = LatLng(lat, long)
            return addMarker(
                MarkerOptions()
                    .position(point)
                    .title(title)
                    .icon(bitmapDescriptorFromVector(drawable))
            )
        }
    }


    private fun bitmapDescriptorFromVector(drawable: Drawable?): BitmapDescriptor? {
        return drawable?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    companion object {
        val SELECTED_ADDRESS = "SELECTED_ADDRESS"
        val SELECTED_CITY = "SELECTED_CITY"
        val LOCATION_LAT = "LOCATION_LAT"
        val LOCATION_LON = "LOCATION_LON"
    }
}