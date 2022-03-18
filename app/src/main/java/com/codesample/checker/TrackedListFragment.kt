package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.codesample.checker.adapters.TrackedListAdapter
import com.codesample.checker.databinding.FragmentTrackedListBinding
import com.codesample.checker.utils.SnackbarUtil
import com.codesample.checker.viewmodels.TrackedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackedListFragment : Fragment() {
    @Inject lateinit var snackbarUtil: SnackbarUtil
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

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                handleLoadStates(binding.root, loadStates)
            }
        }

        return binding.root
    }

    private fun handleLoadStates(root: View, loadStates: CombinedLoadStates) = with(loadStates) {
        listOf(refresh, append, prepend).forEach { loadState ->
            if (loadState is LoadState.Error) {
                snackbarUtil.showLoadError(root, loadState.error)
            }
        }
    }
}