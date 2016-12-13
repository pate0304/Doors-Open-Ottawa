package com.algonquincollege.hurdleg.planets;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.hurdleg.planets.model.Planet;
import com.algonquincollege.hurdleg.planets.utils.utils.HttpMethod;
import com.algonquincollege.hurdleg.planets.utils.utils.RequestPackage;

import static com.algonquincollege.hurdleg.planets.MainActivity.REST_URI;

/**
 * Created by shiva on 2016-12-11.
 */

public class NewBuildingActivity extends FragmentActivity {

    private String buildingName;
    private String buildingAdress;
    private String buildingdescription;
    private ProgressBar pb;



    private static final String TAG ="tag" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postbuilding);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onCreate: "+"new POST building view");

//        if(TextUtils.isEmpty(buildingName)) {
//            bname.setError("Enter Building Name");
//            return;
//        }
        Button btn = (Button) findViewById(R.id.postbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                // Perform action on click

                   postBuilding();

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

    private void postBuilding(){
        final EditText bname = (EditText) findViewById(R.id.postname);
        buildingName=bname.getText().toString();
        final EditText baddress = (EditText) findViewById(R.id.postaddress);
        buildingAdress = baddress.getText().toString();
        final EditText bdescription = (EditText) findViewById(R.id.postdescription);
        buildingdescription = bdescription.getText().toString();

        createPlanet(REST_URI);
    }

            private void createPlanet(String uri) {
                Planet planet = new Planet();
                planet.setName(buildingName);
                planet.setAddress(buildingAdress);
                planet.setDescription(buildingdescription);
                planet.setImage("tmp.png");


                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.POST);
                pkg.setUri(uri);
                pkg.setParam("name", planet.getName());
                pkg.setParam("address", planet.getAddress());
                pkg.setParam("description", planet.getDescription());
                pkg.setParam("image", planet.getImage());

                NewBuildingActivity.DoTask postTask = new DoTask();
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
                        Toast.makeText(NewBuildingActivity.this, "Failed To add a building", Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        Toast.makeText(NewBuildingActivity.this, "Building Succsessfully Added", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }