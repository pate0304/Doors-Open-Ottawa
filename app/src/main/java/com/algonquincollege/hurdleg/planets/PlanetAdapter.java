package com.algonquincollege.hurdleg.planets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.hurdleg.planets.model.Planet;

import static android.content.ContentValues.TAG;

/**
 * Purpose: customize the Planet cell for each planet displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class PlanetAdapter extends ArrayAdapter<Planet> {

    private Context context;
    private List<Planet> planetList;




    public PlanetAdapter(Context context, int resource, List<Planet> objects) {
        super(context, resource, objects);
        this.context = context;
        this.planetList = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_planet, parent, false);

        //Display planet name in the TextView widget
        Planet planet = planetList.get(position);
        TextView title = (TextView) view.findViewById(R.id.textView1);
        TextView address = (TextView) view.findViewById(R.id.textView2);

        title.setText(planet.getName());
        address.setText(planet.getAddress());


        if (planet.getBitmap() != null) {
            Log.i( "PLANETS", planet.getName() + "\tbitmap in memory" );
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(planet.getBitmap());
        }
        else {
            Log.i( "PLANETS", planet.getName() + "\tfetching bitmap using AsyncTask");
            PlanetAndView container = new PlanetAndView();
            container.planet = planet;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return view;
    }


    // container for AsyncTask params
    private class PlanetAndView {
        public Planet planet;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<PlanetAndView, Void, PlanetAndView> {

        @Override
        protected PlanetAndView doInBackground(PlanetAndView... params) {

            PlanetAndView container = params[0];
            Planet planet = container.planet;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + planet.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                planet.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PlanetAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
            result.planet.setBitmap(result.bitmap);
        }
    }


}
