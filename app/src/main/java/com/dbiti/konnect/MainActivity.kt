package com.dbiti.konnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.complaint_home.*

class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

class MainActivity : AppCompatActivity() {
    private companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var auth: FirebaseAuth

    //cloud firestore instance
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize auth
        auth = Firebase.auth
        Toast.makeText(this, "${auth.currentUser.email}", Toast.LENGTH_LONG).show()



        //query data
        val query = db.collection("users").whereEqualTo("email", auth.currentUser.email)

        val options = FirestoreRecyclerOptions.Builder<ComplaintsModel>().setQuery(query, ComplaintsModel::class.java)
            .setLifecycleOwner(this).build()

        //recyclerview
        //1. set layout manager


        //2. set adapter on rv
        val adapter = object:FirestoreRecyclerAdapter<ComplaintsModel, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

                val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.complaint_home, parent, false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: UserViewHolder,
                position: Int,
                model: ComplaintsModel
            ) {
                Log.i(TAG, "${model.cprogress} progress bar")
                val tvCTitle = holder.itemView.findViewById<TextView>(R.id.etComplaintTitle)

                val tvCVotes = holder.itemView.findViewById<TextView>(R.id.etVote)
                val pgBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
                tvCTitle.text = model.ctitle
                val t = tvCTitle.text.toString()
                tvCVotes.text = model.votes.toString()
                pgBar?.max = 5
                pgBar?.progress = model.cprogress

                holder.itemView.setOnClickListener {
//                   Toast.makeText(this@MainActivity, "$t",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, ComplaintDetail::class.java)
                    intent.putExtra("complaintTitle", t)
                    startActivity(intent)
                }
            }

        }

        rvComplaint.adapter = adapter
        rvComplaint.layoutManager = LinearLayoutManager(this)


        //nav at bottom
        setupNavigation()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miLogOut){
            Log.i(TAG, "Logout")

            //logout user
            auth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.BNMhome -> {
                    Log.i(TAG, "Clicked home")
                    Toast.makeText(this, "Home selected", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.BNMlocation -> {
                    val intentMap = Intent(this, MapsActivity::class.java)
                    startActivity(intentMap)
                    Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.BNMadd -> {
                    val intentAdd = Intent(this@MainActivity, ReportIssue1::class.java)
                    startActivity(intentAdd)
                    Toast.makeText(this, "Add selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> true
            }
        }
    }


       
   }
