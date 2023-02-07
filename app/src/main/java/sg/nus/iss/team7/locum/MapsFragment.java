package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sg.nus.iss.team7.locum.Model.JobPost;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private String address;
    private JobPost jobPost;

    private static final float DEFAULT_ZOOM = 15f;

    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    public MapsFragment(boolean mLocationPermissionGranted, String address, JobPost jobPost) {
        this.mLocationPermissionGranted = mLocationPermissionGranted;
        this.address = address;
        this.jobPost = jobPost;
    }

//    private OnMapReadyCallback callback = new OnMapReadyCallback() {
//
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        }
//    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
                    if(mLocationPermissionGranted){
//                        getDeviceLocation();
//                        mMap.setMyLocationEnabled(true);
                        geoLocate();

                    }
                }
            });
        }
    }

//    private void getDeviceLocation(){
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        try {
//            if(mLocationPermissionGranted){
//                Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful()){
//                            Location currentLocation = (Location) task.getResult();
//                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM,"You are here");
//                        }else {
//                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//        }catch (Exception e){
//        }
//    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }

    private void geoLocate(){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(address,1);

        }catch (IOException e){

        }
        if(list.size() > 0){
            Address address = list.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//            Toast.makeText(getContext(), "location founded"+address.toString(), Toast.LENGTH_LONG).show();
            moveCamera(latLng,DEFAULT_ZOOM,jobPost.getClinic().getName());
        }

    }

}