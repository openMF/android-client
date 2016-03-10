package com.mifos.api;

import com.mifos.utils.PrefManager;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by rajan on 10/3/16.
 */
public class OauthOkHttpClient
{
	public OkHttpClient getOauthOkHttpClient()
	{
		OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(new Interceptor()
		{
			@Override
			public Response intercept(Chain chain) throws IOException
			{
				Request newRequest = chain.request().newBuilder()
						.addHeader(ApiRequestInterceptor.HEADER_TENANT, "default")
						.addHeader(ApiRequestInterceptor.HEADER_AUTH, PrefManager.getToken())
						.addHeader("Accept", "application/octet-stream")
						.build();
				return chain.proceed(newRequest);
			}
		}).build();
		return okClient;
	}
}
