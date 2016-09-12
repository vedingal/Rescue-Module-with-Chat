package com.venndingal.mapsactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = "MapsActivity", str_message;
    private EditText et_pickUp, et_dropOff, et_notes, et_FullName;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String buttonClicked;
    private Button btn_submit;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String myPref, nameFromPref;
    private Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // for hiding title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        c = getApplicationContext();

        et_FullName = (EditText) findViewById(R.id.et_FullName);
        et_pickUp = (EditText) findViewById(R.id.et_pickUp);
        et_dropOff = (EditText) findViewById(R.id.et_dropOff);
        et_notes = (EditText) findViewById(R.id.et_notes);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        myPref = "com.venndingal.mapsactivity";
        sp = c.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.contains("fullName")){
            nameFromPref = sp.getString("fullName",null);
            et_FullName.setText(nameFromPref);
        }else{

        }


        et_pickUp.setOnClickListener(doThisOnClick);
        et_dropOff.setOnClickListener(doThisOnClick);
        btn_submit.setOnClickListener(doThisOnClick);
    }

    private View.OnClickListener doThisOnClick = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.et_pickUp:
                    try {
                        buttonClicked = "pickUp";
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(MapsActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                    break;
                case R.id.et_dropOff:
                    try {
                        buttonClicked = "dropOff";
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(MapsActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                    break;
                case R.id.btn_submit:
                    String str_fullName = et_FullName.getText().toString();
                    String str_pickUp = et_pickUp.getText().toString();
                    String str_dropOff = et_dropOff.getText().toString();
                    String str_note = et_notes.getText().toString();

                    if (!str_fullName.equals("")
                            && !str_pickUp.equals("")
                            && !str_dropOff.equals("")
                            && !str_note.equals("")){
                        String userName = str_fullName;
                        editor.putString("fullName",userName);
                        editor.commit();

                        str_message = str_pickUp + "#%" + str_dropOff + "#%" + str_note;
                        Log.v(TAG, "str_message >> " + str_message);

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(userName,"");
                        root.updateChildren(map);

                        Intent in = new Intent(getApplicationContext(), Chat_Room.class);
                        in.putExtra("roomName", userName);
                        in.putExtra("msg", str_message);
                        in.putExtra("userName", userName);
                        startActivity(in);
                    }else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivity.this, "Please complete all fields.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;
                default:
                    break;        }
        }

    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();

    }

    private void getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        Log.v(TAG, "lat >> " + myLocation.getLatitude());
        Log.v(TAG, "lng >> " + myLocation.getLongitude());

        LatLng sydney = new LatLng(myLocation.getLatitude(),  myLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("You're here."));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Address: " + place.getAddress());
                if (buttonClicked.equals("pickUp")){
                    et_pickUp.setText(place.getName() + ", " +place.getAddress());
                }else{
                    et_dropOff.setText(place.getName() + ", " +place.getAddress());
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
