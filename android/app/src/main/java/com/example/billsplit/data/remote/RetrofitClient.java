package com.example.billsplit.data.remote;

import com.example.billsplit.local.TokenManager;
import com.example.billsplit.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static ApiService apiService;

    private RetrofitClient() {
    }

    /**
     * Attaches "Authorization: Bearer <token>" to EVERY outgoing request
     * automatically, whenever a token exists — so individual Repository
     * methods never need to remember to add it themselves. On /auth/login
     * and /auth/register specifically, TokenManager.getToken() is null
     * anyway (nothing to log in with yet), so this is a harmless no-op
     * for those two calls.
     */
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String token = TokenManager.getInstance().getToken();

            if (token == null) {
                return chain.proceed(original);
            }

            Request authorized = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authorized);
        }
    }

    private static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // full request/response bodies in Logcat — turn down to NONE before a release build

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS) // generous on purpose — Render free tier cold starts can take 30-60s on the FIRST request after idle
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
}