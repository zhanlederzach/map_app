package com.main.ui_core

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.main.ui_core.extensions.dpToPixelInt

class Loader(context: Context?) {

    private val dialog: Dialog
    private val loading: ProgressBar
    private val mainView: View

    init {
        val alertDialog = AlertDialog.Builder(context)
        mainView = LayoutInflater.from(context).inflate(R.layout.custom_loader, null)
        loading = mainView.findViewById(R.id.loading)
        dialog = alertDialog.setView(mainView)
            .create()
        dialog?.window?.setDimAmount(0.0f)
    }

    fun showLoading() {
        dialog.apply {
            show()
            window?.setLayout(mainView.dpToPixelInt(100f), mainView.dpToPixelInt(100f))
        }
    }

    fun hideLoading(withAnimation: Boolean = false) {
        if (withAnimation) {
            Handler().postDelayed({
                dialog.dismiss()
            }, 2500)
        } else {
            dialog.dismiss()
        }
    }
}
