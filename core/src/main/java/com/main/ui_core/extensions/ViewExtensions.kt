package com.main.ui_core.extensions

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

fun TextView.setTextAsync(text: CharSequence?) {
    (this as AppCompatTextView).apply {
        val params = TextViewCompat.getTextMetricsParams(this)
        val precomputedText = PrecomputedTextCompat.create(text ?: "", params)
        setTextFuture(PrecomputedTextCompat.getTextFuture(precomputedText, params, null))
    }
}

fun View.dpToPixelInt(dp: Float): Int =
    (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

fun View.dpToPixel(dp: Float): Float =
    dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun View.dpToPixel(dp: Int): Int =
    dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT).toInt()

fun View.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()).toInt()
}

fun View.spToPx(sp: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.getResources().getDisplayMetrics())
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setHtmlText(htmlInstruction: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlInstruction, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
        Html.fromHtml(htmlInstruction).toString()
    }
}