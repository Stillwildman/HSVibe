package com.hsvibe.network

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.hsvibe.utilities.L


/**
 * Created by Vincent on 2021/8/17.
 */
class IntTypeAdapter : TypeAdapter<Int>() {

    override fun write(out: JsonWriter?, value: Int?) {
        out?.value(value)
    }

    override fun read(input: JsonReader?): Int {
        return input?.let {
            if (it.peek() == JsonToken.NULL) {
                it.nextNull()
                return 0
            }

            try {
                val result: String = it.nextString()
                if (result.isEmpty()) {
                    0
                }
                else result.toInt()
            }
            catch (e: NumberFormatException) {
                L.e("catch NumberFormatException!!!!")
                throw JsonSyntaxException(e)
            }
        } ?: 0
    }
}