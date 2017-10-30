/*
 * BluetoothScanner.java
 * Copyright (C) 2017  Automata Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.makeapede.mirror_controller.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import com.makeapede.mirror_controller.R;

public class BluetoothScanner extends ScanCallback {
	private BluetoothScanEventListener scanEventListener;
	private Context context;
	protected boolean scanning = false;

	protected BluetoothAdapter btAdapter;
	private BluetoothLeScanner leScanner;

	public BluetoothScanner(Context context) throws BluetoothNotSupportedException {
		this.context = context;

		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
		leScanner = btAdapter.getBluetoothLeScanner();

		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(context, context.getText(R.string.ble_not_supported), Toast.LENGTH_SHORT).show();
		}

		if (btAdapter == null || leScanner == null) {
			throw new BluetoothNotSupportedException();
		}
	}

	public void startScan(BluetoothScanEventListener scanEventListener, long period) {
		if (!isScanning()) {
			scanning = true;

			this.scanEventListener = scanEventListener;

			leScanner.startScan(this);

			new Handler().postDelayed(this::stopScan, period);
		}
	}

	public void stopScan() {
		if (isScanning()) {
			scanning = false;

			scanEventListener.onScanComplete();

			leScanner.stopScan(this);
		}
	}

	@Override
	public void onScanResult(int callbackType, ScanResult result) {
		scanEventListener.onDeviceFound(result.getDevice());
	}

	public Context getContext() {
		return context;
	}

	public BluetoothAdapter getBtAdapter() {
		return btAdapter;
	}

	public boolean isScanning() {
		return scanning;
	}

	public interface BluetoothScanEventListener {
		void onDeviceFound(BluetoothDevice device);
		void onScanComplete();
	}
}
