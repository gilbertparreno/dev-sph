package testservice.gp.com.api.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by gilbert on 12/25/18.
 */
object ApiUtils {

    @Volatile
    private var client: OkHttpClient? = null

    @Volatile
    private var gson: Gson? = null

    private val LOCK = Any()

    val gsonInstance: Gson?
        get() {
            if (gson == null) {
                synchronized(LOCK) {
                    if (gson == null) {
                        gson = GsonBuilder()
                                .registerTypeAdapter(DateTypeAdapter::class.java, DateTypeAdapter())
                                .create()
                    }
                }
            }
            return gson
        }

    fun initOkHttpClient(context: Context, debug: Boolean) {
        if (client == null) {
            synchronized(LOCK) {
                val builder = OkHttpClient.Builder()

                if (debug) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    builder.addInterceptor(loggingInterceptor)
                }
                client = builder.build()
            }
        } else {
            throw Throwable("OkHttpClient already initialized!")
        }
    }

    fun getClient(): OkHttpClient? {
        if (client == null) throw IllegalStateException("Call ApiUtils.init() first!")

        return client
    }
}