package com.example.algoliademo1.util

import android.content.Context
import java.io.IOException

class JsonUtil {

    companion object {
        fun loadJSONFromAsset(context: Context): String {
            val json: String?
            try {
                val inputStream = context.applicationContext.assets.open("products4.json")

                val size = inputStream.available()
                val buffer = ByteArray(size)
                val charset = Charsets.UTF_8
                inputStream.read(buffer)
                inputStream.close()

                json = String(buffer, charset)
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }

            return json
        }
    }
}