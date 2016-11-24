package com.algonquincollege.hurdleg.planets;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//import static com.algonquincollege.hurdleg.planets.Constants.detail_address;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.algonquincollege.hurdleg.planets.Constants.detail_calendar;
import static com.algonquincollege.hurdleg.planets.Constants.detail_description;
import static com.algonquincollege.hurdleg.planets.Constants.detail_title;
import static com.algonquincollege.hurdleg.planets.mapConstants.detail_address;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder mGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeocoder = new Geocoder(this, Locale.CANADA);

        TextView title = (TextView) findViewById(R.id.buildingName);
        TextView des = (TextView) findViewById(R.id.buildingDescr);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString(detail_title);
        title.setText(name);
        String description = bundle.getString(detail_description);
        des.setText(description);

        TextView cal = (TextView) findViewById(R.id.cal);
        List<String> calendar = bundle.getStringArrayList(detail_calendar);
        StringBuilder mtempCalender = new StringBuilder();
        for (int i = 0; i < calendar.size(); i++) {
            mtempCalender.append(calendar.get(i) + " \n");

        }
        cal.setText(mtempCalender.toString());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString(Constants.detail_address);
        String joined = address + "ottawa" + "ON";
        pin(joined);


    }

    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());

            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12.0f));

            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }


}
