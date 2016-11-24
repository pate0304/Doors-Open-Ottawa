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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.parsers.PlanetJSONParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 */
public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    // URL to my RESTful API Service hosted on my Bluemix account.
// public static final String REST_URI = "https://planets-hurdleg.mybluemix.net/planets";
    public static final String REST_URI = " https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    private ProgressBar pb;
    private List<MyTask> tasks;
    private List<Planet> planetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("ABOUT DIALOG");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Jay Patel (pate0304)"+"\nGregoire Rosier")
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
        }
        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        PlanetAdapter adapter = new PlanetAdapter(this, R.layout.item_planet, planetList);
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
        Planet theSelectedPlanet = planetList.get(position);
//        Toast.makeText(this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();


//        TextView title = (TextView) findViewById(R.id.detailTitle);
        String name = theSelectedPlanet.getName().toString();
//        title.setText(name);
        String dess = theSelectedPlanet.getDescription().toString();
        String address = theSelectedPlanet.getAddress().toString();
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra(detail_address, address);
        intent.putExtra(detail_title, name);
        intent.putExtra(detail_description, dess);
        intent.putStringArrayListExtra(detail_calendar, (ArrayList<String>) theSelectedPlanet.getCal());
//        title.setText(name);
        startActivity(intent);

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

            String content = HttpManager.getData(params[0]);
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

            planetList = PlanetJSONParser.parseFeed(result);
            updateDisplay();
        }
    }
}