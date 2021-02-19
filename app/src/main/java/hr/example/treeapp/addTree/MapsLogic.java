package hr.example.treeapp.addTree;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import hr.example.treeapp.R;

public class MapsLogic{
    private Marker currentMarker;
    private GoogleMap map;

    public MapsLogic (Marker currentMarker, GoogleMap map){
        this.currentMarker = currentMarker;
        this.map = map;
    }

    /**
     * Metoda koja postavlja marker na korisnikovu lokaciju, ako je korisnik dopustio pristup lokaciji
     */
    public void refreshMarkerNoLocation(){
        final LatLng startLocation = new LatLng(44.601505, 16.440230);

        final com.google.android.gms.maps.model.LatLng mapsLatLng =
                new com.google.android.gms.maps.model.LatLng(startLocation.latitude,
                        startLocation.longitude);
        if(currentMarker!=null)
            currentMarker.remove();
        currentMarker = map.addMarker(new MarkerOptions()
                .position(mapsLatLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));

    }
    /**
     * Metoda koja marker postavlja na default lokaciju kada korisnik ne dozvoli pristup svojoj lokaciji ili nema upaljen gps
     */
    public void refreshMarkerLive(double latitude, double longitude){
        final LatLng startLocation = new LatLng(latitude, longitude);

        final com.google.android.gms.maps.model.LatLng mapsLatLng =
                new com.google.android.gms.maps.model.LatLng(startLocation.latitude,
                        startLocation.longitude);
        if(currentMarker!=null)
            currentMarker.remove();
        currentMarker = map.addMarker(new MarkerOptions()
                .position(mapsLatLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
        Float zoomLvl = (float)15;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapsLatLng,zoomLvl));
    }

    public Marker getCurrentMarker(){
        if(currentMarker!=null)
            return currentMarker;
        else return null;
    }

}
