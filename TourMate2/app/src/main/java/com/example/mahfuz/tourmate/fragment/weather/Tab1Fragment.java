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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.WeatherActivity;
import com.example.mahfuz.tourmate.api.Api;
import com.example.mahfuz.tourmate.apiPojo.CurrentWeather;
import com.example.mahfuz.tourmate.listener.OnDataFetchListener;
import com.example.mahfuz.tourmate.utility.WeatherUtility;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Tab1Fragment extends Fragment implements LocationListener, OnDataFetchListener {
    public static final String TAG = "Tab 1";

    protected LocationManager locationManager;
    private TextView tvTemp, tvDate, tvDay, tvCity;
    private TextView tvWeatherDes, tvTempMin, tvTempMax, tvSunrise, tvSunset, tvHumidity, tvPressure;
    private ImageView ivWeatherIcon;
    private double lati;
    private double longi;
    static String cityName;
    String currentCity;

    private CurrentWeather currentWeather;

    View view;

    private String units = "metric";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);
        WeatherActivity activity = (WeatherActivity) getActivity();
        //initializing view
        initializeView(view);
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
        Log.d("city name: ", cityName+"");
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
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        String url = String.format("weather?q=%s&units=%s&appid=%s",cityName, units, getString(R.string.weather_api_key));

        Call<CurrentWeather> call = api.getCurrentWeather(url);

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                currentWeather = response.body();

                try {
                    String icon = currentWeather.getWeather()
                            .get(0).getIcon();


                    // setting data into view
                    tvTemp.setText(currentWeather.getMain().getTemp()+"°C");
                    tvDate.setText(WeatherUtility.milliToDateConverter(currentWeather.getDt()).toString());
                    tvDay.setText(WeatherUtility.milliToDayConverter(currentWeather.getDt()));
                    //ivWeatherIcon.setImageResource(getResources().getIdentifier(icon, "drawable", "com.example.mahfuz.weatherapp"));
                    Picasso.get().load("https://openweathermap.org/img/w/" + icon + ".png")
                            .into(ivWeatherIcon);
                    tvWeatherDes.setText(currentWeather.getWeather().get(0).getDescription());
                    tvTempMin.setText(currentWeather.getMain().getTempMin()+"°C");
                    tvTempMax.setText(currentWeather.getMain().getTempMax()+"°C");
                    tvSunrise.setText(WeatherUtility.getTime(currentWeather.getSys().getSunrise()));
                    tvSunset.setText(WeatherUtility.getTime(currentWeather.getSys().getSunset()));
                    tvHumidity.setText(currentWeather.getMain().getHumidity()+"%");
                    tvPressure.setText(currentWeather.getMain().getPressure().toString()+" hpa");
                    tvCity.setText(currentWeather.getName());

                    Tab1Fragment.cityName = cityName;

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went Wrong! May be city name is not Correct!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }


    // for handling unit change
    public void getWeatherWithUnit(CharSequence units) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        String url = String.format("weather?q=%s&units=%s&appid=%s",cityName, units, getString(R.string.weather_api_key));

        Call<CurrentWeather> call = api.getCurrentWeather(url);

        final String checkUnits = units.toString();
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                currentWeather = response.body();

                String icon = currentWeather.getWeather()
                        .get(0).getIcon();

                // setting data into view

                tvDate.setText(WeatherUtility.milliToDateConverter(currentWeather.getDt()).toString());
                tvDay.setText(WeatherUtility.milliToDayConverter(currentWeather.getDt()));
                //ivWeatherIcon.setImageResource(getResources().getIdentifier(icon, "drawable", "com.example.mahfuz.weatherapp"));
                Picasso.get().load("https://openweathermap.org/img/w/" + icon + ".png")
                        .into(ivWeatherIcon);
                tvWeatherDes.setText(currentWeather.getWeather().get(0).getDescription());
                if (checkUnits.equals("metric")) {
                    tvTemp.setText(currentWeather.getMain().getTemp()+"°C");
                    tvTempMin.setText(currentWeather.getMain().getTempMin()+"°C");
                    tvTempMax.setText(currentWeather.getMain().getTempMax()+"°C");
                } else {
                    tvTemp.setText(currentWeather.getMain().getTemp()+"°F");
                    tvTempMin.setText(currentWeather.getMain().getTempMin()+"°F");
                    tvTempMax.setText(currentWeather.getMain().getTempMax()+"°F");
                }
                tvSunrise.setText(WeatherUtility.getTime(currentWeather.getSys().getSunrise()));
                tvSunset.setText(WeatherUtility.getTime(currentWeather.getSys().getSunset()));
                tvHumidity.setText(currentWeather.getMain().getHumidity()+"%");
                tvPressure.setText(currentWeather.getMain().getPressure().toString()+" hpa");
                tvCity.setText(currentWeather.getName());

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }


    // for handling clicking home button
    public void getWeatherByHome() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        String url = String.format("weather?q=%s&units=%s&appid=%s",currentCity, units, getString(R.string.weather_api_key));

        Call<CurrentWeather> call = api.getCurrentWeather(url);

        final String checkUnits = units.toString();
        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                currentWeather = response.body();

                String icon = currentWeather.getWeather()
                        .get(0).getIcon();

                // setting data into view

                tvDate.setText(WeatherUtility.milliToDateConverter(currentWeather.getDt()).toString());
                tvDay.setText(WeatherUtility.milliToDayConverter(currentWeather.getDt()));
                //ivWeatherIcon.setImageResource(getResources().getIdentifier(icon, "drawable", "com.example.mahfuz.weatherapp"));
                Picasso.get().load("https://openweathermap.org/img/w/" + icon + ".png")
                        .into(ivWeatherIcon);
                tvWeatherDes.setText(currentWeather.getWeather().get(0).getDescription());
                if (checkUnits.equals("metric")) {
                    tvTemp.setText(currentWeather.getMain().getTemp()+"°C");
                    tvTempMin.setText(currentWeather.getMain().getTempMin()+"°C");
                    tvTempMax.setText(currentWeather.getMain().getTempMax()+"°C");
                } else {
                    tvTemp.setText(currentWeather.getMain().getTemp()+"°F");
                    tvTempMin.setText(currentWeather.getMain().getTempMin()+"°F");
                    tvTempMax.setText(currentWeather.getMain().getTempMax()+"°F");
                }
                tvSunrise.setText(WeatherUtility.getTime(currentWeather.getSys().getSunrise()));
                tvSunset.setText(WeatherUtility.getTime(currentWeather.getSys().getSunset()));
                tvHumidity.setText(currentWeather.getMain().getHumidity()+"%");
                tvPressure.setText(currentWeather.getMain().getPressure().toString()+" hpa");
                tvCity.setText(currentWeather.getName());

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
    }

    @Override
    public void dataFetch(String cityName) {
        getWeather(cityName);
    }

    @Override
    public void unitFetch(String units) {
        getWeatherWithUnit(units);
    }


    public void initializeView(View view) {
        tvTemp = view.findViewById(R.id.tv_temp);
        tvDate = view.findViewById(R.id.tv_date);
        tvDay = view.findViewById(R.id.tv_day);
        tvCity = view.findViewById(R.id.tv_city);
        tvTempMin = view.findViewById(R.id.tv_temp_min);
        tvTempMax = view.findViewById(R.id.tv_temp_max);
        tvSunrise = view.findViewById(R.id.tv_sunrise);
        tvSunset = view.findViewById(R.id.tv_sunset);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvWeatherDes = view.findViewById(R.id.tv_weather_description);
        ivWeatherIcon = view.findViewById(R.id.iv_weatherIcon);
    }
}
