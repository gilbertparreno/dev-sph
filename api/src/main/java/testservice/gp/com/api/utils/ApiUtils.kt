package testservice.gp.com.api.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import okhttp3.Cache
import okhttp3.Interceptor
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

                val networkCacheInterceptor = Interceptor { chain ->
                    val originalResponse = chain.proceed(chain.request())
                    originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control",
                                    String.format("max-age=%d", 60))
                            .build()
                }

                val offlineInterceptor = Interceptor { chain ->
                    var request = chain.request()
                    request = if (hasNetwork(context)!!) {
                        val maxAge = 60
                        request.newBuilder().addHeader("Cache-Control", "public, max-age=$maxAge").build()
                    } else {
                        val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                        request.newBuilder().addHeader("Cache-Control",
                                "public, only-if-cached, max-stale=$maxStale").build()
                    }

                    chain.proceed(request)
                }

                builder.networkInterceptors().add(networkCacheInterceptor)
                builder.addInterceptor(offlineInterceptor)

                val cacheSize = (5 * 1024 * 1024).toLong()
                val cache = Cache(context.cacheDir, cacheSize)
                builder.cache(cache)

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