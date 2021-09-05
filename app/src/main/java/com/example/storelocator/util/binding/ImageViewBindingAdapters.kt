package com.example.storelocator.util.binding

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter(value = ["srcUrlAsync", "centerCrop", "placeholder"], requireAll = false)
fun setSrcUrlAsync(
    view: ImageView,
    imageUrl: String?,
    centerCrop: Boolean,
    @DrawableRes placeholder: Int
) {
    if (imageUrl != null) {
        val requestCreator = Picasso.get().load(imageUrl)
        if (centerCrop) {
            requestCreator.fit().centerCrop()
        }
        if(placeholder != 0) {
            requestCreator.placeholder(placeholder).error(placeholder)
        }
        requestCreator.into(view)
    } else {
        view.setImageResource(placeholder)
    }
}