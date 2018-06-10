package com.example.mahfuz.tourmate.Nearby;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NearbyPlaceService {
    @GET
    Call<NearbyPlacesResponse> getNearbyPlaces(@Url String url);
}
