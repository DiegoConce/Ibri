package com.ibri

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ibri.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlerAndGoToMain()
    }

    private fun handlerAndGoToMain() =
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().popBackStack(R.id.action_splashScreenFragment_to_bottomNavFragment, true)
            //findNavController().navigate(SplashScreenFragmentD)
            findNavController().navigate(R.id.action_splashScreenFragment_to_bottomNavFragment)
        }, 1000)

}