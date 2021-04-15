package com.utem.ftmk.ws2.arsclient.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.utem.ftmk.ws2.arsclient.R;
import com.utem.ftmk.ws2.arsclient.model.client.Client;
import com.utem.ftmk.ws2.arsclient.ui.authentication.RegistrationActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

// TODO: Handle don't ask again in permission dialog
// TODO: Add selected location image and mini map to confirm the user selection
// TODO: Modify getCurrentLocation to PlaceDetectionClient.getCurrentPlace()

public class SelectLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback, PlaceSelectionListener {

    public static final int RESULT_PERMISSION_DINED = 102;
    public static final String EXTRA_INIT_LOCATION = "extra_init_location";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String CODE_MALAYSIA = "MY";
    private static final LatLng DEFAULT_LAT_LNG = new LatLng(2.3137898, 102.3189279); // UTeM Kafeteria 1
    private static final float DEFAULT_ZOOM = 20;

    public static final String SELECTED_LOCATION = "selected_location";

    private GoogleMap mMap;
    private Client.Location mLocation;
    private Button mButtonSelect;
    private FloatingActionButton mFabMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        // [START initialize_view]
        mButtonSelect = findViewById(R.id.button_select_location);
        mFabMarker = findViewById(R.id.fab_marker);
        // [END initialize_view]

        // [START initialize_autocomplete_support_fragment]
        // Initialize the SDK
        Places.initialize(this, getString(R.string.google_maps_key));
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList
                (Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        // Set up filter results by Malaysia
        autocompleteFragment.setCountry(CODE_MALAYSIA);
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        // [END initialize_autocomplete_support_fragment]

        // [START initialize_support_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // [END initialize_support_map_fragment]

    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean granted = checkLocationPermission();
        if (!granted)
            return;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        initializeDeviceLocation();
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                setResult(RESULT_PERMISSION_DINED);
                finish();
            }
        }
    }

    /**
     * Checks to prompt the user for permission to use the device location.
     */
    private boolean checkLocationPermission() {
        boolean granted = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            granted = false;
        }
        return granted;
    }

    /**
     * Initializes the current location of the device, and positions the map's camera.
     */
    private void initializeDeviceLocation() throws SecurityException {
        Client.Location initLocation = (Client.Location) getIntent()
                .getSerializableExtra(EXTRA_INIT_LOCATION);

        if (initLocation != null) {
            mMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(initLocation.getLatLng(), DEFAULT_ZOOM));
            mMap.addMarker(new MarkerOptions()
                    .position(initLocation.getLatLng()).title(initLocation.getName()));
            mButtonSelect.setEnabled(true);
            mFabMarker.setEnabled(true);
        } else {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnCompleteListener(task -> {
                        Location location = task.getResult();
                        LatLng latLng = location != null ?
                                new LatLng(location.getLatitude(), location.getLongitude()) :
                                DEFAULT_LAT_LNG;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    });
        }
    }

    @Override
    public void onPlaceSelected(@NotNull Place place) {
        mLocation = new Client.Location(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getLatLng()
        );
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
        mButtonSelect.setEnabled(true);
        mFabMarker.setEnabled(true);
    }

    @Override
    public void onError(@NotNull Status status) {
        Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
        mButtonSelect.setEnabled(false);
        mFabMarker.setEnabled(false);
    }

    public void onSelectLocationClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_LOCATION, mLocation);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onLocateMarkerClicked(View view) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation.getLatLng(), DEFAULT_ZOOM));
    }

}