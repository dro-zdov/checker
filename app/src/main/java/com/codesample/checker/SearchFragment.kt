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
import androidx.lifecycle.Observer
import com.codesample.checker.databinding.FragmentSearchBinding
import com.codesample.checker.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupSearchView(binding.searchView)

        return binding.root
    }

    private fun setupSearchView(searchView: SearchView) {
        val activity = requireActivity()
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchConfig = searchManager.getSearchableInfo(activity.componentName)

        searchView.setSearchableInfo(searchConfig)

        val adapter = SimpleCursorAdapter(
            activity,
            R.layout.suggestion_item,
            createMatrixCursor(),
            arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
            intArrayOf(R.id.suggestion),
            0
        )

        searchView.suggestionsAdapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus() // hide keyboard
                handleQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    viewModel.searchSuggestions(null)
                    clearQuery()
                } else {
                    viewModel.searchSuggestions(newText)
                }
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int) = false

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = adapter.cursor
                cursor.moveToFirst()
                cursor.move(position)
                val query = cursor.getString(1)
                searchView.setQuery(query, true)
                return true
            }
        })

        viewModel.suggestions.observe(viewLifecycleOwner, Observer {
            val cursor = createMatrixCursor()
            val lastIdx = it.size - 1
            for (i in 0..lastIdx) {
                cursor.addRow(arrayOf(i, it[i]))
            }
            adapter.swapCursor(cursor)
        })
    }

    private fun createMatrixCursor(): MatrixCursor {
        return MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
    }

    private fun handleQuery(query: String) {
        //TODO: Handle query
    }

    private fun clearQuery() {
        //TODO: Clear query
    }
}