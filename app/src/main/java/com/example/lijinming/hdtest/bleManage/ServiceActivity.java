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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lijinming.hdtest.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ServiceActivity extends Activity {
	private static final String TAG = "ServiceActivity";
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic, gattCharacteristicPulse,
			gattCharacteristicECG,gattCharacteristicSound;

	private ToggleButton mToggleButton;
	private TextView pulsePlay,ecgPlay,soundPlay;
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
				mBluetoothGattCharacteristic = (BluetoothGattCharacteristic)getIntent()
						.getSerializableExtra("characteristic");
				UUID uuid = mBluetoothGattCharacteristic.getUuid();
				Log.e("TAG","数据有变化");
				if(uuid.equals(SampleGattAttributes.PusleCharacteristic)){
					String PulseData16 = String.valueOf(mBluetoothGattCharacteristic.getValue());//得到characteristic的值
//					String PulseCutFlag =PulseData16.substring(2);//去掉进制标识位
					String PulseData10 = Integer.valueOf(PulseData16, 16).toString();//将16进制数转换为10进制然后转为字符串
					Log.e("TAG", "接收到数据");
//					DataPlay.setText(PulseData10);

				}else if(uuid.equals(SampleGattAttributes.ECGCharacteristic)){
					mBluetoothGattCharacteristic.getValue();

				}else if (uuid.equals(SampleGattAttributes.SoundCharacteristic)){
					mBluetoothGattCharacteristic.getValue();
				}
				return;
			}
			// 读取脉搏数据
			if (BLEService.ACTION_PULSE_READ_OVER.equals(action)) {
				Log.e(TAG,"准备读取脉搏特征值");
				String Pulse = intent.getStringExtra(BLEService.Pulse);
     			Log.e(TAG, Pulse);
				pulsePlay.setText(Pulse);
				return;
			}
			//读取心音数据
			if (BLEService.ACTION_SOUND_READ_OVER.equals(action)) {
				Log.e(TAG, "准备读取心音特征值");
				String sound = intent.getStringExtra(BLEService.Sound);
				Log.e(TAG, sound);
				soundPlay.setText(sound);
				return;
			}
			/*if (BLEService.ACTION_ECG_READ_OVER.equals(action)) {
				Log.e(TAG, "准备读取特征值");
				String ecg = intent.getStringExtra(BLEService.ECG);
				Log.e(TAG, ecg);
				pulsePlay.setText(ecg);
			*//*	String ECG = intent.getStringExtra(BLEService.ECG);
				Log.e(TAG,ECG);
				ecgPlay.setText(Pulse);*//*
				return;
			}*/



		}
	}
	private void initView(){
		pulsePlay = (TextView)findViewById(R.id.pulsePlay);
		ecgPlay = (TextView)findViewById(R.id.ecgPlay);
		soundPlay = (TextView)findViewById(R.id.soundPlay);
		mToggleButton = (ToggleButton)findViewById(R.id.Realstart);
		mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Timer pulseTimer = new Timer();
					pulseTimer.schedule(new pulseTask(), 0, 5);
					Timer soundTimer = new Timer();
					soundTimer.schedule(new soundTask(), 0, 5);
					/*Timer ecgTimer = new Timer();
					ecgTimer.schedule(new ecgTask(),0,5);*/
					return;

				}else {


				}
			}
		});
	}
	class pulseTask extends TimerTask {

		@Override
		public void run() {
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicSound);
			//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicECG);

		}
	}
	class soundTask extends TimerTask {

		@Override
		public void run() {
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicSound);
			//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicECG);

		}

	}
	class ecgTask extends TimerTask {

		@Override
		public void run() {
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicSound);
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicECG);

		}

	}



	// 获取名字
	private boolean read_name_flag = false;
//	private boolean servicesdiscovered_flag = false;
	private boolean connect_flag = false;
	private boolean bind_flag = false;
	private BluetoothDevice device;
	//一个Handler对应一个线程，这里的线程是主线程。但是为什么可以写三个Handler?
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
	private Handler connect_fail_handl = new Handler() {
		public void handleMessage(Message msg) {
			Tools.mBLEService.disConectBle();
			Toast.makeText(getApplicationContext(), "连接失败",
					Toast.LENGTH_LONG).show();
			finish();
		};
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
		new readNameThread().start();
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
				//for (int i = 0; i < 10; i++) {
				while(true){
					connect_flag = false;
					System.out.println("conectBle");
					if(exit_activity)return;  // 如果已经退出程序，则结束线程
					Tools.mBLEService.conectBle(device);

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
					.getService(SampleGattAttributes.HeartService);

			Log.e("TAG", "得到服务");
			if(service == null){
				Log.e("TAG", "连接服务失败");
				if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					readNameFail.sendEmptyMessage(0);
				}
				return;
			}
			b.putString("msg", "读取通道信息");
			reflashDialogMessage.sendMessage(msg);
		    gattCharacteristicPulse =service
					.getCharacteristic(SampleGattAttributes.PusleCharacteristic);
			Log.e("TAG", "得到脉搏特征");
		    gattCharacteristicECG =service
					.getCharacteristic(SampleGattAttributes.ECGCharacteristic);
			gattCharacteristicSound =service
					.getCharacteristic(SampleGattAttributes.SoundCharacteristic);
			dis_services_handl.sendEmptyMessage(0);

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
}
