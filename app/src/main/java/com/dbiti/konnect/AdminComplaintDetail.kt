package com.dbiti.konnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.admin_complain_detail.*
import kotlinx.android.synthetic.main.complaint_detail.*
import kotlinx.android.synthetic.main.complaint_detail.cdProgressBar
import kotlinx.android.synthetic.main.complaint_detail.tvCdCTitle
import kotlinx.android.synthetic.main.complaint_detail.tvCdCvote
import kotlinx.android.synthetic.main.complaint_detail.tvCdDesc
import kotlinx.android.synthetic.main.complaint_home.*

private const val TAG = "ComplaintDetail"
class AdminComplaintDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_complain_detail)

        val title = intent.getStringExtra("complaintTitle").toString()


        //query
        val db = Firebase.firestore
        db.collection("users")
                .whereEqualTo("ctitle", title)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        val cid = document.id
                        tvCdCTitle.setText(document.data.getValue("ctitle").toString())
                        tvCdCvote.setText(document.data.getValue("votes").toString())
                        tvCdDesc.setText(document.data.getValue("desc").toString())
                        cdProgressBar.max = 5
                        val status = document.data.getValue("cprogress").toString().toInt()

                        cdProgressBar.progress = status
                        Log.i("ComplaintDetailgedocs", "${document.id} => ${status}")

                        progressButton.setOnClickListener{
                            if(status > 4){

                            }
                            else{
                                db.collection("users").document(cid).update("cprogress", FieldValue.increment(1) )
                                cdProgressBar.progress = status+1

                            }

                        }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }



    }
}