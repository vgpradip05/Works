package com.example.offlineimagesearch.network;


import com.example.offlineimagesearch.models.Rsp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import retrofit2.http.GET;
import retrofit2.http.Headers;

import retrofit2.http.Query;


public class APIClient {

    public static String BASE_URL;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {


        if (retrofit == null) {


            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    /*.addInterceptor(HeaderInterceptor())*/
                    .build();


            BASE_URL = "https://www.flickr.com/";


            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

   /* private static Interceptor HeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .addHeader("appCode", Constants.AppConfig.APP_CODE)
                        .addHeader("appVersion", Utils.getAppVersion(mContext))
                        .addHeader("clientType", Constants.AppConfig.APP_TYPE_VALUE)
                        .addHeader("sessionId", appPreferences.getSessionID())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
    }*/

    public static APIInterface getAPIManager() {
        APIInterface apiInterface = getClient().create(APIInterface.class);
        return apiInterface;
    }

    public interface APIInterface {
        @Headers("Content-Type: application/json")
        @GET("services/rest/")
        Call<Rsp> getPhotos(@Query("api_key") String api_key, @Query("method") String method,
                            @Query("text") String text, @Query("page") String page,
                            @Query("format") String format,
                            @Query("nojsoncallback") String nojsoncallback
        );
    }


}
