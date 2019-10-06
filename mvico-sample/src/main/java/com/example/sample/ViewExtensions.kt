package com.example.sample

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

inline fun Activity.frameLayout(block: FrameLayout.() -> Unit) = FrameLayout(this).apply(block)

inline fun Fragment.frameLayout(block: FrameLayout.() -> Unit) =
    FrameLayout(context as FragmentActivity).apply(block)

inline fun Activity.linearLayout(block: LinearLayout.() -> Unit) = LinearLayout(this).apply(block)

inline fun Fragment.linearLayout(block: LinearLayout.() -> Unit) =
    LinearLayout(context as FragmentActivity).apply(block)

inline fun ViewGroup.recyclerView(block: RecyclerView.() -> Unit) {
    addView(
        RecyclerView(context).apply(block)
    )
}

inline fun ViewGroup.progressBar(block: ProgressBar.() -> Unit) = ProgressBar(context).apply(block)

inline fun ViewGroup.textView(block: TextView.() -> Unit) = TextView(context).apply(block)

inline fun ViewGroup.editText(block: EditText.() -> Unit) = EditText(context).apply(block)

inline fun ViewGroup.button(block: Button.() -> Unit) = Button(context).apply(block)

inline fun ViewGroup.imageView(block: ImageView.() -> Unit) = ImageView(context).apply(block)

fun TextView.text(value: CharSequence) = setText(value)

fun Button.onClick(body: (View) -> Unit) = setOnClickListener(body)

fun View.size(width: Size, height: Size) {
    if (layoutParams == null) {
        layoutParams = ViewGroup.LayoutParams(
            width.value,
            height.value
        )
    } else {
        layoutParams.width = width.value
        layoutParams.height = height.value
    }
}

fun View.visibility(visibility: Visibility) = setVisibility(visibility.value)

fun View.gravity(gravity: Gravity){
    if (parent is LinearLayout){
        (layoutParams as LinearLayout.LayoutParams).gravity = gravity.value
    }
}

fun View.id(value: Int) {
    id = value
}

fun <T: RecyclerView.ViewHolder> RecyclerView.adapter(adapter: RecyclerView.Adapter<T>) = setAdapter(adapter)
fun RecyclerView.layoutManager(manager: RecyclerView.LayoutManager){
    layoutManager = manager
}

enum class Size(val value: Int) {
    MATCH_PARENT(ViewGroup.LayoutParams.MATCH_PARENT),
    WRAP_CONTENT(ViewGroup.LayoutParams.WRAP_CONTENT)
}

enum class Visibility(val value: Int) {
    VISIBLE(View.VISIBLE),
    INVISIBLE(View.INVISIBLE),
    GONE(View.GONE)
}

enum class Gravity(val value: Int){
    START(android.view.Gravity.START),
    CENTER(android.view.Gravity.CENTER),
    CENTER_HORIZONTAL(android.view.Gravity.CENTER_HORIZONTAL),
    CENTER_VERTICAL(android.view.Gravity.CENTER_VERTICAL),
    END(android.view.Gravity.END)
}

class Effect<T>(initialValue: T){
    var value = initialValue
    set(value) {
        if (field != value){
            field = value
            change(value)
        }
    }

    var change: (T) -> Unit = {}
}

infix fun <T> Effect<T>.bind(body: (T) -> Unit){
    change = body
}

fun ImageView.load(url: String){
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadMoviePreview(url: String){
    Glide.with(this)
        .load("${BuildConfig.POSTER_URL}w185/$url")
        .transform(RoundedCorners(20))
        .into(this)
}

