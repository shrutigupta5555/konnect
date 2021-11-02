package com.dbiti.konnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_issue3.*
import kotlinx.android.synthetic.main.activity_main.*

class Issue3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue3)

        var intent = intent

        val addlocation1 = intent.getStringExtra("Location1")
        val title = intent.getStringExtra("Title")
        val desc = findViewById<TextView>(R.id.desc)

        btnnext3.setOnClickListener {
            val de =desc.text.toString()

            val intent = Intent(this@Issue3, Issue4::class.java);

            intent.putExtra("Description",de)
            intent.putExtra("Title1",title)
            intent.putExtra("Location2",addlocation1)
            startActivity(intent)
        }
    }
}