package com.hsvibe.viewadapters

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hsvibe.R

/**
 * Created by Vincent on 2021/7/13.
 */
object GlideBinding {

    private val options: RequestOptions by lazy {
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_place_holder_circle).centerCrop()
    }

    private val optionsWithAutomaticCache: RequestOptions by lazy {
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.ic_place_holder_circle).centerCrop()
    }

    @JvmStatic
    @BindingAdapter("glideImage")
    fun setGlideImage(imageView: ImageView, imageUrl: String?) {
        imageUrl?.let {
            Glide.with(imageView.context)
                .load(it)
                .apply(options)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("backgroundByGlide")
    fun setBackgroundByGlide(view: View, @DrawableRes drawableRes: Int) {
        Glide.with(view.context)
            .load(drawableRes)
            .apply(optionsWithAutomaticCache)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.let { view.background = it }
                }
            })
    }
}