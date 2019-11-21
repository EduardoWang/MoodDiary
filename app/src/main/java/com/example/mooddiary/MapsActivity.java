package com.example.mooddiary;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mooddiary.ui.home.HomeViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an activity that shows a map which mood event marked
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<MoodEvent> myMoods  = new ArrayList<>();
    private HashMap<String,MoodEvent> friendMoods = new HashMap<>();
    private ArrayList<MoodEvent> myMapMoods = new ArrayList<>();
    private HashMap<String,MoodEvent> friendMapMoods = new HashMap<>();
    String map = null;
    FirebaseFirestore db;
  
    /**
     * This creates the view of map
     * @param savedInstanceState
     *      If the activity is being re-initialized after previously being shut down
     *      then this Bundle contains the data it most recently supplied in.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        this.map = intent.getStringExtra("map");
        this.myMoods = (ArrayList<MoodEvent>) intent.getSerializableExtra("moodlist");
        this.friendMoods = (HashMap<String,MoodEvent>)intent.getSerializableExtra("friendsEvents");

    }

    /**
     * This deals with back button clicking
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng newYork = new LatLng(60, -100);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newYork));

        if (map.equals("mymap")){
            getMyMapMoods(myMoods);
            setMyMapMarker();
        }
        else if(map.equals("friendmap")){
            getFriendMapMoods(friendMoods);
            setFriendMapMarker();
        }
    }

    /**
     * Getting the latitude and longitude of a location string by geocoder
     * May throw IOException if the locationName does not exist
     * @param context
     *      This activity
     * @param locationName
     *      This a location
     * @return
     *       Returns the LatLng of location
     */
    public LatLng getLocationLatLng(Context context, String locationName) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng l1 = null;

        try {
            address = coder.getFromLocationName(locationName, 5);
            if (address == null) {
                return null;
            }
            if (!(address.isEmpty())){
                Address location = address.get(0);
                l1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
            else{
                Toast.makeText(MapsActivity.this, locationName+" is not a valid address, please " +
                        "enter the correct address", Toast.LENGTH_SHORT).show();
                return null;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return l1;
    }

    /**
     * get a list of mood events with a string of location of the user
     * @param myMoodList
     *        This is a moodlist of user mood
     */
    public void getMyMapMoods(ArrayList<MoodEvent> myMoodList){
        for (int i = 0; i< myMoodList.size();i++){
            if (myMoodList.get(i).getLocation()!=null)
                myMapMoods.add(myMoodList.get(i));
        }
    }

    /**
     * get a list of mood events with a string of location of the user's friends
     * @param friendMoodList
     *        This is the moodlist of user's friends
     */
    public void getFriendMapMoods(HashMap<String,MoodEvent> friendMoodList){
        for(Map.Entry<String,MoodEvent> entry:friendMoodList.entrySet()){
            String key = entry.getKey();
            MoodEvent moodEvent = entry.getValue();
            if (!moodEvent.getLocation().equals("")){
                friendMapMoods.put(key,moodEvent);
            }
        }
    }

    /**
     * set the location point markers of the user on the map
     */
    public void setMyMapMarker(){
        for (MoodEvent moodEvent:myMapMoods) {
            String locationName = moodEvent.getLocation();
            LatLng markPoint = getLocationLatLng(getApplicationContext(), locationName);
            if(markPoint!=null){
                mMap.addMarker(new MarkerOptions().position(markPoint).title(moodEvent.getMood().getMood()).icon(
                        BitmapDescriptorFactory.fromResource(moodEvent.getMood().getMarker())));
            }
        }
    }

    /**
     * set the location points markers of the user's friends locations
     */
    public void setFriendMapMarker(){
        for (Map.Entry<String,MoodEvent> entry:friendMapMoods.entrySet()){
            String name = entry.getKey();
            MoodEvent moodEvent = entry.getValue();
            String locationName = moodEvent.getLocation();
            LatLng markPoint = getLocationLatLng(getApplicationContext(), locationName);
            if (markPoint != null) {
                mMap.addMarker(new MarkerOptions().position(markPoint).title(name).icon(
                        BitmapDescriptorFactory.fromResource(moodEvent.getMood().getMarker())));
            }
        }
    }
}

