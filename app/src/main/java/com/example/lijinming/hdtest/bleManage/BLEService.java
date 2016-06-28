package com.example.lijinming.hdtest.bleManage;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.UUID;

public class BLEService extends Service {
	private static final String TAG = "BLEService";
	public final static String Pulse = "PulseCharacteristic";
	public final static String ECG = "ECGCharacteristic";
	public final static String Sound = "SoundCharacteristic";


	public final static String ACTION_DATA_CHANGE = "com.example.bluetooth.le.ACTION_DATA_CHANGE";
	public final static String ACTION_RSSI_READ = "com.example.bluetooth.le.ACTION_RSSI_READ";
	public final static String ACTION_STATE_CONNECTED = "com.example.bluetooth.le.ACTION_STATE_CONNECTED";
	public final static String ACTION_STATE_DISCONNECTED = "com.example.bluetooth.le.ACTION_STATE_DISCONNECTED";
	public final static String ACTION_WRITE_OVER = "com.example.bluetooth.le.ACTION_WRITE_OVER";

	public final static String ACTION_PULSE_READ_OVER = "com.example.bluetooth.le.ACTION_PULSE_READ_OVER";//�����Զ���㲥
	public final static String ACTION_ECG_READ_OVER = "com.example.bluetooth.le.ACTION_ECG_READ_OVER";//�ĵ��Զ���㲥
	public final static String ACTION_SOUND_READ_OVER = "com.example.bluetooth.le.ACTION_SOUND_READ_OVER";//�����Զ���㲥



	public final static String ACTION_READ_Descriptor_OVER = "com.example.bluetooth.le.ACTION_READ_Descriptor_OVER";
	public final static String ACTION_ServicesDiscovered_OVER = "com.example.bluetooth.le.ACTION_ServicesDiscovered_OVER";

	public  BluetoothManager mBluetoothManager;
	public static BluetoothAdapter mBluetoothAdapter;
	public static BluetoothGatt mBluetoothGatt;
	private boolean connect_flag = false;

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if (newState == BluetoothProfile.STATE_CONNECTED) { // ���ӳɹ�
				Log.e(TAG, "�������ӳɹ�");
				System.out.println("CONNECTED");
				connect_flag = true;
				mBluetoothGatt.discoverServices();
				broadcastUpdate(ACTION_STATE_CONNECTED);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // �Ͽ�����

				connect_flag = false;
				broadcastUpdate(ACTION_STATE_DISCONNECTED);
			}

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.e(TAG, "���ַ���");
				broadcastUpdate(ACTION_ServicesDiscovered_OVER, status);
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
			broadcastUpdate(ACTION_READ_Descriptor_OVER, status);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			/*if (characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
				Log.e(TAG, "��ȡ��������ֵ");
				broadcastUpdate(ACTION_PULSE_READ_OVER, characteristic);
			}*//*else if (characteristic.getUuid().equals(SampleGattAttributes.ECGCharacteristic)){
				Log.e(TAG, "��ȡ�ĵ�����ֵ");
				broadcastUpdate(ACTION_ECG_READ_OVER, characteristic);
			}else if (characteristic.getUuid().equals(SampleGattAttributes.SoundCharacteristic)){

				Log.e(TAG, "��ȡ��������ֵ");
				broadcastUpdate(ACTION_SOUND_READ_OVER, characteristic);
			}
*/
		}


		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG, "����ֵ�����仯");

			super.onCharacteristicChanged(gatt, characteristic);
			if (characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
				Log.e(TAG, "��ȡ��������ֵ");
				broadcastUpdate(ACTION_PULSE_READ_OVER, characteristic);
			}else if (characteristic.getUuid().equals(SampleGattAttributes.ECGCharacteristic)){
				Log.e(TAG, "��ȡ��������ֵ");
				broadcastUpdate(ACTION_ECG_READ_OVER, characteristic);
			}if (characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
				Log.e(TAG, "��ȡ��������ֵ");
				broadcastUpdate(ACTION_SOUND_READ_OVER, characteristic);
			}

		}
		

	/*	@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			broadcastUpdate(ACTION_WRITE_OVER, status);
		}*/

	};

	public class LocalBinder extends Binder {
		public BLEService getService() {
			return BLEService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		disConectBle();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		disConectBle();
		return super.onUnbind(intent);
	}

	// ��ʼ��BLE
	public boolean initBle() {
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		if (null == mBluetoothManager) {
			return false;
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (null == mBluetoothAdapter) {
			return false;
		}

//		if (!mBluetoothAdapter.isEnabled()) {
//			mBluetoothAdapter.enable();
//		}

		return true;
	}

	// ɨ��
	public void scanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.startLeScan(callback);
	}

	// ֹͣɨ��
	public void stopscanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.stopLeScan(callback);
	}

	// ��������
	public boolean conectBle(BluetoothDevice mBluetoothDevice) {
		disConectBle();
		
		BluetoothDevice device_tmp = mBluetoothAdapter.getRemoteDevice(mBluetoothDevice.getAddress());
		if(device_tmp == null){
			System.out.println("device ������");
			return false;
		}
		
		mBluetoothGatt = device_tmp.connectGatt(getApplicationContext(), false,
				mGattCallback);

		return true;
	}
		
	// �ر�����
	public void disConectBle(){
		if(mBluetoothGatt != null){
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
			connect_flag = false;
		}
	}
	
	// ����Ƿ�����
	public boolean isConnected()
	{
		return connect_flag;
	}
	
	// CONNECTED��DISCONNECTED    ���͹㲥��Ϣ
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	// service��Descriptor     ���͹㲥��Ϣ
	private void broadcastUpdate(final String action, int value) {
		final Intent intent = new Intent(action);
		intent.putExtra("value", value);
		sendBroadcast(intent);
	}

	//DataChange��Read      ���͹㲥��Ϣ
	private void broadcastUpdate(final String action, BluetoothGattCharacteristic characteristic) {
		Log.e("TAG", "�����ȴ�����");
		Intent intent = new Intent(action);
		if(characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
			//�����յ�����ֵת��Ϊԭ���Ľ��ƣ�Ȼ��תΪ�ַ�����
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				String PulseCutSpace = stringBuilder.toString().replaceAll(" ", "");//ȥ���ַ������еĿո�
				intent.putExtra(Pulse,PulseCutSpace);
				sendBroadcast(intent);

				Log.e(TAG, "�����������ͳɹ�");

			}
			sendBroadcast(intent);
			Log.e(TAG, "�����������ͳɹ�");
		}else if(characteristic.getUuid().equals(SampleGattAttributes.ECGCharacteristic)){
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				String ecgCut = stringBuilder.toString().replaceAll(" ", "");//ȥ���ַ������еĿո�
			}
			sendBroadcast(intent);
			Log.e(TAG, "�����������ͳɹ�");
		}else if(characteristic.getUuid().equals(SampleGattAttributes.SoundCharacteristic)){
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				String soundCut = stringBuilder.toString().replaceAll(" ", "");//ȥ���ַ������еĿո�
				Log.e(TAG,soundCut);
				String soundData10 = Integer.valueOf(soundCut, 16).toString();//��16������ת��Ϊ10������Ȼ����תΪ�ַ���
				Log.e(TAG, soundData10);
				//			intent.putExtra(Key, new String(data) + "\n" + stringBuilder.toString());
				intent.putExtra(Sound,soundData10);
			}
			sendBroadcast(intent);
			Log.e(TAG, "�����������ͳɹ�");
		}


	}
	/*//����readCharacteristic�������У����Ը���notification֪ͨ
	*//**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
													 boolean enabled) {
		Log.w(TAG, "����֪ͨ����");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		Log.w(TAG, "Descriptor����");

		// ��������������֪ͨ�������ȴ�֪ͨ������©���ݣ���֤���յ������������ԣ�
		if (SampleGattAttributes.PusleCharacteristic.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_PulseCHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Log.w(TAG, "Descriptor���óɹ�");
			mBluetoothGatt.writeDescriptor(descriptor);
		}else if (SampleGattAttributes.ECGCharacteristic.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_ECGCHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}else if (SampleGattAttributes.SoundCharacteristic.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_SoundCHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}
	/*/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	 * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 *//*
	public static void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}*/
}

