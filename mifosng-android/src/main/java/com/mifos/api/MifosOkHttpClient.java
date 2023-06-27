package com.mifos.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by Rajan Maurya on 16/06/16.
 */
public class MifosOkHttpClient {
    public OkHttpClient getMifosOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Enable Full Body Logging
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(Level.BODY);

        // Setting Timeout 30 Seconds
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);

        // Interceptor :> Full Body Logger and ApiRequest Header
        builder.addInterceptor(logger);
        builder.addInterceptor(new MifosInterceptor());
        builder.addNetworkInterceptor(new StethoInterceptor());

        // Adding Certificate Pinning
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("<YOUR_DOMAIN>", "sha256/<YOUR_HASH>")
                .build();
        builder.certificatePinner(certificatePinner);

        // Using the default SSL Socket Factory and Hostname Verifier
        // as they use the trusted certificates by default
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
