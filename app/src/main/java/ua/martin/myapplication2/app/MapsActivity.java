package ua.martin.myapplication2.app;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.maps.android.SphericalUtil;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapLongClickListener, OnMarkerDragListener, GoogleMap.OnMyLocationChangeListener {
    private static boolean allowedZoneExists = false;

    //Map
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locMan;

    //GUI
    private TextView debugTextView; //just text view to show coordinates of the marker
    private Button saveButton;

    //Player will be passed from login activity (or from some another activity)
    Player player = Player.getPlayer("Martin");
    private static HashMap <String, Portal> playerPortals = new HashMap<String, Portal>(); //here saved portals, which user put himself

    private static LinkedList<Portal> teamPortals = new LinkedList<Portal>(); //here stores all portals through out the game

    //Settings
    private int allowedRadius = 1000;   //radius of the allowed area
    private LatLng center;              //center of the allowed area
    private int delta = 25;                  //allowed distance between player and portal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);    //show blue marker of ourselves
        mMap.getUiSettings().setCompassEnabled(true);   //show compass after map rotation

        //set-up textview for debugging
        debugTextView = (TextView)findViewById(R.id.location);
        debugTextView.setText("I am ready");

        //nulling static vars (cause of some reasons they don't work without this)
        allowedZoneExists = false;
        playerPortals = new HashMap<String, Portal>();

        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry <String, Portal> entry : playerPortals.entrySet()){
                    //TODO: send info to the server
                    teamPortals.add(entry.getValue());
                }
                debugTextView.setText("all portals were saved");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            //add needed listeners to the map
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerDragListener(this);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
               setUpMap();
            }
        }
    }

    private void setUpMap() {
        //move camera to current location
        locMan = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        Location loc = locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(loc.getLatitude(), loc.getLongitude()))
                .zoom(12.8f)
                .build();
        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpdate);
        mMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMapLongClick (LatLng point){
        if (allowedZoneExists) {
            if (playerPortals.size() < 3 && isInZone(point)) {
                Portal p = new Portal(player.name + (playerPortals.size() + 1), point);
                playerPortals.put(p.getId(), p);
                Marker mMark = mMap.addMarker(new MarkerOptions().position(point)
                        .title(p.getId()).draggable(true));
            }
        } else {
            CircleOptions circleOptions = new CircleOptions()
                    .center(point)
                    .radius(allowedRadius)
                    .fillColor(0x2200FFFF)
                    .strokeWidth(0.2f);
            Circle circle = mMap.addCircle(circleOptions);

            center = point;

            allowedZoneExists = true;
        }
    }

    private LatLng prevMarkerPos;
    @Override
    public void onMarkerDragStart(Marker marker) {
        if (isInZone(marker.getPosition())) {
            prevMarkerPos = marker.getPosition();
        } else {
            prevMarkerPos = center;
            marker.setPosition(center);
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if (!isInZone(marker.getPosition())){
            marker.setPosition(prevMarkerPos);
        } else {
            prevMarkerPos = marker.getPosition();
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        playerPortals.get(marker.getTitle()).setLatLng(prevMarkerPos);
        debugTextView.setText(playerPortals.get(marker.getTitle()).toString());
        marker.setPosition(prevMarkerPos);
    }

    private boolean isInZone(LatLng point){
        return SphericalUtil.computeDistanceBetween(point, center) <= allowedRadius;
    }

    private double debugMinDist = -1;
    @Override
    public void onMyLocationChange(Location location) {
        //TODO: foreach portal check whether we are near it?
        LatLng playerPosition = new LatLng (location. getLatitude(), location.getLongitude());

        for (Portal p : teamPortals){
            if (SphericalUtil.computeDistanceBetween(p.getLatLng(),playerPosition)<=delta){
                debugTextView.setText("Get the portal!");
            }
        }
    }
}
