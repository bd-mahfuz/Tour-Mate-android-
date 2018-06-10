package com.example.mahfuz.tourmate.fragment.map;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.mahfuz.tourmate.direction.DirectionResponse;
import com.example.mahfuz.tourmate.direction.DirectionService;
import com.example.mahfuz.tourmate.direction.Step;
import com.example.mahfuz.tourmate.Nearby.Location;
import com.example.mahfuz.tourmate.Nearby.NearbyPlaceAdapter;
import com.example.mahfuz.tourmate.Nearby.NearbyPlaceService;
import com.example.mahfuz.tourmate.Nearby.NearbyPlacesResponse;
import com.example.mahfuz.tourmate.Nearby.Result;
import com.example.mahfuz.tourmate.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearByFragment extends Fragment implements OnMapReadyCallback {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private GoogleMap map;
    private GoogleMapOptions options;
    private SupportMapFragment mapFragment;
    private Context context;

    private Button findButton;
    private Spinner locationCategorySp;
    private Spinner locationDistanceSp;
    private FrameLayout mapContainer;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private String searchItem = "locality";
    private int kilometre = 1500;
    public static double currentLatitude;
    public static double currentLongitude;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private String[] locationCategories = {
            "Restaurant",
            "Bank",
            "ATM",
            "Hospital",
            "Shopping Mall",
            "Mosque",
            "Bus Station",
            "Police Station"
    };

    private String[] locationDistances = {
            "0.5km",
            "1km",
            "1.5km",
            "2km",
            "2.5km",
            "3km",
            "4km",
            "5km",
            "6km",
            "8km",
            "10km"
    };


    public NearByFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (checkLocationPermission()) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            if (location != null) {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();

                                setMarker();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "getting Location failed", Toast.LENGTH_SHORT).show();
                            Log.e("Location Failed", e.getMessage());
                        }
                    });
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new GoogleMapOptions();
        options.compassEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

        FragmentTransaction ft = getFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment);
        ft.commit();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationCategorySp = view.findViewById(R.id.locationCategorySpinner);
        locationDistanceSp = view.findViewById(R.id.locationDistanceSpinner);
        findButton = view.findViewById(R.id.findLocationButton);
        mapContainer = view.findViewById(R.id.mapContainer);

        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        locationCategories);
        ArrayAdapter<String> distanceArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                locationDistances);

        locationCategorySp.setAdapter(categoryArrayAdapter);
        locationDistanceSp.setAdapter(distanceArrayAdapter);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNearbyPlaces();
            }
        });

        recyclerView = view.findViewById(R.id.placeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void selectKilometre(int position) {
        switch (position) {
            case 0:
                kilometre = 1500;
                break;
            case 1:
                kilometre = 1000;
                break;
            case 2:
                kilometre = 1500;
                break;
            case 3:
                kilometre = 2000;
                break;
            case 4:
                kilometre = 2500;
                break;
            case 5:
                kilometre = 3000;
                break;
            case 6:
                kilometre = 4000;
                break;
            case 7:
                kilometre = 5000;
                break;
            case 8:
                kilometre = 6000;
                break;
            case 9:
                kilometre = 8000;
                break;
            case 10:
                kilometre = 10000;
                break;
        }
    }

    private void selectLocation(int position) {
        switch (position) {
            case 0:
                searchItem = "restaurant";
                break;
            case 1:
                searchItem = "bank";
                break;
            case 2:
                searchItem = "atm";
                break;
            case 3:
                searchItem = "hospital";
                break;
            case 4:
                searchItem = "shopping_mall";
                break;
            case 5:
                searchItem = "mosque";
                break;
            case 6:
                searchItem = "bus_station";
                break;
            case 7:
                searchItem = "police";
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMarker();
    }

    private void setMarker() {
        if (checkLocationPermission()) {
            map.clear();
            map.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

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

    private void getNearbyPlaces() {
        selectKilometre(locationDistanceSp.getSelectedItemPosition());
        selectLocation(locationCategorySp.getSelectedItemPosition());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NearbyPlaceService service = retrofit.create(NearbyPlaceService.class);
        String apiKey = getString(R.string.place_api_key);
        String url = String.format(
                "json?location=%f,%f&radius=%d&type=%s&key=%s",
                currentLatitude,
                currentLongitude,
                kilometre,
                searchItem,
                apiKey);

        System.out.println(url);

        Call<NearbyPlacesResponse> call = service.getNearbyPlaces(url);

        call.enqueue(new Callback<NearbyPlacesResponse>() {
            @Override
            public void onResponse(Call<NearbyPlacesResponse> call, Response<NearbyPlacesResponse> response) {
                if (response.code() == 200) {
                    NearbyPlacesResponse nearbyPlacesResponse = response.body();
                    final List<Result> resultList = nearbyPlacesResponse.getResults();

                    adapter = new NearbyPlaceAdapter(resultList, getContext());
                    recyclerView.setAdapter(adapter);

                    mapContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<NearbyPlacesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load nearby places. Turn on your internet and GPS", Toast.LENGTH_SHORT).show();
                Log.e("Nearby Place Error: ", t.getMessage());
            }
        });
    }

    private void setMarker(List<Result> resultList) {
        map.clear();

        for (int i = 0; i < resultList.size(); i++) {
            Location location = resultList.get(i).getGeometry().getLocation();
            LatLng latLng = new LatLng(location.getLat(), location.getLng());
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Near By");
    }
}