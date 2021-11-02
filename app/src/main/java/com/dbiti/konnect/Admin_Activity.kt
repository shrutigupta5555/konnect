package com.dbiti.konnect

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_report_issue1.*
import java.util.*

private const val TAG = "Admin_Activity"


class AdminUserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
class Admin_Activity : AppCompatActivity() {


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_)
        auth = Firebase.auth

        Toast.makeText(this, "${auth.currentUser.email}", Toast.LENGTH_LONG).show()



        //query data
//        val city = "Kalyan"
//        val db = Firebase.firestore
//        val query = db.collection("users").whereEqualTo("city", city)
//
//        val options = FirestoreRecyclerOptions.Builder<ComplaintsModel>().setQuery(query, ComplaintsModel::class.java)
//                .setLifecycleOwner(this).build()
//
//        //recyclerview
//        //1. set layout manager
//
//
//        //2. set adapter on rv
//        val adapter = object: FirestoreRecyclerAdapter<ComplaintsModel, AdminUserViewHolder>(options){
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
//
//                val view = LayoutInflater.from(this@Admin_Activity).inflate(R.layout.complaint_home, parent, false)
//                return AdminUserViewHolder(view)
//            }
//
//            override fun onBindViewHolder(
//                    holder: AdminUserViewHolder,
//                    position: Int,
//                    model: ComplaintsModel
//            ) {
////                Log.i(MainActivity.TAG, "${model.cprogress} progress bar")
//                val tvCTitle = holder.itemView.findViewById<TextView>(R.id.etComplaintTitle)
//
//                val tvCVotes = holder.itemView.findViewById<TextView>(R.id.etVote)
//                val pgBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
//                tvCTitle.text = model.ctitle
//                val t = tvCTitle.text.toString()
//                tvCVotes.text = model.votes.toString()
//                pgBar?.max = 5
//                pgBar?.progress = model.cprogress
//
//                holder.itemView.setOnClickListener {
//                    Log.i(TAG, "$it")
////                   Toast.makeText(this@Admin_Activity, "$it",Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@Admin_Activity, AdminComplaintDetail::class.java)
//                    intent.putExtra("complaintTitle", t)
//                    startActivity(intent)
//                }
//            }
//
//
//
//        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            Log.d("Debug:", CheckPermission().toString())
            Log.d("Debug:", isLocationEnabled().toString())
            RequestPermission()
            getLastLocation()

//
//        rvAdminComplaint.adapter = adapter
//        rvAdminComplaint.layoutManager = LinearLayoutManager(this)

        //navigation
        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miLogOut) {
            Log.i(TAG, "Logout")

            //logout user
            auth.signOut()
            val logoutIntentAdmin = Intent(this, LoginActivity::class.java)
            logoutIntentAdmin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntentAdmin)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.admin_nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Adminhome -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.Adminlocation -> {
                    val intentMap = Intent(this@Admin_Activity, MapsActivity::class.java)
                    startActivity(intentMap)
                    Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> true
            }
        }
    }

    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    val location: Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
//                        Log.i(TAG, getCityName(location.latitude,location.longitude))
                        tvAdminCity.text = getCityName(location.latitude,location.longitude)
                        val city = getCityName(location.latitude,location.longitude)

                        val db = Firebase.firestore
                        val query = db.collection("users").whereEqualTo("city", city)

                        val options = FirestoreRecyclerOptions.Builder<ComplaintsModel>().setQuery(query, ComplaintsModel::class.java)
                                .setLifecycleOwner(this).build()

                        //recyclerview
                        //1. set layout manager


                        //2. set adapter on rv
                        val adapter = object: FirestoreRecyclerAdapter<ComplaintsModel, AdminUserViewHolder>(options){
                            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {

                                val view = LayoutInflater.from(this@Admin_Activity).inflate(R.layout.complaint_home, parent, false)
                                return AdminUserViewHolder(view)
                            }

                            override fun onBindViewHolder(
                                    holder: AdminUserViewHolder,
                                    position: Int,
                                    model: ComplaintsModel
                            ) {
//                Log.i(MainActivity.TAG, "${model.cprogress} progress bar")
                                val tvCTitle = holder.itemView.findViewById<TextView>(R.id.etComplaintTitle)

                                val tvCVotes = holder.itemView.findViewById<TextView>(R.id.etVote)
                                val pgBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
                                tvCTitle.text = model.ctitle
                                val t = tvCTitle.text.toString()
                                tvCVotes.text = model.votes.toString()
                                pgBar?.max = 5
                                pgBar?.progress = model.cprogress

                                holder.itemView.setOnClickListener {
                                    Log.i(TAG, "$it")
//                   Toast.makeText(this@Admin_Activity, "$it",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@Admin_Activity, AdminComplaintDetail::class.java)
                                    intent.putExtra("complaintTitle", t)
                                    startActivity(intent)
                                }
                            }



                        }


                        rvAdminComplaint.adapter = adapter
                        rvAdminComplaint.layoutManager = LinearLayoutManager(this)



                        //Locationtxt.text = "You Current Location is : Long: "+ location.longitude + " , Lat: " + location.latitude +  getCityName(location.latitude,location.longitude)
//                        Locationtxt.text = "You Current Location is : Long: "+ location.longitude + " , Lat: " + location.latitude + " Your City: " + getCityName(location.latitude,location.longitude) + " Your Country: "+ getCountryName(location.latitude,location.longitude)
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }


    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
            tvAdminCity.text = getCityName(lastLocation.latitude, lastLocation.longitude)
            Log.i(TAG,  getCityName(lastLocation.latitude, lastLocation.longitude))
            //Locationtxt.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
//            Locationtxt.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\nYour City: " + getCityName(lastLocation.latitude,lastLocation.longitude) + "\nYour Country: "+ getCountryName(lastLocation.latitude,lastLocation.longitude)
        }
    }

    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }

    /*private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }*/

    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,1)

        cityName = Adress.get(0).locality
//        tvAdminCity.text = cityName
                return cityName
    }

    private fun getCountryName(lat: Double,long: Double):String{
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,1)

        countryName = Adress.get(0).countryName
        return countryName
    }



}

