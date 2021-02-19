package hr.example.treeapp.addTree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import hr.example.treeapp.R;


public class AddTreeActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, LocationListener {
    TextView treeDescription;

    ImageView imageView;
    Uri filePath;

    Uri finalImageUri;

    GoogleMap map;
    Marker marker;

    List<String> hashtags;

    protected LocationManager locationManager;
    protected Context context;
    protected double latitude, longitude;
    private int counter=0;

    private AlertDialog.Builder builder;
    private SharedPreferences.Editor editor;

    private boolean pressedLater;
    private boolean pressedDontAskAgain;
    private long laterPressedTime;
    private Button uploadButton;

    private final String[] permissions = {"Camera", "Media & Storage"};
    private boolean cameraPermsOk=false;
    private boolean locationPremsOk=false;
    private PermissionsChecks permissionsChecks;

    private ImageManipulation imageManipulation;
    private MapsLogic mapsLogic;
    private final HashtagsLogic hashtagsLogic = new HashtagsLogic();
    AddTreeLogic addTreeLogic;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree2);

        treeDescription = findViewById(R.id.treeDescriptionText);
        imageView = findViewById(R.id.treeImageView);
        builder = new AlertDialog.Builder(AddTreeActivity.this);
        imageManipulation = new ImageManipulation(this);
        permissionsChecks = new PermissionsChecks(this);
        addTreeLogic = new AddTreeLogic(this);
        uploadButton=findViewById(R.id.plantTreeButton);

        setUpPermissionsRequest();

        checkPermissionsOnStartup();

        mapInitialization();

        verifyPermissions();

        startingListeners();

        //scrollBlocker();

    }

    private void scrollBlocker() {
        ScrollView mainScrollView = (ScrollView) findViewById(R.id.add_tree_scroll_view);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {

            }
        });
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //mainScrollView.requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //mainScrollView.requestDisallowInterceptTouchEvent(false);
                Float zoomLvl = map.getCameraPosition().zoom;
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),zoomLvl));
            }
        });

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    private void setUpPermissionsRequest() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        pressedLater = sharedPref.getBoolean(getResources().getString(R.string.later), false);
        pressedDontAskAgain = sharedPref.getBoolean(getResources().getString(R.string.dont_ask_again), false);
        //laterPressedTime = sharedPref.getLong(getResources().getString(R.string.later_pressed_time), 0);
    }

    @SuppressLint("MissingPermission")
    private void verifyPermissions() {
        if (permissionsChecks.checkLocationPermissions()){
            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);
                    locationPremsOk=true;
                }
            }
            //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        if (permissionsChecks.checkCameraAndStoragePermissions()){
            cameraPermsOk=true;
        }
    }

    private void checkPermissionsOnStartup() {
        if (!permissionsChecks.checkAllPermissions()) {
            alertDialogeForAskingPermissions();
            // check if pressedLater variable is been true
        } else if (pressedLater) {
            if (laterPressedTime != 0) {
                //check if its been 1 hour since later is been pressed.
                /**
                Date dateObj = new Date();
                long timeNow = dateObj.getTime();
                long oneHourLater = laterPressedTime + (3600 * 1000);
                if (oneHourLater <= timeNow) {

                    requestPermission();
                    editor.putBoolean(getResources().getString(R.string.later), false);
                    editor.commit();
                }*/

            }
            // If pressed don't ask again the app should bot request permissions again.
        } else if (!pressedDontAskAgain)
            requestPermission();
    }

    private void startingListeners(){
        imageView.setOnClickListener(v -> {
            selectImage();
            treeDescription.setText(Html.fromHtml(hashtagsLogic.colorHastags(treeDescription)));
        });
        treeDescription.setOnFocusChangeListener((v, hasFocus) -> treeDescription.setText(Html.fromHtml(hashtagsLogic.colorHastags(treeDescription))));

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                finish();
            }
        });
    }

    private void mapInitialization() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //provjera je li korisnik dao dopuštenja, ako nije od korisnika se traži davanje dopuštenja
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (!permissionsChecks.checkAnyCameraAndStoragePermission()) {

                //few important permissions were not been granted
                // ask the user again.
                alertDialoge();
                mapsLogic.refreshMarkerNoLocation();
            }
            else {
                if (locationManager != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);
                        locationPremsOk=true;
                    }
                }
                //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }
    private void alertDialoge() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialoge_desc).setTitle(R.string.we_request_again);

        builder.setCancelable(false)
                .setPositiveButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton(R.string.dont_ask_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'Don't Ask Again' Button
                        // the sharedpreferences value is true
                        editor.putBoolean(getResources().getString(R.string.dont_ask_again), true);
                        editor.commit();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void alertDialogeForAskingPermissions() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(getResources().getString(R.string.app_name) + " needs access to " + permissions[0] + ", " + permissions[1]).setTitle(R.string.permissions_required);

        //Setting message manually and performing action on button click
        //builder.setMessage("Do you want to close this application ?")
        builder.setCancelable(false)
                .setPositiveButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Later'
                        //Saving later boolean value as true, also saving time of pressed later
                        Date dateObj = new Date();
                        long timeNow = dateObj.getTime();
                        //refreshMarkerNoLocation();
                        editor.putLong(getResources().getString(R.string.later_pressed_time), timeNow);
                        editor.putBoolean(getResources().getString(R.string.later), true);
                        editor.commit();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void requestPermission() {
        if (permissionsChecks.checkBuildVersion()) {
            if (!permissionsChecks.checkAnyImportantPermission()) {
                ActivityCompat.requestPermissions(AddTreeActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                cameraPermsOk=true;
                locationPremsOk=true;
            }
        }
    }

    //prikaz izbornika za odabir načina uploada,
    private void selectImage() {
        final CharSequence[] options = { getString(R.string.take_photo), getString(R.string.choose_photo_from_gallery),getString(R.string.cancel)};

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AddTreeActivity.this);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(cameraPermsOk){
                    if (options[item].equals(getString(R.string.take_photo)))
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = null;
                        try{
                            f=imageManipulation.createImageFile();
                        }
                        catch (IOException ex){
                            Toast.makeText(AddTreeActivity.this, "Error at file create", Toast.LENGTH_LONG).show();
                        }
                        if (f != null) {
                            Uri photoURI = FileProvider.getUriForFile(AddTreeActivity.this,
                                    "hr.example.treeapp.fileprovider",
                                    f);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, 1);
                        }
                    }
                    else if (options[item].equals(getString(R.string.choose_photo_from_gallery)))
                    {
                        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                    else if (options[item].equals(getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                }
                else
                    alertDialoge();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(imageManipulation.currentPhotoPath);
                Uri urif = Uri.fromFile(f);
                if (urif!=null);
                imageManipulation.startCrop(urif);
            }

            else if (requestCode == 2) {
                filePath = data.getData();
                if (filePath!=null);
                imageManipulation.startCrop(filePath);
            }
        }
        if (requestCode==UCrop.REQUEST_CROP && resultCode==RESULT_OK){
            Uri path=UCrop.getOutput(data);
            finalImageUri=path;
            if(path!=null)
                setImageToView(path);
        }
    }

    /**
     * metoda preuzima Uri slike, uklanja okvir s ikonom i postavlja novi okvir gdje se slika prikazuje s dohvaćenim URI
     * @param uri - uri slike,  iz fotogalerije ili iz fotoaparata
     */
    private void setImageToView(Uri uri){
        imageView.setImageBitmap(null);
        imageView.setImageURI(uri);
    }

    /**
     * Poziva se kada korisnik klikne na discard. Svi podaci se odbacuju.
     * @param view
     */
    public void discard(View view) {
        finish();
    }

    /**
     * U ovoj metodi se poziva sloj poslovne logike preko kojeg se objava pohranjuje u Firebase
     */
    public void uploadImage() {
        LatLng treeLocation = getPinedLocation(); //lokacija pin-a
        Uri image= finalImageUri; //uri of a cropped image
        String treeDesc = treeDescription.getText().toString();
        List<String> treeTagsList = hashtags;
        Double treeLat = treeLocation.latitude;
        Double treeLng = treeLocation.longitude;

        addTreeLogic.uploadPost(image,treeLat, treeLng,treeDesc);
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * Metoda pomoću koje se mapa popunjava, prikazuje se cijela Hrvatska
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        scrollBlocker();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        final LatLng startLocation = new LatLng(44.478513, 16.577829);

        final com.google.android.gms.maps.model.LatLng mapsLatLng =
                new com.google.android.gms.maps.model.LatLng(startLocation.latitude,
                        startLocation.longitude);
        Float zoomLvl = (float)5.5;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapsLatLng,zoomLvl));
        mapsLogic = new MapsLogic(marker, map);
        mapsLogic.refreshMarkerNoLocation();
        marker = mapsLogic.getCurrentMarker();
    }

    /**
     * @return - vraća LatLng - Latitude i Longitude koordinate na kojima je postavljen pin
     */
    private LatLng getPinedLocation (){
        LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        return latLng;
    }

    /**
     * dohvaća se lokacija korisnika, prva lokacija koja se vraća je na 0.0, 0.0. pa ju treba zanemariti
     * Kada se preciznost lokacije smanji ispod 1000 m tada se postavlja marker na trenutnu lokaciju korisnika
     * Ova metoda se izvršava samo jednom i to kod pokretanja ove aktivnosti (to ositugrava field counter)
     * @param location - lokacija korisnika
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location.getAccuracy()<1000 && counter==0 && location!=null && locationPremsOk){
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            mapsLogic.refreshMarkerLive(latitude,longitude);
            counter++;
        }
    }
}