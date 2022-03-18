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
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.utils.SnackbarUtil
import com.codesample.checker.viewmodels.AdDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdDetailFragment : Fragment() {
    @Inject lateinit var snackbarUtil: SnackbarUtil
    private val args: AdDetailFragmentArgs by navArgs()
    private val viewModel: AdDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAdDetailBinding.inflate(inflater, container, false)

        viewModel.setId(args.adId)

        viewModel.history.observe(viewLifecycleOwner) { dataOrException ->
            with(dataOrException) {
                if (data != null) {
                    handleData(binding, data)
                }
                if (exception != null) {
                    handleException(binding, exception)
                }
            }
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

    private fun handleData(binding: FragmentAdDetailBinding, allHistory: List<AdDetailsContainer>) {
        val adapter = AdDetailsAdapter(allHistory)
        val headItem = allHistory[0]
        binding.headItem = headItem.details
        binding.isTracked = headItem.rowId != null
        binding.adDetails.adapter = adapter
        binding.executePendingBindings()
    }

    private fun handleException(binding: FragmentAdDetailBinding, exception: Exception) {
        snackbarUtil.showLoadError(binding.root, exception)
    }

    fun interface Callback {
        fun add(details: AdDetails, isTracked: Boolean)
    }

}