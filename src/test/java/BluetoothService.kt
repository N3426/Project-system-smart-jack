package com.example.ssjbeta2

import android.bluetooth.BluetoothSocket

object BluetoothService {
    private var bluetoothSocket: BluetoothSocket? = null

    @Synchronized
    fun setBluetoothSocket(socket: BluetoothSocket?) {
        bluetoothSocket = socket
    }

    @Synchronized
    fun getBluetoothSocket(): BluetoothSocket? {
        return bluetoothSocket
    }

    // Add other Bluetooth management methods as needed.
}
