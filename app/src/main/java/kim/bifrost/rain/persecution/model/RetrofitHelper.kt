package kim.bifrost.rain.persecution.model

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * kim.bifrost.rain.persecution.model.RetrofitHelper
 * Persecution
 *
 * @author 寒雨
 * @since 2022/3/9 9:16
 **/
object RetrofitHelper {

    private const val BASE_URL = "http://42.192.196.215:8080"
    private val retrofit by lazy { initRetrofit() }
    val service: ApiService by lazy { retrofit.create(ApiService::class.java) }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .build()
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }
}