package com.finie.swiftride;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUESTCODE = 100;
    private static final String TAG ="Maps Activity Report" ;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 101;
    private boolean mLocationPermisionGranted;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COUSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private EditText Search;
    private ImageButton Update;
    private GoogleMap mGoogleMap;
    private  String LatitudePosition,LongitudePosition,CabDetails,Direction;
    private List<CabModel> cabModelList;
    private ClusterManager mClusterManger;
    private ClusterManagerRender mClusterManagerRender;
    private ArrayList<MarkerCluster> mClusterMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        Update = (ImageButton)findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,Booking.class);
                startActivity(intent);
            }
        });
        Search = (EditText)findViewById(R.id.Search);

        getLocationPermission();

    }

private void Initialize(){
    Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionRef, KeyEvent keyEvent) {

            if(actionRef == EditorInfo.IME_ACTION_SEARCH || actionRef== EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER ){


                GetLocations();
            }
return false;
        }
    });

}

    private void GetLocations() {

      String Locator = Search.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);

        List<Address> addressList = new ArrayList<>();

        try{

            addressList = geocoder.getFromLocationName(Locator,1);

            Log.d(TAG, "GetLocations: Found the location \n"+addressList.toString());

        }catch(IOException  e){

            Toast.makeText(this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(addressList.size() > 0 ){

            Address address = addressList.get(0);

            moveCameras(new LatLng(address.getLatitude(),address.getLongitude()),15f,"Destination: "+address.getLatitude()+" longitude "+address.getLongitude());
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mLocationPermisionGranted) {
            MyLocation();

            Initialize();

            double lat = 0.6773;
            double lon = 34.7796;

            LatLng location = new LatLng(lat,lon);

            geoLocator(location);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mMap.setMyLocationEnabled(true);
}

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        mLocationPermisionGranted = false;

        switch (requestCode){

            case LOCATION_PERMISSION_REQUESTCODE: {

                if(grantResults.length > 0){

                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermisionGranted = false;
                        }

                    }
                    
                    
                    mLocationPermisionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void MyLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{


   if(mLocationPermisionGranted){
       final Task locationTask = fusedLocationProviderClient.getLastLocation();
       locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
           @Override
           public void onComplete(@NonNull Task<Location> task) {
               if(task.isSuccessful()){

                   Location CurrentLocation =(Location)task.getResult();
                   moveCameras(new LatLng(CurrentLocation.getLatitude(),CurrentLocation.getLongitude()),15f,"You");

               }
               else{
                   Toast.makeText(MapsActivity.this, "Current Location is null", Toast.LENGTH_SHORT).show();
               }
           }
       });


   }

 }catch(SecurityException e){
     Toast.makeText(this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
 }

    }

    private void moveCameras(LatLng latLng, float zoom,String title) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));


        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
             mMap.addMarker(markerOptions);

    }


   private void getLocationPermission(){

        String [] permission = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

     if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){


         if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COUSE_LOCATION) == PackageManager.PERMISSION_GRANTED){


             mLocationPermisionGranted = true;

             initMap();
         }
         else {

             ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUESTCODE);
         }

       }
       else {

           ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUESTCODE);
     }

   }

   private void geoLocator(LatLng location){

       Geocoder geocoder = new Geocoder(MapsActivity.this);

       List<Address> addressList = new ArrayList<>();

       try{

           addressList = geocoder.getFromLocation(location.latitude,location.longitude,1);

       }catch(IOException  e){

           Toast.makeText(this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
       }

if(addressList.size() > 0 ){

    Address address = addressList.get(0);

     moveCameras(new LatLng(address.getLatitude(),address.getLongitude()),15f,"Cab at "+address.getAddressLine(0));
}



   }

private void FetchsCabs(){


    cabModelList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
    db.setFirestoreSettings(settings);

    db.collection("Cabs").addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

            if(e != null){

                Log.d(TAG ,"Error "+e.getMessage());
            }

            for(DocumentChange documentChange : documentSnapshots.getDocumentChanges()){

                if(documentChange.getType() == DocumentChange.Type.ADDED){

                    CabModel story = documentChange.getDocument().toObject(CabModel.class);

                    LatitudePosition = story.getLatitude();
                    LatitudePosition = story.getLongitude();
                    CabDetails = story.getCabDetails();
                    Direction = story.getDirection();

                }
            }//end of for


        }
    });


}

   public void addMarker(){
        if(mGoogleMap != null){

            if(mClusterManger == null){
                mClusterManger = new ClusterManager<MarkerCluster>(getApplicationContext(),mGoogleMap);
            }
if(mClusterManagerRender == null){

    mClusterManagerRender = new ClusterManagerRender(getApplicationContext(),mGoogleMap,mClusterManger);
}


       }
   }
}
