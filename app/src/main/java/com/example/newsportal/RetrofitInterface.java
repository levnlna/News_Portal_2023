package com.example.newsportal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitInterface {
    @GET
    Call<News> getNews(@Url String url);

    @GET
    Call<News> getNewsByCategory(@Url String url);
}
