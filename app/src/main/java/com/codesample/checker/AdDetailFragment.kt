package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.codesample.checker.AdDetailFragment.Callback
import com.codesample.checker.adapters.AdDetailsAdapter
import com.codesample.checker.databinding.FragmentAdDetailBinding
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.viewmodels.AdDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdDetailFragment : Fragment() {
    private val args: AdDetailFragmentArgs by navArgs()
    private val viewModel: AdDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAdDetailBinding.inflate(inflater, container, false)

        viewModel.setId(args.adId)

        viewModel.history.observe(viewLifecycleOwner) { allHistory ->
            val adapter = AdDetailsAdapter(allHistory)
            val headItem = allHistory[0]
            binding.headItem = headItem.details
            binding.isTracked = headItem.rowId != null
            binding.adDetails.adapter = adapter
            binding.executePendingBindings()
        }

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.callback = Callback { adDetails, isTracked ->
            if (isTracked) {
                viewModel.deleteAdDetails(adDetails)
            }
            else {
                viewModel.saveAdDetails(adDetails)
            }
        }

        return binding.root
    }

    fun interface Callback {
        fun add(details: AdDetails, isTracked: Boolean)
    }

}