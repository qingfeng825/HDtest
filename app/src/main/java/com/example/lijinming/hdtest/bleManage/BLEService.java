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

	public final static String ACTION_PULSE_READ_OVER = "com.example.bluetooth.le.ACTION_PULSE_READ_OVER";//脉搏自定义广播
	public final static String ACTION_ECG_READ_OVER = "com.example.bluetooth.le.ACTION_ECG_READ_OVER";//心电自定义广播
	public final static String ACTION_SOUND_READ_OVER = "com.example.bluetooth.le.ACTION_SOUND_READ_OVER";//心音自定义广播



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
			if (newState == BluetoothProfile.STATE_CONNECTED) { // 链接成功
				Log.e(TAG, "蓝牙连接成功");
				System.out.println("CONNECTED");
				connect_flag = true;
				mBluetoothGatt.discoverServices();
				broadcastUpdate(ACTION_STATE_CONNECTED);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断开链接

				connect_flag = false;
				broadcastUpdate(ACTION_STATE_DISCONNECTED);
			}

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.e(TAG, "发现服务");
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
		}


		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG, "特征值发生变化");

			super.onCharacteristicChanged(gatt, characteristic);
			if (characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
				Log.e(TAG, "读取脉搏特征值");
				broadcastUpdate(ACTION_PULSE_READ_OVER, characteristic);
			}else if (characteristic.getUuid().equals(SampleGattAttributes.ECGCharacteristic)){
				Log.e(TAG, "读取心电特征值");
				broadcastUpdate(ACTION_ECG_READ_OVER, characteristic);
			}else if (characteristic.getUuid().equals(SampleGattAttributes.SoundCharacteristic)){
				Log.e(TAG, "读取心音特征值");
				broadcastUpdate(ACTION_SOUND_READ_OVER, characteristic);
			}

		}
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

	// 初始化BLE
	public boolean initBle() {
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		if (null == mBluetoothManager) {
			return false;
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (null == mBluetoothAdapter) {
			return false;
		}

		return true;
	}

	// 扫描
	public void scanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.startLeScan(callback);
	}

	// 停止扫描
	public void stopscanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.stopLeScan(callback);
	}

	// 发起连接
	public boolean conectBle(BluetoothDevice mBluetoothDevice) {
		disConectBle();
		
		BluetoothDevice device_tmp = mBluetoothAdapter.getRemoteDevice(mBluetoothDevice.getAddress());
		if(device_tmp == null){
			System.out.println("device 不存在");
			return false;
		}
		
		mBluetoothGatt = device_tmp.connectGatt(getApplicationContext(), false,
				mGattCallback);

		return true;
	}
		
	// 关闭连接
	public void disConectBle(){
		if(mBluetoothGatt != null){
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
			connect_flag = false;
		}
	}
	
	// 检查是否连接
	public boolean isConnected()
	{
		return connect_flag;
	}
	
	// CONNECTED与DISCONNECTED    发送广播消息
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	// service与Descriptor     发送广播消息
	private void broadcastUpdate(final String action, int value) {
		final Intent intent = new Intent(action);
		intent.putExtra("value", value);
		sendBroadcast(intent);
	}

	//DataChange与Read发送广播消息
	private void broadcastUpdate(final String action, BluetoothGattCharacteristic characteristic) {
		Log.e("TAG", "特征等待发送");
		Intent intent = new Intent(action);
		if(characteristic.getUuid().equals(SampleGattAttributes.PusleCharacteristic)){
			//将接收的特征值转换为原来的进制，然后转为字符串。
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				String PulseCutSpace = stringBuilder.toString().replaceAll(" ", "");//去除字符中所有的空格
				Log.e(TAG,PulseCutSpace);
				intent.putExtra(Pulse,PulseCutSpace);
				sendBroadcast(intent);
				Log.e(TAG, "脉搏特征发送成功");
			}
		}else if(characteristic.getUuid().equals(SampleGattAttributes.ECGCharacteristic)){
			String ecgOriginalData,validData,ecgCut,verifyData;
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				ecgOriginalData = stringBuilder.toString().replaceAll(" ", "");//去除字符中所有的空格
				//截取之后进行验证确保获取的心电数据有效
				int validIndex = ecgOriginalData.indexOf("AAAA04");
				if(validIndex<=16){
					validData = ecgOriginalData.substring(validIndex+6,validIndex+16);
					ecgCut = validData.substring(0,8);
					verifyData = validData.substring(8,10);
					boolean dataFlag = DataValid.isValid(ecgCut,verifyData);
//					Log.e(TAG,ecgCut);
					if(dataFlag){
						Log.e(TAG,ecgCut);
						intent.putExtra(ECG,ecgCut);
						sendBroadcast(intent);
						Log.e(TAG, "心电特征发送成功");
					}
				}
			}

		}else if(characteristic.getUuid().equals(SampleGattAttributes.SoundCharacteristic)){
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				String soundCut = stringBuilder.toString().replaceAll(" ", "");//去除字符中所有的空格
				Log.e(TAG,soundCut);
				intent.putExtra(Sound,soundCut);
				sendBroadcast(intent);
				Log.e(TAG, "心音特征发送成功");
			}
		}


	}
	/*//由于readCharacteristic方法不行，所以改用notification通知
	*//**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
													 boolean enabled) {
		Log.w(TAG, "进入通知设置");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		Log.w(TAG, "Descriptor设置");

		Boolean pusleUuid = SampleGattAttributes.PusleCharacteristic.equals(characteristic.getUuid()),
				ecgUuid = SampleGattAttributes.ECGCharacteristic.equals(characteristic.getUuid()),
				soundUuid = SampleGattAttributes.SoundCharacteristic.equals(characteristic.getUuid());
		// 设置脉搏特征的通知，被动等待通知不会遗漏数据（保证接收到的数据完整性）
		if (soundUuid) {
			BluetoothGattDescriptor soundDescriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_SoundCHARACTERISTIC_CONFIG));
			soundDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(soundDescriptor);
			Log.w(TAG, "sound设置成功");
		}else if(pusleUuid){
			BluetoothGattDescriptor pulseDescriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_PulseCHARACTERISTIC_CONFIG));
			pulseDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Log.w(TAG, "pulse设置成功");
			mBluetoothGatt.writeDescriptor(pulseDescriptor);
		}else if(ecgUuid){
			BluetoothGattDescriptor ecgDescriptor = characteristic.getDescriptor(
					UUID.fromString(SampleGattAttributes.CLIENT_ECGCHARACTERISTIC_CONFIG));
			ecgDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(ecgDescriptor);
			Log.w(TAG, "ecg设置成功");
		}
	}

}

