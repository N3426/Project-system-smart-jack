import android.bluetooth.BluetoothSocket;

public class BluetoothService {
    private static BluetoothSocket socket;

    public static synchronized void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        BluetoothService.socket = bluetoothSocket;
    }

    public static synchronized BluetoothSocket getBluetoothSocket() {
        return BluetoothService.socket;
    }

    // Add methods to manage the connection, read, write, and close operations.
}
