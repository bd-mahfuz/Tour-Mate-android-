package com.example.mahfuz.tourmate.api;

import com.example.mahfuz.tourmate.apiPojo.ForecastWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ForecastApi {

    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GET
    Call<ForecastWeather> getForecastWeather(@Url String url);
}
