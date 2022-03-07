package com.codesample.checker.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codesample.checker.TrackedListFragment
import com.codesample.checker.SearchFragment

const val SEARCH_PAGE_INDEX = 0
const val CHECKED_LIST_PAGE_INDEX = 1

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        SEARCH_PAGE_INDEX to { SearchFragment() },
        CHECKED_LIST_PAGE_INDEX to { TrackedListFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}