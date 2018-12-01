package com.holidayjournal.ui.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.holidayjournal.R;
import com.holidayjournal.models.LocationModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.utils.Constants;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class LocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    @BindView(R.id.location_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.location_set_btn)
    Button mSetBtn;

    private final int LOCATION_REQ_ID = 0;

    private GoogleMap mMap;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadMap();

        mSetBtn.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_location;
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMapStyle();

        if (getIntent().getParcelableExtra(Constants.LOCATION) != null) {
            zoomToSelectedLocation();
        } else {
            verifyUserLocationPermission();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                address = null;

                addMarker(latLng);
            }
        });
    }

    private void addMapStyle() {
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                showToast("Failed to apply style.");
            }
        } catch (Resources.NotFoundException e) {
            showToast("No style resource found.");
        }
    }

    private void addMarker(LatLng latLng) {
        try {
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            List<Address> locations = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = locations.get(0);
            mMap.addMarker(new MarkerOptions().position(latLng).title(address.getLocality())).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomToSelectedLocation() {
        LocationModel locationModel = getIntent().getParcelableExtra(Constants.LOCATION);
        LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(locationModel.getName())).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
    }

    private void zoomToUserLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_set_btn:
                returnLocation();
                break;
        }
    }

    private void returnLocation() {
        if (this.address != null) {
            LocationModel locationModel = new LocationModel();
            locationModel.setName(address.getLocality());
            locationModel.setLatitude(address.getLatitude());
            locationModel.setLongitude(address.getLongitude());

            Intent intent = new Intent();
            intent.putExtra(Constants.LOCATION, locationModel);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            showToast("Please set a location.");
        }
    }

    private void verifyUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQ_ID);

        } else {
            zoomToUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQ_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                zoomToUserLocation();
            }
        }
    }
}
