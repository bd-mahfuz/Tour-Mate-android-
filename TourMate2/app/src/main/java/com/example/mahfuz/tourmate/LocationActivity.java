package com.example.mahfuz.tourmate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mahfuz.tourmate.model.MyItem;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private GoogleMapOptions options;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private ClusterManager<MyItem> clusterManager;
    private List<MyItem> clusterItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        geoDataClient = Places.getGeoDataClient(this);
        placeDetectionClient = Places.getPlaceDetectionClient(this);
        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(checkLocationPermission())
            map.setMyLocationEnabled(true);
        clusterManager = new ClusterManager<MyItem>(this,map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        LatLng latLng = new LatLng(23.7509,90.3935);
        /*map.addMarker(new MarkerOptions().position(latLng).title("BITM").snippet("BDBL Bhaban, Karwanbazar, Dhaka"));*/

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        //map.clear();
        /*map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},555);
            return false;
        }
        return true;
    }

    public void getCurrentPlaces(View view) {
        if(checkLocationPermission())
           // Toast.makeText(LocationActivity.this, "clicked ", Toast.LENGTH_SHORT).show();
            placeDetectionClient.getCurrentPlace(null)
                    .addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            //Toast.makeText(LocationActivity.this, "clicked ", Toast.LENGTH_SHORT).show();

                            if(task.isSuccessful() && task.getResult() != null){
                                Toast.makeText(LocationActivity.this, "clicked ", Toast.LENGTH_SHORT).show();

                                PlaceLikelihoodBufferResponse response = task.getResult();
                                int count = response.getCount();
                                Toast.makeText(LocationActivity.this, "clicked "+count, Toast.LENGTH_SHORT).show();
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
    }

    private void addCurrentPlaceMarker(String name, String address, LatLng latLng) {
        //map.addMarker(new MarkerOptions().position(latLng).title(name).snippet(address));
        MyItem item = new MyItem(latLng,name,address);
        clusterItems.add(item);
    }
}
