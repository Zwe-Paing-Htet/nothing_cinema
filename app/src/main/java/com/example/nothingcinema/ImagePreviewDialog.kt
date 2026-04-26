package com.example.nothingcinema

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

object ImagePreviewDialog {

    fun show(context: Context, imageBytes: ByteArray) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image_preview, null)
        val previewImageView = dialogView.findViewById<ImageView>(R.id.previewImageView)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        previewImageView.setImageBitmap(bitmap)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
    }
}