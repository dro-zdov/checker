package com.codesample.checker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codesample.checker.databinding.FragmentAdDetailBinding
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.viewmodels.AdDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class AdDetailFragment : Fragment() {
    private val args: AdDetailFragmentArgs by navArgs()
    private val viewModel: AdDetailsViewModel by viewModels()
    @Inject lateinit var repository: AdDetailsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAdDetailBinding.inflate(inflater, container, false)

        viewModel.getAdDetails(args.adId).observe(viewLifecycleOwner) { allHistory ->
            binding.adDetails = allHistory[0].details
            binding.isTracked = allHistory[0].rowId != null
            binding.executePendingBindings()
            addImageViews(binding, allHistory[0].details)
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

    private fun addImageViews(binding: FragmentAdDetailBinding, details: AdDetails) {
        val mainLayout = binding.mainLayout
        val topViewId = binding.price.id
        val columnsCount = 4

        val imageViews = details.images.map {
            val imageView = ImageView(requireContext())
            imageView.id = View.generateViewId()
            imageView.layoutParams = getImageLayoutParams()
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            mainLayout.addView(imageView)
            imageView
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(mainLayout)
        imageViews.forEachIndexed { i, imageView ->
            val topEnd = when(i.div(columnsCount)) {
                0 -> topViewId
                else -> imageViews[i - columnsCount].id
            }
            val leftEnd = when(i.mod(columnsCount)) {
                0 -> ConstraintSet.PARENT_ID
                1 -> R.id.guideline1
                2 -> R.id.guideline2
                3 -> R.id.guideline3
                else -> throw IllegalStateException("Never happens")
            }
            val rightEnd = when(i.mod(columnsCount)) {
                0 -> R.id.guideline1
                1 -> R.id.guideline2
                2 -> R.id.guideline3
                3 -> ConstraintSet.PARENT_ID
                else -> throw IllegalStateException("Never happens")
            }

            constraintSet.connect(imageView.id, ConstraintSet.TOP, topEnd, ConstraintSet.BOTTOM)
            constraintSet.connect(imageView.id, ConstraintSet.LEFT, leftEnd, ConstraintSet.LEFT)
            constraintSet.connect(imageView.id, ConstraintSet.RIGHT, rightEnd, ConstraintSet.RIGHT)
        }
        constraintSet.applyTo(mainLayout)

        imageViews.forEachIndexed { i, imageView ->
            Glide.with(requireContext())
                .load(details.images[i].img100x75)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }

    private fun getImageLayoutParams(): ConstraintLayout.LayoutParams {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.ad_detail_image, null) as ConstraintLayout
        val view = layout.findViewById<ImageView>(R.id.imagePlaceholder)
        return view.layoutParams as ConstraintLayout.LayoutParams
    }

    fun interface Callback {
        fun add(details: AdDetails, isTracked: Boolean)
    }
}