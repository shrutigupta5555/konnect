package com.dbiti.konnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.complaint_detail.*
import kotlinx.android.synthetic.main.complaint_home.*

private const val TAG = "ComplaintDetail"
class ComplaintDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complaint_detail)

        val title = intent.getStringExtra("complaintTitle").toString()


        //query
        val db = Firebase.firestore
        db.collection("users")
            .whereEqualTo("ctitle", title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    tvCdCTitle.setText(document.data.getValue("ctitle").toString())
                    tvCdCvote.setText(document.data.getValue("votes").toString())
                    tvCdDesc.setText(document.data.getValue("desc").toString())
                    cdProgressBar.max = 5
                    val status = document.data.getValue("cprogress").toString().toInt()

                    cdProgressBar.progress = status
                    Log.i("ComplaintDetailgedocs", "${document.id} => ${status}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }
}