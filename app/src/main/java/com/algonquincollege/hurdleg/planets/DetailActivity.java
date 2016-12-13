package com.algonquincollege.hurdleg.planets;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.utils.utils.HttpMethod;
import com.algonquincollege.hurdleg.planets.utils.utils.RequestPackage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.algonquincollege.hurdleg.planets.Constants.bid;
import static com.algonquincollege.hurdleg.planets.Constants.detail_calendar;
import static com.algonquincollege.hurdleg.planets.Constants.detail_description;
import static com.algonquincollege.hurdleg.planets.Constants.detail_title;
import static com.algonquincollege.hurdleg.planets.MainActivity.REST_URI;
import static com.algonquincollege.hurdleg.planets.mapConstants.detail_address;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.INVISIBLE);

        mGeocoder = new Geocoder(this, Locale.CANADA);

//        TextView title = (TextView) findViewById(R.id.buildingName);
        TextView des = (TextView) findViewById(R.id.buildingDescr);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString(detail_title);
//        title.setText(name);
        getActionBar().setTitle(name);
        String description = bundle.getString(detail_description);
        des.setText(description);
        int bidd = bundle.getInt(bid);
        Log.d("BUILDING ID", String.valueOf(bidd));
        TextView cal = (TextView) findViewById(R.id.cal);
        List<String> calendar = bundle.getStringArrayList(detail_calendar);
        String[] stringArray = calendar.toArray(new String[0]);
        StringBuilder mtempCalender = new StringBuilder();
        for (int i = 0; i < calendar.size(); i++) {
            mtempCalender.append(calendar.get(i) + " \n");

        }


        cal.setText(mtempCalender);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_delete_data) {
            if (isOnline()) {
                deletePlanet( REST_URI );
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
return true;
        }
    private void deletePlanet(String uri) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        // DELETE the planet with Id 8
        Bundle bundle = getIntent().getExtras();
        int bidd = bundle.getInt(bid);//building id for delete
        Log.d("bid","bid:"+bidd);
        pkg.setUri( uri+"/"+bidd );
        DetailActivity.DoTask deleteTask = new DoTask();
        deleteTask.execute( pkg );
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = com.algonquincollege.hurdleg.planets.utils.utils.HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(DetailActivity.this, "Failed", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

                @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString(Constants.detail_address);
        String joined = address + " ottawa" + " ON";
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
