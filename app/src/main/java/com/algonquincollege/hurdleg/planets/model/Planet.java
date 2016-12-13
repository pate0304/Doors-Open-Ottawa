package com.algonquincollege.hurdleg.planets.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Model a planet of our solar system.
 *
 * A Planet has the following properties:
 *   planetId
 *   name
 *   overview
 *   image
 *   description
 *   distance from the Sun
 *   number of moons
 *
 * //TODO: compare this Java class to JSON object: https://planets-hurdleg.mybluemix.net/planets/2
 *
 * Pro-Tip: I used Android Studio IDE to generate the getters & setters. For details:
 *          Canvas > Modules > Resources > How to Generate Getters and Setters
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 * Refracted by Jay Patel(pate0304@algonquinlive.com
 */
public class Planet {
    private int planetId;
    private String name;
    private String overview;
    private String image;
    private String description;

    private List<String> cal;
    private Bitmap bitmap;
    private String address;

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}

    public int getPlanetId() { return planetId; }
    public void setPlanetId(int planetId) { this.planetId = planetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }


    public List<String> getCal() {
        return cal;
    }

    public void setCal(List<String> cal) {
        this.cal = cal;
    }

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }



}
