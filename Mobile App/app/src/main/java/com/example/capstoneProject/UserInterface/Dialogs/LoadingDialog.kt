package com.example.capstoneProject.UserInterface.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.example.capstoneProject.R

class LoadingDialog(activity: Activity, textMessage: String) {
    private val activity: Activity? = activity
    private var dialog: AlertDialog? = null
    private val message: String = textMessage

    fun startLoadingAnimation() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_custom, null)
        builder.setView(view)
        val textView = view.findViewById<TextView>(R.id.textView_customDialog)
        builder.setCancelable(false)
        textView.text = message
        dialog = builder.create()
        dialog!!.show()

    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }

}