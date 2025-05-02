/*
 * Copyright (c) 2025 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest

object IconUtils {
    suspend fun convertToDrawable(icon: Icon?, context: Context): Drawable? {
        return icon?.loadDrawable(context)
    }

    suspend fun convertToBitmap(icon: Icon?, context: Context): Bitmap? {
        val drawable = convertToDrawable(icon, context)
        return drawable?.let {
            if (drawable is BitmapDrawable) return@let drawable.bitmap

            if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                return@let createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight
                ).applyCanvas {
                    drawable.setBounds(0, 0, this.width, this.height)
                    drawable.draw(this)
                }
            }
            return@let null
        }
    }

    suspend fun checkSum(bitmap: Bitmap): String = ByteArrayOutputStream(bitmap.byteCount).use {
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, it)
        val hashBytes = MessageDigest.getInstance("SHA-256").digest(it.toByteArray())
        hashBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun smartSaveToStorage(icon: Icon?, context: Context): String? {
        val bitmap = convertToBitmap(icon = icon, context = context)
        return bitmap?.let { bm ->
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSLESS
            } else {
                Bitmap.CompressFormat.WEBP
            }
            val filename = "${checkSum(bitmap)}.webp"

            if (File(context.filesDir, filename).exists()) return@let filename

            val isStored = context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                bm.compress(format, 100, it)
            }
            return@let if (isStored) filename else null
        }
    }

    fun getPackageIcon(packageName: String?, context: Context): Drawable? {
        return packageName?.let {
            return try {
                context.packageManager.getApplicationIcon(it)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun loadIconFormPath(path: String?, context: Context): Icon? {
        return path?.let {
            val file = File(context.filesDir, it)
            return@let if (file.exists()) Icon.createWithFilePath(file.path) else null
        }
    }
}