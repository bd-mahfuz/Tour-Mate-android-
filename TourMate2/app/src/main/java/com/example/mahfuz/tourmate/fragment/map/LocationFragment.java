package com.example.mahfuz.tourmate.fragment.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.direction.DirectionResponse;
import com.example.mahfuz.tourmate.direction.DirectionService;
import com.example.mahfuz.tourmate.direction.Step;
import com.example.mahfuz.tourmate.model.MyItem;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.mahfuz.tourmate.fragment.weather.Tab1Fragment.TAG;


public class LocationFragment extends Fragment implements LocationListener, OnMapReadyCallback, View.OnClickListener{

    private GoogleMap map;
    private GoogleMapOptions options;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private ClusterManager<MyItem> clusterManager;
    private List<MyItem> clusterItems = new ArrayList<>();

    private String[] instructions;
    private Button instructionBtn;

    private Button placeButton;
    private double longi;
    private double lati;
    private LocationManager locationManager;


    public LocationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        placeButton = view.findViewById(R.id.findCurrentPlaceBtn);
        instructionBtn = view.findViewById(R.id.instructionBtn);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreateView: permission not init");
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }

        geoDataClient = Places.getGeoDataClient(getActivity());
        placeDetectionClient = Places.getPlaceDetectionClient(getActivity());
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.locationMapContainer, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);

        placeButton.setOnClickListener(this);
        instructionBtn.setOnClickListener(this);

        return  view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(checkLocationPermission())
            map.setMyLocationEnabled(true);
        clusterManager = new ClusterManager<MyItem>(getActivity(),map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        LatLng latLng = new LatLng(lati,longi);
        map.addMarker(new MarkerOptions().position(latLng).title("BITM").snippet("BDBL Bhaban, Karwanbazar, Dhaka"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        //map.clear();
        /*map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });*/

        getDirections();

    }

    private void getDirections() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionService service = retrofit.create(DirectionService.class);
        String key = getString(R.string.direction_api_key);
        String url = String.format("directions/json?origin=23.773702,90.427118&destination=23.762180,90.443744&alternatives=true&key=%s",key);
        Call<DirectionResponse>call = service.getDirections(url);

        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if(response.code() == 200){
                    DirectionResponse directionResponse = response.body();
                    List<Step>steps = directionResponse.getRoutes()
                            .get(0).getLegs().get(0)
                            .getSteps();
                    instructionBtn.setVisibility(View.VISIBLE);
                    instructions = new String[steps.size()];
                    LatLng endPoint = null;
                    for(int i = 0; i < steps.size(); i++){
                        double startLatitude = steps.get(i).getStartLocation().getLat();
                        double startLongitude = steps.get(i).getStartLocation().getLng();

                        LatLng startPoint = new LatLng(startLatitude,startLongitude);

                        double endLatitude = steps.get(i).getEndLocation().getLat();
                        double endLongitude = steps.get(i).getEndLocation().getLng();

                        endPoint = new LatLng(endLatitude,endLongitude);

                        Polyline polyline = map.addPolyline(new PolylineOptions()
                                .add(startPoint)
                                .add(endPoint)
                                .color(Color.BLUE)
                                .clickable(true));



                        String instructionsLine = String.valueOf(Html.fromHtml(steps.get(i).getHtmlInstructions()));
                        String distance = steps.get(i).getDistance().getText();
                        String time = steps.get(i).getDuration().getText();
                        instructions[i] = instructionsLine+"-"+distance+", "+time;
                        polyline.setTag(instructionsLine);
                    }
                    map.addMarker(new MarkerOptions().position(endPoint));


                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(), "granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},555);
            return false;
        }
        return true;
    }

    public void getCurrentPlaces(View view) {

    }

    private void addCurrentPlaceMarker(String name, String address, LatLng latLng) {
        //map.addMarker(new MarkerOptions().position(latLng).title(name).snippet(address));
        MyItem item = new MyItem(latLng,name,address);
        clusterItems.add(item);
    }

    @Override
    public void onClick(View view) {
        
        Button button = (Button) view;

        if(checkLocationPermission()){
            if (button.getId() == placeButton.getId()) {
                // Toast.makeText(LocationActivity.this, "clicked ", Toast.LENGTH_SHORT).show();
                placeDetectionClient.getCurrentPlace(null)
                        .addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                                //Toast.makeText(LocationActivity.this, "clicked ", Toast.LENGTH_SHORT).show();

                                if(task.isSuccessful() && task.getResult() != null){
                                    Toast.makeText(getActivity(), "clicked ", Toast.LENGTH_SHORT).show();

                                    PlaceLikelihoodBufferResponse response = task.getResult();
                                    int count = response.getCount();
                                    Toast.makeText(getActivity(), "clicked "+count, Toast.LENGTH_SHORT).show();
                                    for(int i = 0; i < count; i++){
                                        PlaceLikelihood placeLikelihood = response.get(i);
                                        String name = (String) placeLikelihood.getPlace().getName();
                                        String address = (String) placeLikelihood.getPlace().getAddress();
                                        LatLng latLng = placeLikelihood.getPlace().getLatLng();
                                        addCurrentPlaceMarker(name,address,latLng);
                                    }
                                    clusterManager.addItems(clusterItems);
                                    clusterManager.cluster();
                                }
                            }
                        });
            } else if (button.getId() == instructionBtn.getId()) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setItems(instructions, null)
                        .show();
            }
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        longi = location.getLongitude();
        lati = location.getLatitude();
        Log.d("langi, lati: ", longi+","+lati);
    }
}
