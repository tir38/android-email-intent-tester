package io.jasonatwood.android.emailintenttester

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import java.io.*

/*
 * Copy image from assets to [android.content.Context.getFilesDir],
 * so that we can later share via [android.content.Intent.EXTRA_STREAM]
 */

fun generateUriFromImage(context: Context): Uri {
    return getUriForFile(
        context,
        "io.jasonatwood.android.emailintenttester.fileprovider",
        File(context.filesDir, "cat.jpg")
    )
}

/**
 * Scraped from https://stackoverflow.com/a/19218941
 */
fun copyAssets(context: Context) {
    val assetManager: AssetManager = context.assets
    var files: Array<String>? = null
    try {
        files = assetManager.list("")
    } catch (e: IOException) {
        Log.e("tag", "Failed to get asset file list.", e)
    }
    for (filename in files!!) {
        var inputStream: InputStream?
        var outputStream: OutputStream?
        try {
            inputStream = assetManager.open(filename)
            val outDir = context.filesDir
            val outFile = File(outDir, filename)
            outputStream = FileOutputStream(outFile)
            copyFile(inputStream, outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            Log.e("tag", "Failed to copy asset file: $filename", e)
        }
    }
}

//@Throws(IOException::class)
private fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
    val buffer = ByteArray(1024)
    var read: Int
    while (inputStream.read(buffer).also { read = it } != -1) {
        outputStream.write(buffer, 0, read)
    }
}