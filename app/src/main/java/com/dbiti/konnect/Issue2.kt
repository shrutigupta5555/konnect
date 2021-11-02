package com.dbiti.konnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_issue2.*
import kotlinx.android.synthetic.main.activity_main.*

class Issue2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue2)

        var intent = intent

        val Locationtxt = intent.getStringExtra("Location")
        val title = findViewById<EditText>(R.id.title)

        btnnext2.setOnClickListener {
            val ti =title.text.toString()
            val intent = Intent(this@Issue2, Issue3::class.java);
            intent.putExtra("Title", ti)
            intent.putExtra("Location1",Locationtxt)
            startActivity(intent)
        }
    }
}