package com.utem.ftmk.ws2.arsconsumer.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.utils.LocationUtil;

import java.util.Arrays;

public class MainLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback, PlaceSelectionListener,
        GoogleMap.OnMyLocationButtonClickListener {

    public static final int RESULT_PERMISSION_DINED = 101;
    public static final String EXTRA_SELECTED_LOCATION = "extra_selected_location";
    private static final String CODE_MALAYSIA = "MY";
    private static final float DEFAULT_ZOOM = 15;

    private GoogleMap googleMap;
    private Client.Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

        Places.initialize(this, getString(R.string.google_maps_key));

        AutocompleteSupportFragment autoCompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fg_autocomplete);
        assert autoCompleteSupportFragment != null;
        autoCompleteSupportFragment.setCountry(CODE_MALAYSIA);
        autoCompleteSupportFragment.setOnPlaceSelectedListener(this);
        autoCompleteSupportFragment.setPlaceFields(Arrays.asList
                (Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setResult(RESULT_PERMISSION_DINED);
            finish();
            return;
        }

        this.googleMap = googleMap;
        location = (Client.Location) getIntent()
                .getSerializableExtra(MainActivity.EXTRA_INITIAL_LOCATION);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        googleMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        LocationUtil.getLastKnownLocation(this, location -> {
            this.location = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        });
        return true;
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        LatLng latLng = place.getLatLng();
        assert latLng != null;
        location = new Client.Location();
        location.setName(place.getName());
        location.setAddress(place.getAddress());
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
    }

    @Override
    public void onError(@NonNull Status status) {
        Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
    }

    public void onLocatedMarkerClick(View view) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    public void onSelectLocationClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_LOCATION, location);
        setResult(RESULT_OK, intent);
        finish();
    }
}