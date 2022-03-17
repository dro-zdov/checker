package com.codesample.checker.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codesample.checker.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

@BindingAdapter("textFromTimestamp")
fun bindTextFromTimestamp(view: TextView, timestamp: Int?) {
    if (timestamp != null) {
        val date = Date(timestamp.toLong() * 1000)
        val sdf = SimpleDateFormat(
            view.context.getString(R.string.date_format),
            Locale.getDefault()
        )
        view.text = sdf.format(date)
    }
}