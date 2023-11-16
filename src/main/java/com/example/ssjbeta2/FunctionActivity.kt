package com.example.ssjbeta2

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
//import android.bluetooth.BluetoothDevice
//import java.util.UUID
//import java.io.IOException
//import android.bluetooth.BluetoothAdapter
//import android.content.pm.PackageManager






@Suppress("DEPRECATION")
class FunctionActivity : AppCompatActivity() {
    private var isMoveUpButtonPressed = false
    private var isMoveDownButtonPressed = false
    private var isBluetoothConnected = false
    private var bluetoothSocket: BluetoothSocket? = null
    //private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()



    companion object {
        //private const val REQUEST_LOCATION_PERMISSION = 2
        private const val REQUEST_BLUETOOTH_ACTIVITY = 1001
        //private const val HC05_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_function)

        val moveUpButton = findViewById<Button>(R.id.moveUpButton)
        val moveDownButton = findViewById<Button>(R.id.moveDownButton)
        val bluetoothListButton = findViewById<Button>(R.id.bluetoothListButton)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)




        // Initialize the "Move Up" button as green and "Move Down" button as red
        moveUpButton.setBackgroundResource(R.drawable.green_button)
        moveDownButton.setBackgroundResource(R.drawable.red_button)

        moveUpButton.isEnabled = true
        moveDownButton.isEnabled = true


        moveUpButton.setOnClickListener {
            // Change button backgrounds
            moveUpButton.setBackgroundResource(R.drawable.gray_button)
            moveDownButton.setBackgroundResource(R.drawable.red_button)

            // Update button pressed states
            isMoveUpButtonPressed = true
            isMoveDownButtonPressed = false

            // Enable the other button
            moveDownButton.isEnabled = true
            // Disable the current button
            moveUpButton.isEnabled = false

            errorMessage.visibility = View.GONE // Hide error message
            sendBluetoothCommand("1")
            checkBluetoothConnection()
        }

        moveDownButton.setOnClickListener {
            // Change button backgrounds
            moveDownButton.setBackgroundResource(R.drawable.gray_button)
            moveUpButton.setBackgroundResource(R.drawable.green_button)

            // Update button pressed states
            isMoveDownButtonPressed = true
            isMoveUpButtonPressed = false

            // Enable the other button
            moveUpButton.isEnabled = true
            // Disable the current button
            moveDownButton.isEnabled = false

            errorMessage.visibility = View.GONE // Hide error message
            sendBluetoothCommand("0")
            checkBluetoothConnection()
        }

        // Add a click listener for the Bluetooth List Button
        bluetoothListButton.setOnClickListener {
            val intent = Intent(this, BluetoothListActivity::class.java)
            startActivityForResult(intent, REQUEST_BLUETOOTH_ACTIVITY)
        }

        checkBluetoothConnection() // Add this line to initialize the connection status
    }

    private fun sendBluetoothCommand(command: String) {
        val socket = BluetoothService.getBluetoothSocket()
        if (socket != null && socket.isConnected) {
            try {
                val outputStream = socket.outputStream
                outputStream.write(command.toByteArray())
                Toast.makeText(this, "Command sent successfully.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send Bluetooth command.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to send command. Device not connected.", Toast.LENGTH_SHORT).show()
        }
    }


    @Suppress("ControlFlowWithEmptyBody")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH_ACTIVITY && resultCode == Activity.RESULT_OK) {
            isBluetoothConnected = data?.getBooleanExtra("isBluetoothConnected", false) ?: false
            val deviceAddress = data?.getStringExtra("deviceAddress")

            // Assuming that `BluetoothListActivity` has already made the connection and you're just passing the status
            if (isBluetoothConnected && deviceAddress != null) {
                // You can retrieve the connected socket from a global Bluetooth manager or service here
                // For example, if you have a BluetoothService managing connections, you could get the socket like this:
                // bluetoothSocket = BluetoothService.getSocketForDevice(deviceAddress)
            }
            checkBluetoothConnection()
        }
    }






    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Refresh your connection status here
        checkBluetoothConnection()
    }

    private fun checkBluetoothConnection() {
        val connectionStatusTextView = findViewById<TextView>(R.id.connectionStatus)
        val errorMessage = findViewById<TextView>(R.id.errorMessage)

        if (!isBluetoothConnected) {
            errorMessage.visibility = View.VISIBLE
            connectionStatusTextView.text = getString(R.string.status_not_connected)
            connectionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.grayColor))
        } else {
            errorMessage.visibility = View.GONE
            connectionStatusTextView.text = getString(R.string.status_connected)
            connectionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.grayColor)) // Update to a color that represents a successful connection
        }
    }
}








