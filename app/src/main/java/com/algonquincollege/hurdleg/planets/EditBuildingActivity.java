package com.algonquincollege.hurdleg.planets;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.utils.utils.HttpMethod;
import com.algonquincollege.hurdleg.planets.utils.utils.RequestPackage;

import static com.algonquincollege.hurdleg.planets.Constants.bid;
import static com.algonquincollege.hurdleg.planets.Constants.detail_address;
import static com.algonquincollege.hurdleg.planets.Constants.detail_description;
import static com.algonquincollege.hurdleg.planets.Constants.detail_title;
import static com.algonquincollege.hurdleg.planets.MainActivity.REST_URI;

/**
 * Created by shiva on 2016-12-12.
 */

public class EditBuildingActivity extends FragmentActivity {

    private String buildingName;
    private String buildingAdress;
    private String buildingdescription;
    private ProgressBar pb;
    final EditText bname = (EditText) findViewById(R.id.postname);
    final EditText baddress = (EditText) findViewById(R.id.postaddress);
    final EditText bdescription = (EditText) findViewById(R.id.postdescription);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postbuilding);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
//        int bidd = bundle.getInt(bid);
        Planet planet=new Planet();

        String a=planet.getName();


        buildingName=bundle.getString(detail_title);
        bname.setText(buildingName);
        Log.d("tah",buildingName);

        buildingAdress = bundle.getString(detail_address);
        baddress.setText(buildingAdress);
        buildingdescription = bundle.getString(detail_description);
        bdescription.setText(buildingdescription);

        Button btn = (Button) findViewById(R.id.postbutton);
        btn.setText("Update Building");
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                // Perform action on click

                updatePlanet(REST_URI);

            }
        });

        Button cbtn = (Button) findViewById(R.id.cancelButton);
        cbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
//               NavUtils.navigateUpFromSameTask(getParent());
            }
        });
    }

    private void updatePlanet(String uri) {
        Planet planet = new Planet();
        Bundle bundle = getIntent().getExtras();
        int bidd = bundle.getInt(bid);
        buildingName=bname.getText().toString();
        buildingAdress=baddress.getText().toString();
        buildingdescription=bdescription.getText().toString();
        planet.setPlanetId( bidd );

        planet.setName(buildingName);
        planet.setAddress(buildingAdress);
        planet.setDescription(buildingdescription);
        planet.setImage("tmp.png");


        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.PUT);
        pkg.setUri(uri);
        pkg.setParam("name", planet.getName());
        pkg.setParam("address", planet.getAddress());
        pkg.setParam("description", planet.getDescription());
        pkg.setParam("image", planet.getImage());

        EditBuildingActivity.DoTask postTask = new DoTask();
        postTask.execute(pkg);
    }
    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            String content = com.algonquincollege.hurdleg.planets.utils.utils.HttpManager.getData(params[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(EditBuildingActivity.this, "Failed To add a building", Toast.LENGTH_LONG).show();
                return;
            }else{
                Toast.makeText(EditBuildingActivity.this, "Building Succsessfully Updated", Toast.LENGTH_LONG).show();
            }
        }
    }


}
