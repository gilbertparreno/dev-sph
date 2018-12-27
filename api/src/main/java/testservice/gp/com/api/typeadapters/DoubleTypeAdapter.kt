package testservice.gp.com.api.typeadapters

import android.text.TextUtils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * Created by gilbert on 12/25/18.
 */
class DoubleTypeAdapter : TypeAdapter<Double>() {
    override fun write(out: JsonWriter?, value: Double?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out?.value(value.toString())
        }
    }

    override fun read(input: JsonReader?): Double? {
        val json = input?.nextString()
        if (TextUtils.isEmpty(json)) {
            return null
        } else {
            return json?.toDouble()
        }
    }
}