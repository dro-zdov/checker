package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codesample.checker.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

}