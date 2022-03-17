package com.codesample.checker.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.File

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("imageFromFile")
fun bindImageFromFile(view: ImageView, file: File?) {
    if (file != null) {
        Glide.with(view.context)
            .load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}