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
import androidx.lifecycle.lifecycleScope
import com.codesample.checker.adapters.SearchAdsAdapter
import com.codesample.checker.databinding.FragmentSearchBinding
import com.codesample.checker.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()
    private val adapter = SearchAdsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupSearchView(binding.searchView)
        binding.adsList.adapter = adapter

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

        lifecycleScope.launch {
            viewModel.items.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun createMatrixCursor() = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

}