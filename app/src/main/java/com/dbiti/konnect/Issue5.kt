package com.dbiti.konnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_issue4.*
import kotlinx.android.synthetic.main.activity_issue5.*
import kotlinx.android.synthetic.main.activity_report_issue1.*

class Issue5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue5)

        /*val imageView: ImageView = findViewById(R.id.image_view1)
        val bundle: Bundle = intent.extras!!
        val resId: Int = bundle.getInt("resId")
        image_view.setImageResource(resId)*/

        var intent = intent

        val addlocation3 = intent.getStringExtra("Location3")
        val addtitle2 = intent.getStringExtra("Title2")
        val adddesc1 = intent.getStringExtra("Description1")
        val addinfo = findViewById<TextView>(R.id.addinfo)

        btnnext5.setOnClickListener {
            val ad =addinfo.text.toString()
            val intent = Intent(this@Issue5, Preview::class.java);
            Log.i("Issue5", "pressed")
            intent.putExtra("AdditionalInfo",ad)
            intent.putExtra("Description2", adddesc1)
            intent.putExtra("Title3",addtitle2)
            intent.putExtra("Location4",addlocation3)
            startActivity(intent)
        }
    }
}