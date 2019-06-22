package com.finie.swiftride;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;

public class Booking extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUESTCODE = 100;
    private static final String TAG ="Maps Activity Report" ;
    private boolean mLocationPermisionGranted;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COUSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private EditText carDetails,DireDetails;
    private Button buttonSubmit;
    private String LongitudePosition = null,LatitudePosition=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        getLocationPermission();
        carDetails = (EditText)findViewById(R.id.carDetails);
        DireDetails = (EditText)findViewById(R.id.DireDetails);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(carDetails.getText().toString() != null && DireDetails.getText().toString() != null){


                   getUserLocation(carDetails.getText().toString(),DireDetails.getText().toString());

                }
            }
        });

    }

    private void getUserLocation(String CabDetails, String Direction) {

        if(LongitudePosition != null && LatitudePosition != null){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);

            HashMap<Object,String> cab = new HashMap<>();
         cab.put("CabDetails",CabDetails);
         cab.put("Direction",Direction);
         cab.put("longitude",LongitudePosition);
         cab.put("latitude",LatitudePosition);
            db.collection("Cabs")
                    .add(cab)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Intent intent = new Intent(Booking.this,MapsActivity.class);
                            startActivity(intent);

                            Toast.makeText(Booking.this, "Details saved", Toast.LENGTH_SHORT).show();

                        }
                    });







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
                  MyLocation();
                }
            }
        }
    }

    private void getLocationPermission(){

        String [] permission = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){


            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COUSE_LOCATION) == PackageManager.PERMISSION_GRANTED){


                mLocationPermisionGranted = true;

              MyLocation();
            }
            else {

                ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUESTCODE);
            }

        }
        else {

            ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUESTCODE);
        }

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

                          LongitudePosition = String.valueOf(CurrentLocation.getLongitude());
                          LatitudePosition = String.valueOf(CurrentLocation.getLatitude());


                        }
                        else{
                            Toast.makeText(Booking.this, "Current Location is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

        }catch(SecurityException e){
            Toast.makeText(this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }



}



