package ua.martin.myapplication2.app;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by admin on 29.06.2014.
 */
public class Portal {
    private String id;
    private double latitude;
    private double longtitude;
    private boolean closed = false;

    public Portal (String id, LatLng latLng){
        this. id = id;
        this.latitude = latLng.latitude;
        this.longtitude = latLng.longitude;
    }

    public boolean isClosed(){
        return closed;
    }

    public String getId(){
        return id;
    }

    public LatLng getLatLng(){
        return new LatLng (latitude, longtitude);
    }

    public void setLatLng (LatLng newLatLng){
        this.latitude = newLatLng.latitude;
        this.longtitude = newLatLng.longitude;
    }

    @Override
    public String toString(){
        return ""+latitude+','+longtitude;
    }
}
