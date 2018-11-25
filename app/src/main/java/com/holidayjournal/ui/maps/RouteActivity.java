package com.holidayjournal.ui.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.holidayjournal.R;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.models.LocationModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.utils.Constants;

import butterknife.BindView;

public class RouteActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.route_toolbar)
    Toolbar mToolbar;

    private final int STORAGE_REQ_ID = 0;

    private GoogleMap mMap;
    private HolidayModel holiday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        holiday = getIntent().getParcelableExtra(Constants.HOLIDAY);

        loadMap();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_route;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_route_map_share:
                verifyStoragePermissions();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.route_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (holiday != null) {
            for (int i = 0; i < holiday.getDays().size(); i++) {
                LocationModel firstLocation = holiday.getDays().get(i).getLocation();
                if (firstLocation != null) {
                    addMarker(firstLocation);

                    if ((i != (holiday.getDays().size() - 1)) && holiday.getDays().get(i + 1).getLocation() != null) {
                        LocationModel secondLocation = holiday.getDays().get(i + 1).getLocation();
                        LatLng first = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
                        LatLng second = new LatLng(secondLocation.getLatitude(), secondLocation.getLongitude());
                        drawLine(first, second);
                    }
                }
            }
            if (holiday.getDays().get(0).getLocation() != null) {
                zoomToFirstLocation(holiday.getDays().get(0).getLocation());
            }
        }
    }

    private void addMarker(LocationModel location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng)).setTitle(location.getName());
    }

    private void drawLine(LatLng first, LatLng second) {
        PolylineOptions line = new PolylineOptions();
        line.add(first, second);
        line.width(5).color(Color.RED);
        mMap.addPolyline(line);
    }

    private void zoomToFirstLocation(LocationModel firstLocation) {
        LatLng latLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f));
    }

    private void shareRouteMap() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, holiday.getTitle(), null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, holiday.getTitle());
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                intent.setType("*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
            }
        };
        mMap.snapshot(callback);
    }

    private void verifyStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQ_ID);

        } else {
            shareRouteMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQ_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareRouteMap();
                }
                break;
            }
        }
    }

}