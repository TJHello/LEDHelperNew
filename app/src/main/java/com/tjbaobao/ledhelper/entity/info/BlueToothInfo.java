package com.tjbaobao.ledhelper.entity.info;

import android.bluetooth.BluetoothSocket;

public class BlueToothInfo {
	String name ;
	String address ;
	int state ;
	BluetoothSocket mBluetoothSocket ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public BluetoothSocket getmBluetoothSocket() {
		return mBluetoothSocket;
	}

	public void setmBluetoothSocket(BluetoothSocket mBluetoothSocket) {
		this.mBluetoothSocket = mBluetoothSocket;
	}
}
