package com.codesample.checker

import android.app.SearchManager
import android.content.Context
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.codesample.checker.adapters.SearchAdsAdapter
import com.codesample.checker.databinding.FragmentSearchBinding
import com.codesample.checker.utils.SnackbarUtil
import com.codesample.checker.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    @Inject lateinit var snackbarUtil: SnackbarUtil
    private val viewModel by viewModels<SearchViewModel>()
    private val adapter = SearchAdsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupSearchView(binding.searchView)
        binding.adsList.adapter = adapter

        lifecycleScope.launch {
            viewModel.items.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                handleLoadStates(binding.root, loadStates)
            }
        }

        return binding.root
    }

    private fun setupSearchView(searchView: SearchView) {
        val activity = requireActivity()
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchConfig = searchManager.getSearchableInfo(activity.componentName)

        searchView.setSearchableInfo(searchConfig)

        val scAdapter = SimpleCursorAdapter(
            activity,
            R.layout.suggestion_item,
            createMatrixCursor(),
            arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
            intArrayOf(R.id.suggestion),
            0
        )

        searchView.suggestionsAdapter = scAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus() // hide keyboard
                viewModel.setQueryText(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setSuggestionText(newText)
                if (newText.isEmpty()) {
                    viewModel.setQueryText(null)
                }
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int) = false

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = scAdapter.cursor
                cursor.moveToFirst()
                cursor.move(position)
                val query = cursor.getString(1)
                searchView.setQuery(query, true)
                return true
            }
        })

        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            val cursor = createMatrixCursor()
            suggestions.forEachIndexed { i, suggestion ->
                cursor.addRow(arrayOf(i, suggestion))
            }
            scAdapter.swapCursor(cursor)
        }
    }

    private fun createMatrixCursor() = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

    private fun handleLoadStates(root: View, loadStates: CombinedLoadStates) = with(loadStates) {
        listOf(refresh, append, prepend).forEach { loadState ->
            if (loadState is LoadState.Error) {
                snackbarUtil.showLoadError(root, loadState.error)
                if (adapter.itemCount == 0) {
                    viewModel.setQueryText(null) //Clear error after showing it
                }
            }
        }
    }

}