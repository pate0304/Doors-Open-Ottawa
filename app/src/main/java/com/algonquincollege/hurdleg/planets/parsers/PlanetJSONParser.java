package com.algonquincollege.hurdleg.planets.parsers;

import android.util.Log;

import com.algonquincollege.hurdleg.planets.model.Planet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Parse a JSON object for a Planet.
 * <p>
 * //TODO: compare this parser to JSON array: https://planets-hurdleg.mybluemix.net/planets
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *         <p>
 *         Reference: FlowerJSONParser in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class PlanetJSONParser {

    public static List<Planet> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray planetArray = jsonResponse.getJSONArray("buildings");
            List<Planet> planetList = new ArrayList<>();

            for (int i = 0; i < planetArray.length(); i++) {

                JSONObject obj = planetArray.getJSONObject(i);
                Planet planet = new Planet();
                planet.setName(obj.getString("name"));
                planet.setImage(obj.getString("image"));
                planet.setAddress(obj.getString("address"));

                List<String> listOpenHours = new ArrayList<>();

                JSONArray openhours = new JSONArray(obj.getString("open_hours"));
                for (int j = 0; j < openhours.length(); j++) {
                    listOpenHours.add(openhours.get(j).toString());
                }
                planet.setCal(listOpenHours);
//                planet.setPlanetId(obj.getInt("planetId"));
//                planet.setName(obj.getString("name"));
//                planet.setOverview(obj.getString("overview"));
//                planet.setImage(obj.getString("image"));
                planet.setDescription(obj.getString("description"));
//                planet.setDistanceFromSun(obj.getDouble("distance_from_sun"));
//                planet.setNumberOfMoons(obj.getInt("number_of_moons"));

                planetList.add(planet);
            }

            return planetList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }
}
