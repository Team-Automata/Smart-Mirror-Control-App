/*
 * ControllerActivity.java
 * Copyright (C) 2017 Automata Development
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

package com.makeapede.mirror_controller.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.makeapede.mirror_controller.R;
import com.makeapede.mirror_controller.bluetooth.BluetoothActionConstants;
import com.makeapede.mirror_controller.bluetooth.BluetoothConnection;
import com.makeapede.mirror_controller.bluetooth.BluetoothLeConnection;

public class ControllerActivity extends AppCompatActivity implements BluetoothConnection.BluetoothConnectionEventListener,
																	 BluetoothActionConstants,
																	 LifecycleRegistryOwner {
	private static final String TAG = ControllerActivity.class.getSimpleName();

	private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	private ProgressDialog progress;

	private BluetoothConnection bluetoothConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar bar = getSupportActionBar();
		if(bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
		}

		final Intent intent = getIntent();

		String deviceAddress = intent.getStringExtra(DeviceListActivity.EXTRAS_DEVICE_ADDRESS);

		// Set activity title to the name of the connected device
		setTitle(intent.getStringExtra(DeviceListActivity.EXTRAS_DEVICE_NAME));

		int deviceType = intent.getIntExtra(DeviceListActivity.EXTRAS_DEVICE_TYPE,
											BluetoothDevice.DEVICE_TYPE_UNKNOWN);

		if (deviceType == BluetoothDevice.DEVICE_TYPE_LE) {
			bluetoothConnection = new BluetoothLeConnection(this, deviceAddress, this);
		} else {
			finish();
		}

		getLifecycle().addObserver(bluetoothConnection);

		progress = new ProgressDialog(this);
		progress.setTitle("Connecting...");
		progress.setIndeterminate(true);
		progress.show();
	}

	@Override
	public void onBluetoothConnectionEvent(String event) {
		switch (event) {
			case ACTION_CONNECTED:
				progress.hide();

				// Device connected
				break;

			case ACTION_DISCONNECTED:
			case ACTION_ERROR:
				finish();
				break;
		}
	}

	@Override
	public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}
}
