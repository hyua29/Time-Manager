package com.app.cooper.time_manager.custom.views.pickers;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.app.cooper.time_manager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

/**
 * Reference: https://developers.google.com/maps/documentation/android-sdk/start?hl=en_US
 * A fragment where user can pick event location
 */
public class LocationPicker extends AppCompatDialogFragment implements OnMapReadyCallback {

    private  SupportMapFragment supportMapFragment;
    private OnLocationSelectListener callback;
    private Button button;
    private Marker marker;
    private final float ZOOM_LEVEL = 16.0f;
    private Activity activity;

    /**
     * load current location and move the camera after the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        this.moveCamera(googleMap, fusedLocationProviderClient);
        this.showMarkerByClicking(googleMap);
    }

    /**
     * display a marker on the spot clicked by the user
     * @param googleMap
     */
    private void showMarkerByClicking(final GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {

                if (marker != null) {
                    marker.remove();
                }

                marker = googleMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                System.out.println(latlng);

            }
        });
    }

    /**
     * move camera to current location if having permission
     * @param googleMap
     * @param fusedLocationProviderClient
     */
    private void moveCamera(final GoogleMap googleMap, FusedLocationProviderClient fusedLocationProviderClient) {
        //
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            System.out.println("---------------------------");
                            System.out.println(location.getLatitude());
                            System.out.println(location.getAltitude());
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, ZOOM_LEVEL));
                            marker = googleMap.addMarker(new MarkerOptions().position(current).title("current"));
                            System.out.println("---------------------------");
                        }
                    }
                });
    }

    /**
     * callback function
     */
    public interface OnLocationSelectListener {
        void onLocationSelect(String location);
    }

    /**
     * create the map select dialog
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.map_picker, null);


        if (Build.VERSION.SDK_INT < 21) {
            supportMapFragment = (SupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        }
        supportMapFragment.getMapAsync(this);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        // return the location after the button is pressed
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                button = dialogView.findViewById(R.id.saveLocation);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        returnResult();
                    }
                });

            }
        });

        return alertDialog;

    }

    /**
     * run callback function to return location at the marker to parent activity and dismiss the dialog
     */
    private void returnResult() {
        callback.onLocationSelect(getAddress(marker.getPosition().latitude, marker.getPosition().longitude));
        dismiss();
    }

    /**
     * attach dialog to parent activity
     * @param activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity  = activity;
        try
        {
            callback = (OnLocationSelectListener) activity;
        }
        catch (ClassCastException e)
        {
            Log.d("MyDialog", "Activity doesn't implement the interface");
        }
    }

    /**
     * get the detailed address by latitude and longitude given
     * @param lat
     * @param lng
     * @return
     */
    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
        String result = "Cannot recognize location";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() == 0)
                return result;

                System.out.println(addresses);
                Address obj = addresses.get(0);
                result = obj.getAddressLine(0);
            result = result + "\n" + obj.getCountryName();
            result = result + "\n" + obj.getCountryCode();
            result = result + "\n" + obj.getAdminArea();
            result = result + "\n" + obj.getPostalCode();
            result = result + "\n" + obj.getSubAdminArea();
            result = result + "\n" + obj.getLocality();
            result = result + "\n" + obj.getSubThoroughfare();

                Log.v("IGA", "Address" + result);

            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * remove supportMapFragment after the dialog has been dismissed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println(supportMapFragment);
        if (supportMapFragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(supportMapFragment).commit();
        }
    }

}
