package com.dicoding.myappstories.utilities

import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun ImageView.setImageFromUrl(context: Context, url: String) {
    Glide
        .with(context)
        .load(url)
        .into(this)
}

fun TextView.setLocalDateFormat(timestamp: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = sdf.parse(timestamp) as Date

    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formattedDate
}

fun View.animateVisibility(isVisible: Boolean, duration: Long = 400) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}