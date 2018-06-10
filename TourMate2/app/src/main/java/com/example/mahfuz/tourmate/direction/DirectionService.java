package com.example.mahfuz.tourmate.direction;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Mobile App Develop on 5/23/2018.
 */

public interface DirectionService {
    @GET
    Call<DirectionResponse>getDirections(@Url String url);
}
