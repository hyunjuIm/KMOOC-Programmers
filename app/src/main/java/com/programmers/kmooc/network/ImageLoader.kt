package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.Exception

object ImageLoader {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    val cacheSize = maxMemory / 8
    private val bitmapCache = object : LruCache<String?, Bitmap?>(cacheSize) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value?.byteCount!! / 1024
        }
    }

    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {

        if (url.isEmpty()) {
            completed(null)
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                var bitmap: Bitmap? = bitmapCache.get(url)

                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                    bitmapCache.put(url, bitmap)
                }

                withContext(Dispatchers.Main) {
                    completed(bitmap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    completed(null)
                }
            }
        }
    }

}