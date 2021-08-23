package com.ibri.ui.event

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ibri.databinding.FragmentSearchBinding
import com.ibri.databinding.LayoutSeekbarThumbBinding
import com.ibri.model.Tag
import com.ibri.ui.adapters.TagAdapter
import com.ibri.ui.adapters.TagOnClickListener
import com.ibri.ui.viewmodel.CommercialEventViewModel
import com.ibri.ui.viewmodel.SearchViewModel
import com.ibri.ui.viewmodel.StandardEventViewModel
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.ibri.utils.LOG_TEST


class SearchFragment : Fragment(), TagOnClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var bindingSeekBarThumb: LayoutSeekbarThumbBinding
    private lateinit var tagAdapter: TagAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SearchViewModel by activityViewModels()
    private val standEventVM: StandardEventViewModel by activityViewModels()
    private val comEventVM: CommercialEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        bindingSeekBarThumb = LayoutSeekbarThumbBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel.getTags()
        setObservableVM()
        setListeners()
        return binding.root
    }

    private fun setObservableVM() {
        viewModel.tagList.observe(viewLifecycleOwner) {
            setTagRecyclerView(it)
        }
    }

    private fun setListeners() {
        binding.searchBackButton.setOnClickListener { requireActivity().onBackPressed() }


        binding.searchSeekBar.progress = viewModel.distanceInM.value!! / 1000
        binding.searchSeekBar.thumb = getThumb(binding.searchSeekBar.progress)
        binding.searchSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.searchSeekBar.thumb = getThumb(progress)
                viewModel.distanceInM.value = progress * 1000
                reloadEvents()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun getThumb(progess: Int): Drawable {
        bindingSeekBarThumb.tvProgress.text = progess.toString() + "Km"
        bindingSeekBarThumb.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            bindingSeekBarThumb.root.measuredWidth,
            bindingSeekBarThumb.root.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        bindingSeekBarThumb.root.layout(
            0, 0,
            bindingSeekBarThumb.root.measuredWidth,
            bindingSeekBarThumb.root.measuredHeight,
        )
        bindingSeekBarThumb.root.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    private fun reloadEvents() {
        val location = viewModel.location.value
        if (viewModel.isStandardEvent.value == true) {
            standEventVM.getStandEventByPosition(
                location?.latitude.toString(),
                location?.longitude.toString(),
                viewModel.distanceInM.value!!
            )
        } else {
            comEventVM.getCommercialEventsByPosition(
                location?.latitude.toString(),
                location?.longitude.toString(),
                viewModel.distanceInM.value!!
            )
        }
    }

    private fun setTagRecyclerView(it: ArrayList<Tag>) {
        tagAdapter = TagAdapter(this)
        tagAdapter.setData(it)
        gridLayoutManager =
            GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding.tagRecyclerView.layoutManager = gridLayoutManager
        binding.tagRecyclerView.adapter = tagAdapter
    }

    override fun onTagCliked(item: Tag) {
        viewModel.selectedTag.value = item
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToSearchDetailFragment())
    }

    override fun onResume() {
        val location = viewModel.location.value

        if (viewModel.isStandardEvent.value == true) {
            standEventVM.getStandEventByPosition(
                location?.latitude.toString(),
                location?.longitude.toString(),
                viewModel.distanceInM.value!!
            )
        } else {
            comEventVM.getCommercialEventsByPosition(
                location?.latitude.toString(),
                location?.longitude.toString(),
                viewModel.distanceInM.value!!
            )
        }
        super.onResume()
    }
}