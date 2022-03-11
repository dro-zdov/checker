package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codesample.checker.adapters.CHECKED_LIST_PAGE_INDEX
import com.codesample.checker.adapters.SEARCH_PAGE_INDEX
import com.codesample.checker.adapters.ViewPagerAdapter
import com.codesample.checker.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            SEARCH_PAGE_INDEX -> R.drawable.ic_baseline_search
            CHECKED_LIST_PAGE_INDEX -> R.drawable.ic_baseline_art_track
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            SEARCH_PAGE_INDEX -> getString(R.string.search_tab_title)
            CHECKED_LIST_PAGE_INDEX -> getString(R.string.tracked_list_tab_title)
            else -> null
        }
    }
}