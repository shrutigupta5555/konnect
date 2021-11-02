package com.dbiti.konnect

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.complaint_detail.*
import kotlinx.android.synthetic.main.modal_bottom_sheet.*

private const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //get current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



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
    override fun onMapReady(googleMap: GoogleMap) {
        val task = fusedLocationProviderClient.lastLocation


        //get current location
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                        .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                val latitude = it.latitude
                val longitude = it.longitude
                Log.i(TAG, "Long -> $longitude")
                Toast.makeText(this, "${it.latitude} ${it.longitude}", Toast.LENGTH_LONG).show()
                mMap = googleMap

                // Add a marker in curr loc and move the camera
                val currentLocation = LatLng(latitude,longitude)
//                mMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f))


                //map markers

                val db = Firebase.firestore
                db.collection("users")
                        .get()
                        .addOnSuccessListener { documents ->
                            for(document in documents){

                                val markerLat = document.data.getValue("latitude").toString().toDouble()
                                val markerLong = document.data.getValue("longitude").toString().toDouble()
                                val latlng = LatLng(markerLat, markerLong)
                                mMap.addMarker(MarkerOptions().position(latlng).title("Title"))
                                Log.i(TAG, latlng.toString())
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
                //set onclick on marker
                mMap.setOnInfoWindowClickListener {



                    //query
                    val db = Firebase.firestore


                    //bottom sheet
                    val modalbottomSheetFragment = ModalBottomSheet()
                    modalbottomSheetFragment.show(supportFragmentManager,modalbottomSheetFragment.tag)

                    //curr loc
                    db.collection("users")
                        .whereEqualTo("latitude", it.position.latitude.toString())
                        .whereEqualTo("longitude", it.position.longitude.toString())
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {

//                                val tmpTitle = modalbottomSheetFragment.tvBsCTitle

                                modalbottomSheetFragment.tvBsCTitle.setText(document.data.getValue("ctitle").toString())
                                modalbottomSheetFragment.tvBsCvote.setText(document.data.getValue("votes").toString())
                                modalbottomSheetFragment.tvBsDesc.setText(document.data.getValue("desc").toString())
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }

                }
            }
        }

    }
}