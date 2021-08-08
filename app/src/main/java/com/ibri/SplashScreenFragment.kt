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
import com.ibri.utils.DataPreloader
import com.ibri.utils.PreferenceManager

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
        animateTitle()
    }

    private fun animateTitle() {
        binding.splashAppLogo.scaleX = 0.2F
        binding.splashAppLogo.scaleY = 0.2F
        binding.splashAppLogo.animate()
            .withEndAction {
                findNavController().popBackStack(R.id.action_splashScreenFragment_to_bottomNavFragment, true)
                checkLogged()
            }
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .start()
    }

    private fun handlerAndGoToMain() =
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().popBackStack(
                R.id.action_splashScreenFragment_to_bottomNavFragment,
                true
            )
            //findNavController().navigate(SplashScreenFragmentD)
            //  findNavController().navigate(R.id.action_splashScreenFragment_to_bottomNavFragment)

            checkLogged()

        }, 1000)


    private fun checkLogged() {
        val sharedPref = PreferenceManager.getSharedPreferences(requireContext())
        if (sharedPref.getString(PreferenceManager.ACCOUNT_ID, "") == "") {
            findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLogin())
        } else {
            DataPreloader.loadPersonalInfo()
            findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToBottomNavFragment())
        }
    }
}