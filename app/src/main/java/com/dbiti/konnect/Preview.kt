package com.dbiti.konnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_preview.*

private const val TAG = "Preview"
class Preview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val auth = Firebase.auth
        val db = Firebase.firestore


        var intent = intent
        val addlocation4 = intent.getStringExtra("Location4")
        val addlocation5 = findViewById<TextView>(R.id.addlocation5)
        addlocation5.text = addlocation4

        val addtitle3 = intent.getStringExtra("Title3")
        val addtitle4 = findViewById<TextView>(R.id.addtitle4)
        addtitle4.text = addtitle3

        val adddesc2 = intent.getStringExtra("Description2")
        val adddesc3 = findViewById<TextView>(R.id.adddesc)
        adddesc3.text = adddesc2

        val addinfo = intent.getStringExtra("AdditionalInfo")
        val addaddinfo = findViewById<TextView>(R.id.addaddinfo)
        addaddinfo.text = addinfo

        Log.i(TAG, addlocation4?.split(" ").toString())
        val address = addlocation4?.split(" ")
        val lt = address?.get(9)
        val lng = address?.get(6)
        val city = address?.get(12)
        val country = address?.get(14)
        Log.i(TAG, "lat -> $lt long -> $lng  city -> $city country -> $country")
        Submit.setOnClickListener {
            val dataToUpload = hashMapOf(
                    "email" to auth.currentUser.email,
                    "ctitle" to addtitle3,
                    "desc" to adddesc2,
                    "latitude" to lt,
                    "longitude" to lng,
                    "city" to city,
                    "country" to country,
                    "votes" to 0,
                    "cprogress" to 0
            )

            db.collection("users").add(dataToUpload)
            val intent = Intent(this@Preview, MainActivity::class.java)
            startActivity(intent)
        }
    }

}