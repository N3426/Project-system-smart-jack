package com.example.ssjbeta2

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import java.io.IOException
import java.util.UUID
import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.bluetooth.BluetoothSocket
import android.os.Build
import androidx.annotation.RequiresApi


@Suppress("DEPRECATION")
class BluetoothListActivity : AppCompatActivity() {

    private lateinit var refreshButton: Button
    private lateinit var bluetoothListView: ListView
    @Suppress("DEPRECATION")
    private val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var isConnecting: Boolean = false // Flag to indicate if a connection attempt is in progress

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 2
        private const val REQUEST_BLUETOOTH_ACTIVITY = 1001
        private const val HC05_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_list)

        refreshButton = findViewById(R.id.refreshButton)
        bluetoothListView = findViewById(R.id.bluetoothListView)

        refreshButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // Request both ACCESS_FINE_LOCATION and BLUETOOTH_SCAN permissions
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN), REQUEST_LOCATION_PERMISSION)
            } else {
                // Permissions have been granted, proceed with Bluetooth operation
                updateBluetoothDevices()
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateBluetoothDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
            val deviceNameList: List<String> = pairedDevices?.map { it.name } ?: listOf()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNameList)
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ACTIVITY)


            bluetoothListView.adapter = adapter

            bluetoothListView.setOnItemClickListener { _, _, position, _ ->
                val device = pairedDevices?.elementAt(position)
                connectToDevice(device)

            }

        } else {
            // Handle the case where the BLUETOOTH_CONNECT permission is not granted.
            Toast.makeText(this, "Bluetooth permission is not granted. Please enable it in settings.", Toast.LENGTH_LONG).show()
        }
    }

    private fun connectToDevice(device: BluetoothDevice?) {
        if (device == null) {
            Toast.makeText(this, "Device not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (isConnecting) {
            Toast.makeText(this, "Connection attempt in progress, please wait.", Toast.LENGTH_SHORT).show()
            return
        }

        isConnecting = true // Set the flag to true as we are starting a connection attempt

        // Check if the BLUETOOTH_CONNECT permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Thread {
                var socket: BluetoothSocket? = null
                try {
                    val uuid = UUID.fromString(HC05_UUID_STRING)
                    socket = device.createRfcommSocketToServiceRecord(uuid)
                    mBluetoothAdapter?.cancelDiscovery() // It's important to cancel discovery because it otherwise slows down the connection.
                    socket.connect()

                    runOnUiThread {
                        isConnecting = false // Reset the flag as the connection attempt has finished
                        if (!isFinishing && !isDestroyed) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("isBluetoothConnected", socket.isConnected)
                            resultIntent.putExtra("deviceAddress", device.address)
                            // Here, pass the HC05_UUID_STRING directly, since it was used to create the socket
                            resultIntent.putExtra("connectedSocketUUID", HC05_UUID_STRING)
                            setResult(Activity.RESULT_OK, resultIntent)
                            // Inside the connection thread, after successfully connecting
                            BluetoothService.setBluetoothSocket(socket);

                            finish()
                        }
                    }
                } catch (e: IOException) {
                    // Close the socket if an exception occurred
                    try {
                        socket?.close()
                    } catch (e2: IOException) {
                        // Could not close the socket, ignore or log this
                    }
                    runOnUiThread {
                        isConnecting = false // Reset the flag as the connection attempt has finished
                        if (!isFinishing && !isDestroyed) {
                            AlertDialog.Builder(this)
                                .setTitle("Connection Failed")
                                .setMessage("Failed to connect to the device. Please ensure the device is active and in range.")
                                .setPositiveButton("Retry") { _, _ ->
                                    // Retry connection
                                    connectToDevice(device)
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                    }
                } catch (e: SecurityException) {
                    runOnUiThread {
                        isConnecting = false // Reset the flag as the connection attempt has finished
                        if (!isFinishing && !isDestroyed) {
                            Toast.makeText(this, "Bluetooth permissions are not granted.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.start()
        } else {
            isConnecting = false // Reset the flag as we didn't start a connection attempt due to lack of permissions
            Toast.makeText(this, "Bluetooth permissions are not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // All requested permissions have been granted
                    updateBluetoothDevices()
                } else {
                    // Permissions were denied, show a message to the user explaining why these permissions are needed
                    Toast.makeText(this, "Bluetooth scanning and location permissions are required to discover and connect to devices.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, you can proceed with your Bluetooth operations
                // When the connection is successful in BluetoothListActivity

            } else {
                // User did not enable Bluetooth
                Toast.makeText(this, "Bluetooth is not turned on", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



