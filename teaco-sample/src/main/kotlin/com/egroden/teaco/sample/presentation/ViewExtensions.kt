package com.egroden.teaco.sample.presentation

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.egroden.teaco.sample.BuildConfig

fun Context.toast(text: String) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun View.visibility(visibility: Visibility) = setVisibility(visibility.value)

enum class Visibility(val value: Int) {
    VISIBLE(View.VISIBLE),
    INVISIBLE(View.INVISIBLE),
    GONE(View.GONE)
}

class Effect<T>(initialValue: T) {
    var value = initialValue
        set(value) {
            if (field != value) {
                field = value
                change?.invoke(value)
            }
        }

    var change: ((T) -> Unit)? = null
}

infix fun <T> Effect<T>.bind(body: (T) -> Unit) {
    change = body
}

fun <T> Effect<T>.unbind() {
    change = null
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadMoviePreview(url: String) {
    Glide.with(this)
        .load("${BuildConfig.POSTER_URL}w185/$url")
        .into(this)
}

