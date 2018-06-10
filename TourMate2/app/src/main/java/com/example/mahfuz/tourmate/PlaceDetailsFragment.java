package com.example.mahfuz.tourmate;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.mahfuz.tourmate.direction.DirectionResponse;
import com.example.mahfuz.tourmate.direction.DirectionService;
import com.example.mahfuz.tourmate.direction.Step;
import com.example.mahfuz.tourmate.Nearby.Location;
import com.example.mahfuz.tourmate.Nearby.Result;
import com.example.mahfuz.tourmate.fragment.map.NearByFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceDetailsFragment extends Fragment implements OnMapReadyCallback {

    private ImageView placeImageView;
    private TextView placeNameTV;
    private RatingBar placeRatingBar;
    private TextView placeAddressTV;

    private Location location;

    private GoogleMap map;
    private GoogleMapOptions options;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private double placeLatitude;
    private double placeLongitude;

    public PlaceDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        placeImageView = view.findViewById(R.id.placeImageView);
        placeNameTV = view.findViewById(R.id.placeNameTextView);
        placeRatingBar = view.findViewById(R.id.placeRatingBar);
        placeAddressTV = view.findViewById(R.id.placeAddressTextView);

        options = new GoogleMapOptions();
        options.compassEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction()
                .replace(R.id.placeDetailsMapContainer, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Result currentPlace = (Result) bundle.getSerializable("result");
            placeImageView.setImageResource(R.mipmap.ic_launcher);

            String key = getContext().getString(R.string.place_api_key);

            if (currentPlace.getPhotos() != null && currentPlace.getPhotos().size() > 0) {
                String url = String.format(
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=%s&key=%s",
                        currentPlace.getPhotos().get(0).getPhotoReference(),
                        key);
                Picasso.get().load(url).into(placeImageView);
            } else {
                placeImageView.setImageResource(R.drawable.ic_menu_camera);
            }

            placeNameTV.setText(currentPlace.getName());

            if(currentPlace.getRating() != null) {
                placeRatingBar.setRating(currentPlace.getRating().floatValue());
            }

            placeAddressTV.setText(currentPlace.getVicinity());

            placeLatitude = currentPlace.getGeometry().getLocation().getLat();
            placeLongitude = currentPlace.getGeometry().getLocation().getLng();

            LatLng startpoint = new LatLng(NearByFragment.currentLatitude, NearByFragment.currentLongitude);
            LatLng destination = new LatLng(placeLatitude, placeLongitude);
            getDirections(startpoint, destination);
            setMarker(startpoint, destination);
        }
    }

    private void setMarker(Location location) {
        if (map != null) {
            map.clear();
            LatLng latLng = new LatLng(location.getLat(), location.getLng());
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    private void setMarker(LatLng startpoint, LatLng destination) {
        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions().position(startpoint));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startpoint, 16));

            map.addMarker(new MarkerOptions().position(destination));
        }
    }

    private void getDirections(LatLng startPoint, LatLng endPoint) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DirectionService service = retrofit.create(DirectionService.class);
        String key = getString(R.string.direction_api_key);
        String url = String.format("directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                startPoint.latitude,
                startPoint.longitude,
                endPoint.latitude,
                endPoint.longitude,
                key);
        Call<DirectionResponse> call = service.getDirections(url);

        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if (response.code() == 200) {
                    DirectionResponse directionResponse = response.body();

                    List<Step> steps = directionResponse.getRoutes().get(0).getLegs().get(0)
                            .getSteps();

                    for (int i = 0; i < steps.size(); i++) {
                        double startLatitude = steps.get(i).getStartLocation().getLat();
                        double startLongitude = steps.get(i).getStartLocation().getLng();
                        LatLng startPoint = new LatLng(startLatitude, startLongitude);

                        double endLatitude = steps.get(i).getEndLocation().getLat();
                        double endLongitude = steps.get(i).getEndLocation().getLng();
                        LatLng endPoint = new LatLng(endLatitude, endLongitude);

                        Polyline polyline = map.addPolyline(new PolylineOptions()
                                .add(startPoint)
                                .add(endPoint)
                                .color(Color.BLUE));
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (checkLocationPermission()) {
            map.clear();
            LatLng latLng = new LatLng(placeLatitude, placeLongitude);
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
