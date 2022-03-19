package com.codesample.checker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codesample.checker.R
import com.codesample.checker.databinding.AdDetailsItemBinding
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.Image
import java.io.File

class AdDetailsAdapter(
    private val history: List<AdDetailsContainer>
): RecyclerView.Adapter<AdDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdDetailsViewHolder {
        return AdDetailsViewHolder(
            AdDetailsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdDetailsViewHolder, position: Int) {
        holder.bind(history[position])
    }

    override fun getItemCount() = history.size
}

class AdDetailsViewHolder(
    private val binding: AdDetailsItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AdDetailsContainer) {
        binding.adDetails = item.details
        recreateImageViews(binding, item)
        binding.executePendingBindings()
    }

    private fun recreateImageViews(binding: AdDetailsItemBinding, container: AdDetailsContainer) {
        val mainLayout = binding.mainLayout
        val context = mainLayout.context
        val topViewId = binding.price.id
        val columnsCount = 4

        //Remove old views
        var oldImageView: ImageView?
        while (null != mainLayout.findViewWithTag<ImageView>(IMAGE_VIEW_TAG).also { oldImageView = it }) {
            mainLayout.removeView(oldImageView)
        }

        val collection = container.files.ifEmpty {
            container.details.images ?: return
        }

        val imageViews = collection.map {
            val imageView = ImageView(context)
            imageView.id = View.generateViewId()
            imageView.tag = IMAGE_VIEW_TAG
            imageView.layoutParams = getImageLayoutParams(context)
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
            val item = collection[i]
            Glide.with(context).run {
                if (item is Image) {
                    load(item.img100x75)
                }
                else {
                    load(item as File)
                }
            }
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
        }
    }

    private fun getImageLayoutParams(context: Context): ConstraintLayout.LayoutParams {
        val layout = LayoutInflater.from(context).inflate(R.layout.ad_detail_image, null) as ConstraintLayout
        val view = layout.findViewById<ImageView>(R.id.imagePlaceholder)
        return view.layoutParams as ConstraintLayout.LayoutParams
    }

    companion object {
        private const val IMAGE_VIEW_TAG = "imageViewTag"
    }
}