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
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.utilities.DrawableUtil
import kotlinx.coroutines.*

/**
 * Created by Vincent on 2021/7/13.
 */
object GlideBinding {

    private val scope by lazy {
        CoroutineScope(Dispatchers.Main + CoroutineName("GlideBinding") + SupervisorJob())
    }

    private val options: RequestOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_place_holder_circle)
            .error(R.drawable.ic_place_holder_empty_image)
            .centerCrop()
    }

    private val optionsFitCenter: RequestOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_place_holder_circle)
            .error(R.drawable.ic_place_holder_empty_image)
            .fitCenter()
    }

    private val optionsWithAutomaticCache: RequestOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_place_holder_circle)
            .error(R.drawable.ic_place_holder_empty_image)
            .centerCrop()
    }

    private val emptyImagePlaceHolder by lazy {
        //val size = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.icon_common_size)
        DrawableUtil.getDrawableFromVectorRes(R.drawable.ic_place_holder_empty_image, 0)
        //BitmapUtil.getBitmapFromVectorRes(R.drawable.ic_place_holder_empty_image, size)
    }

    private val padding_iconSize_l by lazy { AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.icon_common_size_l) }

    @JvmStatic
    @BindingAdapter("glideImage")
    fun setGlideImage(imageView: ImageView, imageUrl: String?) {
        scope.launch {
            imageUrl?.let {
                imageView.setPadding(0, 0, 0, 0)
                Glide.with(imageView.context)
                    .load(it)
                    .apply(options)
                    .into(imageView)
            } ?: run {
                val placeHolderDeferred = async {
                    emptyImagePlaceHolder
                }
                val placeHolder = placeHolderDeferred.await()
                imageView.setPadding(padding_iconSize_l, padding_iconSize_l, padding_iconSize_l, padding_iconSize_l)
                imageView.setImageDrawable(placeHolder)
            }
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