package com.codesample.checker.utils

import android.view.View
import com.codesample.checker.R
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SnackbarUtil @Inject constructor() {

    fun showLoadError(view: View, t: Throwable) {
        if (view.isAttachedToWindow) {
            val errMsg = "${view.context.getString(R.string.load_error_message)}: ${t.message}"
            Snackbar.make(view, errMsg, Snackbar.LENGTH_LONG).show()
        }
    }
}