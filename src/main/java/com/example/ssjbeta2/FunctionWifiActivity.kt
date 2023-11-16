package com.example.ssjbeta2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import jp.wasabeef.blurry.Blurry
import android.view.View
import androidx.core.content.ContextCompat
import android.os.Handler
import android.os.Looper


class FunctionWifiActivity : AppCompatActivity() {
    private var isMoveUpButtonPressed = false
    private var isMoveDownButtonPressed = false
    private var isWifiConnected = false

    companion object {
        private const val ESP8266_IP_ADDRESS = "http://YOUR_ESP8266_IP_ADDRESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_function)

        val moveUpButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.wifiMoveUpButton)
        val moveDownButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.wifiMoveDownButton)
        val errorMessage = findViewById<TextView>(R.id.wifiErrorMessage)

        // Initialize the "Move Up" button as green and "Move Down" button as red
        moveUpButton.setBackgroundResource(R.drawable.green_button)
        moveDownButton.setBackgroundResource(R.drawable.red_button)

        moveUpButton.setOnClickListener {
            if (!isMoveUpButtonPressed) {
                moveUpButton.setBackgroundResource(R.drawable.gray_button)
                moveDownButton.setBackgroundResource(R.drawable.red_button)
                isMoveUpButtonPressed = true
                isMoveDownButtonPressed = false
            }
            errorMessage.visibility = View.GONE // Hide error message
            sendWifiCommand("1")
            checkWifiConnection()
        }

        moveDownButton.setOnClickListener {
            if (!isMoveDownButtonPressed) {
                moveDownButton.setBackgroundResource(R.drawable.gray_button)
                moveUpButton.setBackgroundResource(R.drawable.green_button)
                isMoveDownButtonPressed = true
                isMoveUpButtonPressed = false
            }
            errorMessage.visibility = View.GONE // Hide error message
            sendWifiCommand("0")
            checkWifiConnection()
        }

        checkWifiConnection() // Initialize the connection status
    }

    private fun sendWifiCommand(command: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$ESP8266_IP_ADDRESS/command?cmd=$command")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@FunctionWifiActivity, "Failed to send command.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                this@FunctionWifiActivity.runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FunctionWifiActivity, "Command sent successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@FunctionWifiActivity, "Failed to send command.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun checkWifiConnection() {
        val connectionStatusTextView = findViewById<TextView>(R.id.connectionStatus)
        val errorMessage = findViewById<TextView>(R.id.wifiErrorMessage)

        // For simplicity, we're just checking if the last command was successful.
        // In a real-world scenario, you might want to ping the ESP8266 to check the connection.
        if (!isWifiConnected) {
            errorMessage.visibility = View.VISIBLE
            connectionStatusTextView.text = getString(R.string.status_not_connected)
            connectionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.grayColor))
        } else {
            errorMessage.visibility = View.GONE
            connectionStatusTextView.text = getString(R.string.status_connected)
            connectionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.grayColor))
        }
    }
}









