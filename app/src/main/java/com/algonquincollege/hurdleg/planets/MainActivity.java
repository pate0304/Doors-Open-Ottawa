package com.algonquincollege.hurdleg.planets;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.parsers.PlanetJSONParser;
import com.algonquincollege.hurdleg.planets.utils.utils.HttpMethod;
import com.algonquincollege.hurdleg.planets.utils.utils.RequestPackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.algonquincollege.hurdleg.planets.Constants.bid;
import static com.algonquincollege.hurdleg.planets.Constants.detail_address;
import static com.algonquincollege.hurdleg.planets.Constants.detail_calendar;
import static com.algonquincollege.hurdleg.planets.Constants.detail_title;
import static com.algonquincollege.hurdleg.planets.Constants.detail_description;

/**
 * Displaying web service data in a ListActivity.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *         <p>
 *         Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 * @see {PlanetAdapter}
 * @see {res.layout.item_planet.xml}
 * Refracted by Jay Patel(pate0304@algonquinlive.com)Final Doors Open Ottawa
 */
public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    // URL to Garry's RESTful API Service hosted on my Bluemix account.
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<GetTask> gtasks;
    private List<Planet> buildingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ottawa);
        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setTitle("");
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        gtasks = new ArrayList<>();
        if (isOnline()) {
            getPlanets(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        //Swipe down to refresh List
        final SwipeRefreshLayout mySwipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        mySwipeRefreshLayout.setProgressViewOffset(true,1,5);
                        mySwipeRefreshLayout.setColorSchemeColors(3443);
                        getPlanets(REST_URI);
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }

        );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


//NavBar icons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //for the sort option
        if (item.isCheckable()) {
            // leave if the list is null
            if (buildingList == null) {
                return true;
            }

            // which sort menu item did the user pick?
            switch (item.getItemId()) {
                case R.id.action_sort_name_asc:
                    Collections.sort(buildingList, new Comparator<Planet>() {
                        @Override
                        public int compare(Planet lhs, Planet rhs) {
                            Log.i("PLANETS", "Sorting planets by name (a-z)");
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Planet>() {
                        @Override
                        public int compare(Planet lhs, Planet rhs) {
                            Log.i("PLANETS", "Sorting planets by name (z-a)");
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    }));
                    break;


            }
            // remember which sort option the user picked
            item.setChecked(true);
            // re-fresh the list to show the sort order
            ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
        } // END if item.isChecked()


        //POST PUT DELETE options

          //POST
        if (item.getItemId() == R.id.action_post_data) {
            if (isOnline()) {
//                createPlanet( REST_URI );
                Intent myIntent = new Intent(this,NewBuildingActivity.class);
                startActivity(myIntent);

            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
//          //PUT
//        if (item.getItemId() == R.id.action_put_data) {
//            if (isOnline()) {
//                updatePlanet( REST_URI );
//            } else {
//                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
//            }
//        }
          //DELETE
//        if (item.getItemId() == R.id.action_delete_data) {
//            if (isOnline()) {
//                deletePlanet( REST_URI );
//            } else {
//                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
//            }
//        }





//About Dialog and buttons
        if (item.getItemId() == R.id.action_get_data) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle("Doors Open Ottawa");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Author: Jay Patel (pate0304)")
                    .setCancelable(false)
                    .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return false;
        }

return true;
    }


    private void createPlanet(String uri) {
        Planet planet = new Planet();
        planet.setName( "pate0304" );
        planet.setAddress("Deerfield drive Ottawa ON");
        planet.setDescription( " Jay Patel" );
        planet.setImage( "images/neptune.png" );


        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri( uri );
        pkg.setParam("name", planet.getName() );
        pkg.setParam("address",planet.getAddress());
        pkg.setParam("description", planet.getDescription() );
        pkg.setParam("image",planet.getImage());


        DoTask postTask = new DoTask();
        postTask.execute( pkg );
    }


//
//    private void requestData(String uri) {
//        MyTask task = new MyTask();
//        task.execute(uri);
//    }
    private void getPlanets(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod( HttpMethod.GET );
        getPackage.setUri( uri );

        GetTask getTask = new GetTask();
        getTask.execute( getPackage );
//        MyTask task = new MyTask();
//        task.execute(uri);
    }
    private void deletePlanet(String uri) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        // DELETE the planet with Id 8
        pkg.setUri( uri + "/218" );
        DoTask deleteTask = new DoTask();
        deleteTask.execute( pkg );
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        PlanetAdapter adapter = new PlanetAdapter(this, R.layout.item_planet, buildingList);
        setListAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Planet theSelectedPlanet = buildingList.get(position);
//        Toast.makeText(this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();
//        this.onKeyLongPress(buildingList.get(position),changeView());

//        TextView title = (TextView) findViewById(R.id.detailTitle);
        String name = theSelectedPlanet.getName().toString();
        String dess = theSelectedPlanet.getDescription().toString();
        String address = theSelectedPlanet.getAddress().toString();
        int bidd = theSelectedPlanet.getPlanetId();
        Log.d("Building id ", String.valueOf(bidd));
//        String date = theSelectedPlanet.getOverview().toString();
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra(detail_address, address);
        intent.putExtra(detail_title, name);
        intent.putExtra(detail_description, dess);
        intent.putExtra(bid,bidd);
//        intent.putExtra(detail_calendar, date);
        intent.putStringArrayListExtra(detail_calendar, (ArrayList<String>) theSelectedPlanet.getCal());
//        title.setText(name);
        startActivity(intent);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Planet theSelectedPlanet = buildingList.get(position);
//        Toast.makeText(this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();
//        this.onKeyLongPress(buildingList.get(position),changeView());

//        TextView title = (TextView) findViewById(R.id.detailTitle);
        String name = theSelectedPlanet.getName().toString();
        String dess = theSelectedPlanet.getDescription().toString();
        String address = theSelectedPlanet.getAddress().toString();
        int bidd = theSelectedPlanet.getPlanetId();
        Log.d("Building id ", String.valueOf(bidd));
//        String date = theSelectedPlanet.getOverview().toString();
        Intent intent = new Intent(getApplicationContext(), EditBuildingActivity.class);
        intent.putExtra(detail_address, address);
        intent.putExtra(detail_title, name);
        intent.putExtra(detail_description, dess);
        intent.putExtra(bid,bidd);
//        intent.putExtra(detail_calendar, date);
        intent.putStringArrayListExtra(detail_calendar, (ArrayList<String>) theSelectedPlanet.getCal());
//        title.setText(name);
        startActivity(intent);
        return false;
    }


    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData( params[0], "pate0304", "password" );
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = PlanetJSONParser.parseFeed(result);
            updateDisplay();
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
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    private class GetTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            if (gtasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            gtasks.add(this);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = com.algonquincollege.hurdleg.planets.utils.utils.HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            gtasks.remove(this);
            if (gtasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = PlanetJSONParser.parseFeed(result);
            updateDisplay();



        }
    }
}