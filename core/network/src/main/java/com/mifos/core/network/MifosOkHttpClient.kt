package com.mifos.core.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mifos.core.datastore.PrefManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Rajan Maurya on 16/06/16.
 */
class MifosOkHttpClient(private val prefManager: PrefManager) {
    // Create a trust manager that does not validate certificate chains
    val mifosOkHttpClient: OkHttpClient

    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager

    //Enable Full Body Logging

    //Set SSL certificate to OkHttpClient Builder

    //Enable Full Body Logging

    //Setting Timeout 30 Seconds

        //Interceptor :> Full Body Logger and ApiRequest Header
        get() {
            val builder = OkHttpClient.Builder()
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return emptyArray()
                        }
                    }
                )

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                //Enable Full Body Logging
                val logger = HttpLoggingInterceptor()
                logger.level = HttpLoggingInterceptor.Level.BODY

                //Set SSL certificate to OkHttpClient Builder
//                builder.sslSocketFactory(sslSocketFactory)
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { hostname, session -> true }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            //Enable Full Body Logging
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            //Setting Timeout 30 Seconds
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS)

            //Interceptor :> Full Body Logger and ApiRequest Header
            builder.addInterceptor(logger)
            builder.addInterceptor(MifosInterceptor(prefManager))
            builder.addNetworkInterceptor(StethoInterceptor())
            return builder.build()
        }
}