package ch.epfl.sweng.runpharaa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import ch.epfl.sweng.runpharaa.database.TrackDatabaseManagement;
import ch.epfl.sweng.runpharaa.database.UserDatabaseManagement;
import ch.epfl.sweng.runpharaa.tracks.Track;
import ch.epfl.sweng.runpharaa.tracks.TrackProperties;
import ch.epfl.sweng.runpharaa.tracks.TrackType;
import ch.epfl.sweng.runpharaa.user.User;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker;

public class TrackPropertiesActivity extends AppCompatActivity implements OnMapReadyCallback {
    //TODO: Check if ScrollView is working!
    private GoogleMap map;
    private LatLng[] points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_properties);
        final Intent intent = getIntent();

        TrackDatabaseManagement.mReadDataOnce(TrackDatabaseManagement.TRACKS_PATH, new TrackDatabaseManagement.OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                final String trackID = intent.getStringExtra("TrackID");
                final Track track = TrackDatabaseManagement.initTrack(data, trackID);
                points = CustLatLng.CustLatLngToLatLng(track.getPath()).toArray(new LatLng[track.getPath().size()]);

                TrackProperties tp = track.getProperties();

                ImageView trackBackground = findViewById(R.id.trackBackgroundID);
                //trackBackground.setImageBitmap(track.getImage()); //TODO: For caching?
                new DownloadImageTask(trackBackground)
                        .execute(track.getImageStorageUri());

                TextView trackTitle = findViewById(R.id.trackTitleID);
                trackTitle.setText(track.getName());

                TextView trackCreator = findViewById(R.id.trackCreatorID);
                //TODO: add the creatorName attribute back to Track.
                trackCreator.setText("By Test User" /*+ track.getCreatorUid()*/);

                //TODO: Add real duration once it is included in the DB.
                TextView trackDuration = findViewById(R.id.trackDurationID);
                trackDuration.setText("Duration: " /*+ tp.getAvgDuration()*/ + "5 minutes");


                TextView trackLength = findViewById(R.id.trackLengthID);
                trackLength.setText("Length: " + Double.toString(tp.getLength()) + " m");

                /*
                TextView trackHeightDifference = findViewById(R.id.trackHeightDiffID);
                trackHeightDifference.setText("Height Difference: " + Double.toString(track.getHeight_diff())); //TODO: Figure out height difference.
                */

                TextView trackLikes = findViewById(R.id.trackLikesID);
                trackLikes.setText(""+tp.getLikes());

                TextView trackFavourites = findViewById(R.id.trackFavouritesID);
                trackFavourites.setText(""+tp.getFavorites());

                ToggleButton toggleLike = findViewById(R.id.buttonLikeID);
                ToggleButton toggleFavorite = findViewById(R.id.buttonFavoriteID);

                // Check if the user already liked this track and toggle the button accordingly
                toggleLike.setChecked(User.instance.alreadyLiked(trackID));

                toggleLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            updateLikes(track, trackID);
                        } else {
                            updateLikes(track, trackID);
                        }
                    }
                });

                // Check if the track already in favorites and toggle the button accordingly
                toggleFavorite.setChecked(User.instance.alreadyInFavorites(trackID));

                toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            updateNbFavorites(track, trackID);
                        } else {
                            updateNbFavorites(track, trackID);
                        }
                    }
                });

                /*
                TextView trackTags = findViewById(R.id.trackTagsID);
                trackTags.setText(createTagString(track));
                */

                drawTrackOnMap();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.d("DB Read: ", "Failed to read data from DB in TrackPropertiesActivity.");
            }
        });

        // Get map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.create_map_view2);
        mapFragment.getMapAsync(this);
    }

    private String createTagString(Track track) {
        Set<TrackType> typeSet = track.getProperties().getType();
        int nbrTypes = typeSet.size();
        String[] trackType = getResources().getStringArray(R.array.track_types);

        String start = (nbrTypes > 1)?"Tags: ":"Tag: ";

        StringBuilder sb = new StringBuilder();
        sb.append(start);

        int i = 0;

        for(TrackType tt : typeSet){

            sb.append(trackType[TrackType.valueOf(tt.name()).ordinal()]);
            if(i < nbrTypes - 1) sb.append(", ");

            i++;
        }

        return sb.toString();
    }

    private void updateLikes(Track track1, String trackID) {
        final Track track = track1;
        if (User.instance.alreadyLiked(trackID)) {
            track.getProperties().removeLike();
            User.instance.unlike(trackID);
            UserDatabaseManagement.removeLikedTrack(trackID);
        } else {
            track.getProperties().addLike();
            User.instance.like(trackID);
            UserDatabaseManagement.updateLikedTracks(User.instance);
        }
        TrackDatabaseManagement.updateTrack(track);
        runOnUiThread(new Runnable() {
            public void run() {
                TextView trackLikesUpdated = findViewById(R.id.trackLikesID);
                trackLikesUpdated.setText(""+track.getProperties().getLikes());
            }
        });
    }

    private void updateNbFavorites(Track track1, String trackID) {
        final Track track = track1;
        if (User.instance.alreadyInFavorites(trackID)) {
            track.getProperties().removeFavorite();
            User.instance.removeFromFavorites(trackID);
            UserDatabaseManagement.removeFavoriteTrack(trackID);
        } else {
            track.getProperties().addFavorite();
            User.instance.addToFavorites(trackID);
        }

        TrackDatabaseManagement.updateTrack(track);
        UserDatabaseManagement.updateFavoriteTracks(User.instance);
        runOnUiThread(() -> {
            TextView trackFavoritesUpdated = findViewById(R.id.trackFavouritesID);
            trackFavoritesUpdated.setText(""+track.getProperties().getFavorites());
        });

    }

    private Track getTrackByID(ArrayList<Track> tracks, String trackID) {
        for (Track t : tracks) {
            if (t.getTrackUid().equals(trackID)) {
                return t;
            }
        }
        return null;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            Bitmap decoded = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                mIcon11 = BitmapFactory.decodeStream(in, null, options);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                mIcon11.compress(Bitmap.CompressFormat.PNG, 50, out);
                decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return decoded;
        }

        /**
         ** Set the ImageView to the bitmap result
         * @param result
         */
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Prepare the map that we are going to draw the track on
        map = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        // Make map static
        //map.getUiSettings().setAllGesturesEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        // Adapt padding to fit markers
        map.setPadding(50, 150, 50, 50);
    }

    /**
     * Draws the full track and markers on the map
     */
    private void drawTrackOnMap() {
        if (map != null && points != null) {
            Log.i("Create Map : ", "Drawing on map in TrackPropertiesActivity.");
            // Get correct zoom
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng point : points)
                boundsBuilder.include(point);
            LatLngBounds bounds = boundsBuilder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.35);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0));
            // Add lines
            map.addPolyline(new PolylineOptions().addAll(Arrays.asList(points)));
            // Add markers (start = green, finish = red)
            map.addMarker(new MarkerOptions().position(points[0]).icon(defaultMarker(150)).alpha(0.8f));
            map.addMarker(new MarkerOptions().position(points[points.length - 1]).icon(defaultMarker(20)).alpha(0.8f));
        }
    }

}