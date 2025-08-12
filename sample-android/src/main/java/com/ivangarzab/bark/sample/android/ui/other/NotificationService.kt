package com.ivangarzab.bark.sample.android.ui.other

import com.ivangarzab.bark.Bark

class NotificationService {
    fun showSuccess(message: String) {
        Bark.i("Success notification: $message")
        // In real app, would show toast/snackbar
    }

    fun showError(message: String) {
        Bark.e("Error notification: $message")
        // In real app, would show error dialog
    }

    fun showWarning(message: String) {
        Bark.w("Warning notification: $message")
        // In real app, would show warning toast
    }

    fun showInfo(message: String) {
        Bark.d("Info notification: $message")
        // In real app, would show info toast
    }
}