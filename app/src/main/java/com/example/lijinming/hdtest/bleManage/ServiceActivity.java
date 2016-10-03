package com.example.lijinming.hdtest.bleManage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lijinming.hdtest.DataManage.MyInternalStorage;
import com.example.lijinming.hdtest.R;

import java.io.IOException;
/**
 * ServiceActivity功能简介：
 * 1：连接BLE设备
 * 2：（通过特定的UUID）得到BLE设备的服务和特征
 * 3：获取特定服务和特征的值
 * 4：对服务和特征的值进行存储并且实时波形显示*/

public class ServiceActivity extends Activity {
	private MyInternalStorage mMyInternalStorage;
	private Boolean readCharacterFlag = false;
	private Boolean notificationFlag = false;
	private static final String TAG = "ServiceActivity";
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic, gattCharacteristicPulse,
			gattCharacteristicECG,gattCharacteristicSound,mNotifyCharacteristic;

	private ToggleButton pulseButton,soundButton,ecgButton;
	private Switch mSwitch;
	private TextView pulsePlay,ecgPlay,soundPlay;
	private BLEService mBLEService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_page);
		setBroadcastReceiver();
		initView();
		getDefaultName();
	}

	// 设置广播监听
	private BluetoothReceiver bluetoothReceiver = null;

	private void setBroadcastReceiver() {
		// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(
				BLEService.ACTION_READ_Descriptor_OVER);
		intentFilter.addAction(BLEService.ACTION_ServicesDiscovered_OVER);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BLEService.ACTION_ECG_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_SOUND_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_PULSE_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_RSSI_READ);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		intentFilter.addAction(BLEService.ACTION_STATE_DISCONNECTED);
		intentFilter.addAction(BLEService.ACTION_WRITE_OVER);
		bluetoothReceiver = new BluetoothReceiver();
		// 注册广播接收器
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BLEService.ACTION_READ_Descriptor_OVER.equals(action)) {
				if (BluetoothGatt.GATT_SUCCESS == intent.getIntExtra("value",
						-1)) {
					read_name_flag = true;
				}
				return;
			}
			if (BLEService.ACTION_ServicesDiscovered_OVER.equals(action)) {
				connect_flag = true;
				return;
			}
			if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
//				connect_flag = true;
				return;
			}
			
			if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
//				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(BluetoothDevice.BOND_BONDED == device.getBondState()){
					Tools.mBLEService.disConectBle();
					readNameFail.sendEmptyMessageDelayed(0, 200);
				}else if(BluetoothDevice.BOND_BONDING == device.getBondState()){
					bind_flag = true;
					System.out.println("正在配对");
				}
				return;
			}
			// 数据改变通知
			if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
				Log.e(TAG,"接收到通知");
				String Pulse = intent.getStringExtra(BLEService.Pulse);
				Log.e(TAG, Pulse);
				pulsePlay.setText(Pulse);
				return;
			}
			// 读取脉搏数据
			if (BLEService.ACTION_PULSE_READ_OVER.equals(action)) {
				Log.e(TAG,"接收到脉搏通知");
				String pulseCutSpace = intent.getStringExtra(BLEService.Pulse);
				//将脉搏数据进行分割，既是将一个数据包分成20个数据
				int k = 0;
				for (int i = 0; i < 20;i++){
					String Str = pulseCutSpace.substring(k,k+2);
					k = k+2;
					String pulseData10 = Integer.valueOf(Str, 16)
							.toString();//将16进制数转换为10进制数然后在转为字符串
					try {
						mMyInternalStorage.appendPusle(pulseData10);//将脉搏数据存入到SDcard以时间加脉搏命名的文件中
					} catch (IOException e) {
						e.printStackTrace();
					}
					//					pulsePlay.setText(PulseData10);
					Log.e(TAG, pulseData10);
				}
				return;
			}//读取心音数据
			if (BLEService.ACTION_SOUND_READ_OVER.equals(action)) {
				Log.e(TAG, "准备读取心音特征值");
				String soundCutSpace = intent.getStringExtra(BLEService.Sound);
				int k = 0;
				for (int i = 0; i < 20;i++){
					String Str = soundCutSpace.substring(k,k+2);
					k = k+2;
					String soundData10 = Integer.valueOf(Str, 16).toString();//将16进制数转换为10进制数然后在转为字符串
					try {
						mMyInternalStorage.appendSound(soundData10);//将心音数据存入到SDcard以时间加脉搏命名的文件中
					} catch (IOException e) {
						e.printStackTrace();
					}
					//					pulsePlay.setText(PulseData10);
					Log.e(TAG, soundData10);
				}
				//soundPlay.setText(soundData10);
				return;
			}
			if (BLEService.ACTION_ECG_READ_OVER.equals(action)) {
				Log.e(TAG, "准备读取心电特征值");
				String ecgCutSpace = intent.getStringExtra(BLEService.ECG);
				int k = 0;
				for (int i = 0; i < 20;i++){
					String Str = ecgCutSpace.substring(k,k+2);
					k = k+2;
					String ecgData10 = Integer.valueOf(Str, 16).toString();//将16进制数转换为10进制数然后在转为字符串
					try {
						mMyInternalStorage.appendECG(ecgData10);//将心电数据存入到SDcard以时间加脉搏命名的文件中
					} catch (IOException e) {
						e.printStackTrace();
					}
					Log.e(TAG, ecgData10);
				}
				return;
			}



		}
	}
	private void initView(){
		mMyInternalStorage = new MyInternalStorage(getApplication());
		pulseButton = (ToggleButton)findViewById(R.id.startPulse);
		pulseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					notificationFlag =true;
					setNotify(gattCharacteristicPulse,notificationFlag);
					return;

				}else {
					notificationFlag =false;
					setNotify(gattCharacteristicPulse,notificationFlag);
				}
			}
		});
		soundButton = (ToggleButton)findViewById(R.id.startSound);
		soundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					notificationFlag =true;
					setNotify(gattCharacteristicSound,notificationFlag);
					return;
				}else {
					notificationFlag =false;
					setNotify(gattCharacteristicSound,notificationFlag);
				}
			}
		});
		ecgButton = (ToggleButton)findViewById(R.id.startECG);
		ecgButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					notificationFlag =true;
					setNotify(gattCharacteristicECG,notificationFlag);
					return;
				}else {
					notificationFlag =false;
					setNotify(gattCharacteristicECG,notificationFlag);
				}
			}
		});
	}



	// 获取名字
	private boolean read_name_flag = false;
//	private boolean servicesdiscovered_flag = false;
	private boolean connect_flag = false;
	private boolean bind_flag = false;
	private BluetoothDevice device;
	private Handler dis_services_handl = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			service_list_adapter.notifyDataSetChanged();
			pd.dismiss();
		}
	};
	private Handler readNameFail = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Tools.mBLEService.disConectBle();
			new readNameThread().start();
		}
		
	};

	private ProgressDialog pd;
	private Handler reflashDialogMessage = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			pd.setMessage(b.getString("msg"));
		}
	};
	private void getDefaultName() {
		// 开启一个缓冲对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle("正在加载...");
		pd.setMessage("正在连接");
		pd.show();
		device = (BluetoothDevice) getIntent()
				.getParcelableExtra("device");
		new readNameThread().start();//开始连接BLE设备之后得到服务和特征
	}
	
	// 读取线程
	private class readNameThread extends Thread{
		@Override
		public void run() {
			super.run();
			Message msg = reflashDialogMessage.obtainMessage();
			Bundle b = new Bundle();
			msg.setData(b);
			
			try {
				while(true){
					connect_flag = false;
					System.out.println("conectBle");
					if(exit_activity)return;  // 如果已经退出程序，则结束线程
					Tools.mBLEService.conectBle(device);//连接BLE设备
					for (int j = 0; j < 50; j++) {
						if (connect_flag) {
							break;
						}
						sleep(100);
					}
					if (connect_flag) {
						break;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			read_name_flag = false; // 读取设备名
			BluetoothGattService service = Tools.mBLEService.mBluetoothGatt
					.getService(SampleGattAttributes.HeartService);//通过特定UUID得到BLE设备的服务
			if(service == null){
				if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					readNameFail.sendEmptyMessage(0);
				}
				return;
			}
			b.putString("msg", "读取通道信息");
			reflashDialogMessage.sendMessage(msg);//发送通知改变Dialog中的提示
			//通过服务获取特定特征的值
		    gattCharacteristicPulse =service
					.getCharacteristic(SampleGattAttributes.PusleCharacteristic);
		    gattCharacteristicECG =service
					.getCharacteristic(SampleGattAttributes.ECGCharacteristic);
			gattCharacteristicSound =service
					.getCharacteristic(SampleGattAttributes.SoundCharacteristic);

			dis_services_handl.sendEmptyMessage(0);//发送通知关闭Dialog
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(!Tools.mBLEService.isConnected()){
			finish();
		}
	}
	private boolean exit_activity = false;
	@Override
	protected void onDestroy() {
		Tools.mBLEService.disConectBle();
		exit_activity = true;
		unregisterReceiver(bluetoothReceiver);
		super.onDestroy();
	}
	public void setNotify(BluetoothGattCharacteristic GattCharacteristic ,Boolean notificationFlag){
		final int charaProp = GattCharacteristic.getProperties();
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			// If there is an active notification on a characteristic, clear
			// it first so it doesn't update the data field on the user interface.
			if (mNotifyCharacteristic != null) {
				BLEService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
			}
//			BLEService.readCharacteristic(GattCharacteristic);
		}
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//			mNotifyCharacteristic = gattCharacteristicPulse;
			BLEService.setCharacteristicNotification(
					GattCharacteristic, notificationFlag);
			Log.e(TAG, "特性设置");
		}
	}
}
