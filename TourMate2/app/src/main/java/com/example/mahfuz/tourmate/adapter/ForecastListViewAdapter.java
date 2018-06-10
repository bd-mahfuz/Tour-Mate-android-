package com.example.mahfuz.tourmate.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.apiPojo.ForecastWeather;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ForecastListViewAdapter extends ArrayAdapter<ForecastWeather.List> {
    private Context context;
    private List<ForecastWeather.List> forecastWeatherList;
    public ForecastListViewAdapter(@NonNull Context context, @NonNull List<ForecastWeather.List> forecastWeatherList) {
        super(context, R.layout.layout_forecast_list, forecastWeatherList);
        this.context = context;
        this.forecastWeatherList = forecastWeatherList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.layout_forecast_list, parent, false);

        ForecastWeather.List currentWeatherList = forecastWeatherList.get(position);

        TextView tvDay = convertView.findViewById(R.id.tv_day);
        TextView  tvMinTemp = convertView.findViewById(R.id.tv_temp_min);
        ImageView ivWeatherIcon = convertView.findViewById(R.id.iv_weather_icon);
        TextView tvDate = convertView.findViewById(R.id.tv_date);
        TextView tvMaxTemp = convertView.findViewById(R.id.tv_temp_max);

        String units = forecastWeatherList.get(0).getUnitType()+"";

        String icon = currentWeatherList.getWeather().get(0).getIcon();

        if (position == 0) {
            tvDay.setText("Today");
        } else {
            tvDay.setText(unixToDay(currentWeatherList.getDt()));
        }

        Picasso.get().load("https://openweathermap.org/img/w/" + icon + ".png")
                .into(ivWeatherIcon);
        //ivWeatherIcon.setImageResource(convertView.getResources().getIdentifier(icon, "drawable", "com.example.mahfuz.weatherapp"));

        tvDate.setText(unixToDay(currentWeatherList.getDt())+","+unixToDate(currentWeatherList.getDt()));
        // checking unit type is metric or imperial for handling C or F sign
        if (units.equals("metric")) {
            //for metric or celsius
            tvMaxTemp.setText("Max: "+currentWeatherList.getMain().getTempMax()+"°C");
            tvMinTemp.setText("Min: "+currentWeatherList.getMain().getTempMin()+"°C");

        } else if (units.equals("imperial")){
            //for imperial or fahrenheit
            tvMaxTemp.setText("Max: "+currentWeatherList.getMain().getTempMax()+"°F");
            tvMinTemp.setText("Min: "+currentWeatherList.getMain().getTempMin()+"°F");

        } else {
            // for default from url or celsius
            tvMaxTemp.setText("Max: "+currentWeatherList.getMain().getTempMax()+"°C");
            tvMinTemp.setText("Min: "+currentWeatherList.getMain().getTempMin()+"°C");
        }

        return convertView;
    }


    private String unixToDay(long timeStamp) {
        Date dateTime = new Date((long)timeStamp*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int dayInt = cal.get(Calendar.DAY_OF_WEEK);

        String dayStr = "Saturday";

        switch (dayInt) {
            case Calendar.SATURDAY:
                dayStr = "Saturday";
                break;
            case Calendar.SUNDAY:
                dayStr = "Sunday";
                break;
            case Calendar.MONDAY:
                dayStr = "Monday";
                break;
            case Calendar.TUESDAY:
                dayStr = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayStr = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayStr = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayStr = "Friday";
                break;
        }

        return dayStr;
    }

    private String unixToDate(long timestamp) {
        // convert seconds to milliseconds
        Date date = new Date(timestamp*1000L);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat("MM yyyy");
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    private String getTimeFromUnix(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);

        Date d = calendar.getTime();
        String timeStr = new SimpleDateFormat("hh:mm a").format(d);

        return timeStr;
    }
}
