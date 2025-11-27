package com.example.luxres;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // For debugging network calls

public class ApiClient {

    // Replace with your actual base URL
    private static final String BASE_URL = "https://your-luxevista-api.com/api/"; // EXAMPLE!

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // For debugging: Log network requests and responses
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Use NONE for production
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    // Add interceptors for adding auth tokens if needed
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Use the custom client
                    .addConverterFactory(GsonConverterFactory.create()) // Using Gson for JSON parsing
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}