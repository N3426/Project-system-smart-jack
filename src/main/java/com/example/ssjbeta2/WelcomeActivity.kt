package com.example.ssjbeta2

import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val nextWifiButton = findViewById<Button>(R.id.nextWifiButton)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val arduinoExplanationButton = findViewById<Button>(R.id.arduinoExplanationButton)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionTextView = findViewById<TextView>(R.id.versionTextView)
        versionTextView.text = "Version: $versionName"

        arduinoExplanationButton.setBackgroundResource(R.drawable.blue_button)

        arduinoExplanationButton.setOnClickListener {
            val intent = Intent(this, ArduinoExplanationActivity::class.java)
            startActivity(intent)
        }

        nextButton.setOnClickListener {
            val intent = Intent(this, FunctionActivity::class.java)
            startActivity(intent)
        }

        nextWifiButton.setOnClickListener {
            val intent = Intent(this, FunctionWifiActivity::class.java)
            startActivity(intent)
        }
    }
}

