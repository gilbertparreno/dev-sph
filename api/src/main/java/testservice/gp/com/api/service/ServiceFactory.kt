package testservice.gp.com.api.service

import android.content.Context
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import testservice.gp.com.api.utils.ApiUtils

object ServiceFactory {

    private var retrofit: Retrofit? = null

    fun init(context: Context, url: String, debug: Boolean) {
        ApiUtils.initOkHttpClient(context, debug)
        val gson = ApiUtils.gsonInstance
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(ApiUtils.getClient()!!)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson!!))
                .build()
    }

    fun <T : Any> getService(clazz: Class<T>): T {
        if (retrofit == null) throw IllegalStateException("Call ServiceFactory.init() first!")
        return retrofit!!.create(clazz)
    }
}