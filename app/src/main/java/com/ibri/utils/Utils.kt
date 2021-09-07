package com.ibri.utils

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Utils {
    companion object {


        fun showErrorAler(context: Context, msg: String) {
            AlertDialog.Builder(context)
                .setTitle("Errore Generico")
                .setMessage(msg)
                .setPositiveButton("OK") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
        }

    }
}