package com.ibri.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ibri.ui.profile.ProfileEventsFragment

class ProfilePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private var pages: Int = 2

    override fun getItemCount(): Int {
        return pages
    }

    override fun createFragment(position: Int): Fragment {
        return ProfileEventsFragment.newInstance(position)
    }
}