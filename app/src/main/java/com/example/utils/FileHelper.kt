package com.example.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createTempImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    
    val storageDir = File(context.cacheDir, "images")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    
    val file = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun createTempVideoUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val videoFileName = "MP4_" + timeStamp + "_"
    
    val storageDir = File(context.cacheDir, "images")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    
    val file = File.createTempFile(
        videoFileName,
        ".mp4",
        storageDir
    )
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
