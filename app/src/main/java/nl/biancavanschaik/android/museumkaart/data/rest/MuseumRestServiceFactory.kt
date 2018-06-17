package nl.biancavanschaik.android.museumkaart.data.rest

import nl.biancavanschaik.android.museumkaart.util.rest.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class MuseumRestServiceFactory(private val retrofit: Retrofit) {

    fun <T> create(type: Class<T>): T {
        return retrofit.create<T>(type)
    }

    companion object {
        fun getInstance() =
                MuseumRestServiceFactory(
                        Retrofit.Builder()
                                .baseUrl("https://m.museumkaart.nl")
                                .addConverterFactory(MoshiConverterFactory.create())
                                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                                .client(createClient())
                                .build()
                )

        private fun createClient(): OkHttpClient {

            // Create a trust manager that does not validate certificate chains
            val trustManager = acceptAllTrustManager()

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, arrayOf(trustManager), java.security.SecureRandom())
            }

            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            return OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.socketFactory, trustManager)
                    .hostnameVerifier { _, _ -> true }
                    .addInterceptor(logging)
                    .build()

        }

        private fun acceptAllTrustManager() = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
        }
    }
}