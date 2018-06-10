package com.example.mahfuz.tourmate.fragment.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.adapter.ForecastListViewAdapter;
import com.example.mahfuz.tourmate.api.ForecastApi;
import com.example.mahfuz.tourmate.apiPojo.ForecastWeather;
import com.example.mahfuz.tourmate.listener.OnDataFetchListener;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Tab2Fargment extends Fragment implements LocationListener, OnDataFetchListener {

    public static final String TAG = "Tab 2";

    protected LocationManager locationManager;
    private ImageView ivWeatherIcon;
    private double lati;
    private double longi;
    static String cityName;
    String currentCity;

    private ForecastWeather forecastWeather;
    private ListView listView;

    View view;

    private Button button;

    private String units = "metric";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);

        listView = view.findViewById(R.id.forecast_list_view);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreateView: permission not init");
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        longi = location.getLongitude();
        lati = location.getLatitude();
        String cityName = getCity();
        currentCity = cityName;
        Log.d("city name in f2: ", cityName+"");
        getWeather(cityName);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void dataFetch(String data) {
//        Log.d("search city Name:", data);
//        Toast.makeText(getActivity(), "Search city"+data, Toast.LENGTH_SHORT).show();
        getWeather(data);
    }

    @Override
    public void unitFetch(String units) {
        getWeatherWithUnits(units);
    }

    public String getCity() {
        String curCity= "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lati, longi, 1);
            if (addressList.size() > 0) {
                curCity = addressList.get(0).getLocality();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curCity;
    }


    public void getWeather(final String cityName) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ForecastApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ForecastApi api = retrofit.create(ForecastApi.class);
        Log.i("city in retrofit", cityName);
        String url = String.format("forecast?q=%s&units=%s&cnt=%d&appid=%s",cityName, units, 7, getString(R.string.weather_api_key));

        Call<ForecastWeather> call = api.getForecastWeather(url);

        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                forecastWeather = response.body();

                try {
                    Log.i("list size: ", forecastWeather.getList().size()+"");

                    ForecastListViewAdapter forecastListViewAdapter = new ForecastListViewAdapter(getActivity(), forecastWeather.getList());
                    listView.setAdapter(forecastListViewAdapter);

                    Tab2Fargment.cityName = cityName;

                    Toast.makeText(getActivity(), "Search api city:"+forecastWeather.getCity().getName(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                Log.d("Failed !!", t.getMessage());

            }
        });
    }


    // for handling unit change
    public void getWeatherWithUnits(CharSequence units) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ForecastApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ForecastApi api = retrofit.create(ForecastApi.class);
        Log.i("city in retrofit", cityName);
        String url = String.format("forecast?q=%s&units=%s&cnt=%d&appid=%s",cityName, units, 7, getString(R.string.weather_api_key));

        Call<ForecastWeather> call = api.getForecastWeather(url);


        final String passUnit = units.toString();
        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                forecastWeather = response.body();

                //setting units property to List for manging F or C
                forecastWeather.getList().get(0).setUnitType(passUnit);
                Log.i("unit is: ", forecastWeather.getList().get(0).getUnitType()+" dsf");
Log.i("list size: ", forecastWeather.getList().size()+"");

                // passing weather list to adapter
                ForecastListViewAdapter forecastListViewAdapter = new ForecastListViewAdapter(getActivity(),
                        forecastWeather.getList());
                listView.setAdapter(forecastListViewAdapter);

                Toast.makeText(getActivity(), "Search api city:"+forecastWeather.getCity().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                Log.d("Failed !!", t.getMessage());

            }
        });
    }


    // for handling home button
    public void getWeatherByHome() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ForecastApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ForecastApi api = retrofit.create(ForecastApi.class);
        Log.i("city in retrofit", cityName);
        String url = String.format("forecast?q=%s&units=%s&cnt=%d&appid=%s",currentCity, units, 7, getString(R.string.weather_api_key));

        Call<ForecastWeather> call = api.getForecastWeather(url);


        final String passUnit = units.toString();
        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                forecastWeather = response.body();

                //setting units property to List for manging F or C
                forecastWeather.getList().get(0).setUnitType(passUnit);
                Log.i("unit is: ", forecastWeather.getList().get(0).getUnitType()+" dsf");
                Log.i("list size: ", forecastWeather.getList().size()+"");

                // passing weather list to adapter
                ForecastListViewAdapter forecastListViewAdapter = new ForecastListViewAdapter(getActivity(),
                        forecastWeather.getList());
                listView.setAdapter(forecastListViewAdapter);

                Toast.makeText(getActivity(), "Search api city:"+forecastWeather.getCity().getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                Log.d("Failed !!", t.getMessage());

            }
        });
    }

}
