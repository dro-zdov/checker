package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.codesample.checker.adapters.SearchAdsAdapter
import com.codesample.checker.adapters.TrackedListAdapter
import com.codesample.checker.databinding.FragmentTrackedListBinding
import com.codesample.checker.viewmodels.SearchViewModel
import com.codesample.checker.viewmodels.TrackedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackedListFragment : Fragment() {
    private val viewModel by viewModels<TrackedListViewModel>()
    private val adapter = TrackedListAdapter()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTrackedListBinding.inflate(inflater, container, false)

        binding.tackedList.adapter = adapter

        lifecycleScope.launch {
            viewModel.allLatest.collectLatest {
                adapter.submitData(it)
            }
        }

        return binding.root
    }

}