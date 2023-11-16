package com.example.ssjbeta2


import java.io.IOException
import ArduinoProject
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ArduinoExplanationActivity : AppCompatActivity() {

    private fun readArduinoCodeFromAssets(): String {
        return try {
            val inputStream = assets.open("arduino_code.txt")
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "Error reading Arduino code."
        }
    }

    private fun readExplanationsCodeFromAssets(): String {
        return try {
            val inputStream = assets.open("explanations_code.txt")
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "Error reading explanations code."
        }
    }

    private fun readAboutProjectFromAssets(): String {
        return try {
            val inputStream = assets.open("about_project.txt")
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "Error reading explanations code."
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduino_explanation)


        // Sample data for the RecyclerView
        val explanations = listOf(
            ArduinoProject(title = "About Project", description = readAboutProjectFromAssets(), imageResourceIds = listOf(R.drawable.default_image)),
            ArduinoProject(title = "Arduino Diagram", description = "Description for Arduino Diagram", imageResourceIds = listOf(R.drawable.diagram1, R.drawable.diagram2), aboutImages = listOf("Picture Diagram Arduino Wiring By Using Tinkercad","Picture Actual Taken By using Tinkercad"),  websiteUrl = "https://www.tinkercad.com/things/ex93zA6NVLh?sharecode=9IpTCObSALe4Q-m6F2g5no4EwCHkgrJz7B9ySNruvnc", linkText = "Go to TinkerCad"),
            ArduinoProject(title = "Code Arduino", description = readArduinoCodeFromAssets(), codeExplanation = readExplanationsCodeFromAssets(), imageResourceIds = listOf(R.drawable.image_code1, R.drawable.image_code2), aboutImages = listOf("","Picture Coding Taken From Arduino IDE"), websiteUrl = "https://github.com/N3426/Project-system-smart-jack/blob/acee3da4f23aa01d0d7b3db9e7e6e8eab213fd52/wrk.ino#L19", linkText = "Go to GitHub to view code"),
            ArduinoProject(title = "Other information", description = "Picture, receipt, coding, app can be found in the Google Drive ", websiteUrl = "https://drive.google.com/drive/folders/1q55bS55VyEmytErxx6g9oeYGWdiABXGT?usp=sharing", linkText = "Google Drive")
            // delete this --> [ ),// ]
        )

        // 1. Initialize the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.arduinoRecyclerView)

        // 2. Set the LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Create and Set the Adapter
        val adapter = ArduinoAdapter(explanations)  // Assuming you've defined an adapter named ArduinoAdapter
        recyclerView.adapter = adapter
    }
}

