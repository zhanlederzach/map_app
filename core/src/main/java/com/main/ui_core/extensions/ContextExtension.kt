package com.main.ui_core.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import java.io.File

fun Context.documentOpenIntent(filePath: String) {
    val map = MimeTypeMap.getSingleton()
    val file = File(filePath)
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.name)
    var type = map.getMimeTypeFromExtension(extension)
    if (type.isNullOrEmpty()) {
        type = "*/*"
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        var intent = Intent(Intent.ACTION_VIEW)
        intent.setData(uri)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent = Intent.createChooser(intent, "")
        startActivity(intent)
    } else {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), type)
        intent = Intent.createChooser(intent, "")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

fun Context.setHidden(isHidden: Boolean, editText: EditText) {
    if (!isHidden) {
        editText.apply {
            setTransformationMethod(null)
            setSelection(editText.getText().length)
        }
    } else {
        editText.apply {
            setTransformationMethod(PasswordTransformationMethod())
            setSelection(editText.getText().length)
        }
    }
}

fun Context.setLayoutParams(view: View) {
    view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun Context.circularProgress(): CircularProgressDrawable {
    return CircularProgressDrawable(this).apply {
        strokeWidth = 3f
        centerRadius = 15f
        start()
    }
}
