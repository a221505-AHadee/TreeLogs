package com.example.a221505_cikgu_izwan_plantlogs

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

// ── HELPER — Save QR Bitmap to Gallery ───────────────────
// Saves the QR code bitmap to the device's Pictures folder
// Returns true if successful
fun saveQrToGallery(context: Context, bitmap: Bitmap, plantCode: String): Boolean {
    return try {
        val filename = "PlantLogs_QR_$plantCode.png"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ — use MediaStore (no permission needed)
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PlantLogs")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            }
        } else {
            // Older Android — direct file write
            val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_PICTURES)
            val plantLogsDir = File(picturesDir, "PlantLogs")
            if (!plantLogsDir.exists()) plantLogsDir.mkdirs()
            val file = File(plantLogsDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
        Toast.makeText(context, "QR saved to Pictures/PlantLogs", Toast.LENGTH_SHORT).show()
        true
    } catch (e: Exception) {
        Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

// ── HELPER — Share QR Bitmap ──────────────────────────────
// Opens share sheet (WhatsApp, Print, etc.) with the QR image
fun shareQrBitmap(context: Context, bitmap: Bitmap, plantCode: String) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "qr_$plantCode.png")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", file)

        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            putExtra(android.content.Intent.EXTRA_TEXT, "PlantLogs QR Code: $plantCode")
            type = "image/png"
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share QR Code"))
    } catch (e: Exception) {
        Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// ── HELPER — Copy picked image to app's internal storage ──
// Gallery picker returns a temporary URI that may not persist.
// This copies the image to app storage so it persists.
fun copyImageToInternalStorage(context: Context, sourceUri: android.net.Uri, filename: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val imagesDir = File(context.filesDir, "plant_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val destFile = File(imagesDir, filename)
        inputStream?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
