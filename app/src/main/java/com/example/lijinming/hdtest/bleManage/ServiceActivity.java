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
 * ServiceActivity���ܼ�飺
 * 1������BLE�豸
 * 2����ͨ���ض���UUID���õ�BLE�豸�ķ��������
 * 3����ȡ�ض������������ֵ
 * 4���Է����������ֵ���д洢����ʵʱ������ʾ*/

public class ServiceActivity extends Activity {
	private MyInternalStorage mMyInternalStorage;
	private Boolean readCharacterFlag = false;
	private Boolean notificationFlag = false;
	private static final String TAG = "ServiceActivity";
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic, gattCharacteristicPulse,
			gattCharacteristicECG,gattCharacteristicSound,mNotifyCharacteristic;

	private ToggleButton mToggleButton;
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

	// ���ù㲥����
	private BluetoothReceiver bluetoothReceiver = null;

	private void setBroadcastReceiver() {
		// ����һ��IntentFilter���󣬽���actionָ��ΪBluetoothDevice.ACTION_FOUND
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
		// ע��㲥������
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
					System.out.println("�������");
				}
				return;
			}
			// ���ݸı�֪ͨ
			if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
				Log.e(TAG,"���յ�֪ͨ");
				String Pulse = intent.getStringExtra(BLEService.Pulse);
				Log.e(TAG, Pulse);
				pulsePlay.setText(Pulse);
				return;
			}
			// ��ȡ��������
			if (BLEService.ACTION_PULSE_READ_OVER.equals(action)) {
				Log.e(TAG,"���յ�����֪ͨ");
				String PulseCutSpace = intent.getStringExtra(BLEService.Pulse);
				//���������ݽ��зָ���ǽ�һ�����ݰ��ֳ�20������
				int k = 0;
				for (int i = 0; i < 20;i++){
					String Str = PulseCutSpace.substring(k,k+2);
					k = k+2;
					String PulseData10 = Integer.valueOf(Str, 16).toString();//��16������ת��Ϊ10������Ȼ����תΪ�ַ���
					try {
						mMyInternalStorage.appendPusle(PulseData10);//���������ݴ��뵽SDcard��ʱ��������������ļ���
					} catch (IOException e) {
						e.printStackTrace();
					}
					//					pulsePlay.setText(PulseData10);
					Log.e(TAG, PulseData10);
				}


				return;
			}
			//��ȡ��������
			if (BLEService.ACTION_SOUND_READ_OVER.equals(action)) {
				Log.e(TAG, "׼����ȡ��������ֵ");
				String sound = intent.getStringExtra(BLEService.Sound);
				Log.e(TAG, sound);
				soundPlay.setText(sound);
				return;
			}
			/*if (BLEService.ACTION_ECG_READ_OVER.equals(action)) {
				Log.e(TAG, "׼����ȡ����ֵ");
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
		/*mSwitch = (Switch)findViewById(R.id.startReceive);
		mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mMyInternalStorage =new MyInternalStorage(getApplication());
					notificationFlag =true;
					setNotify(gattCharacteristicPulse,notificationFlag);
				}else {
					notificationFlag =false;
					setNotify(gattCharacteristicPulse,notificationFlag);
				}
			}
		});*/
		mToggleButton = (ToggleButton)findViewById(R.id.Realstart);
		mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//�����߳���ָ�������ʶ�������ֵ

					mMyInternalStorage =new MyInternalStorage(getApplication());
//					Timer soundTimer = new Timer();
//					soundTimer.schedule(new soundTask(), 2, 10);
//					Timer ecgTimer = new Timer();
//					ecgTimer.schedule(new ecgTask(),0,5);
					notificationFlag =true;
					setNotify(gattCharacteristicPulse,notificationFlag);
//					setNotify(gattCharacteristicSound);
					return;

				}else {
					notificationFlag =false;
					setNotify(gattCharacteristicPulse,notificationFlag);
//					readCharacterFlag = false;
//					Pulse(readCharacterFlag);
				}
			}
		});
	}
	/*public  void  Pulse (Boolean readCharacterFlag){
		if (readCharacterFlag){
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
		}


	}
	class pulseTask extends TimerTask {

		@Override
		public void run() {
			if (readCharacterFlag){
//				Log.e(TAG,"TimerTaskTest");
				Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
				Log.e(TAG, "TimerTaskTest1");
			}
		}
	}
	class soundTask extends TimerTask {

		@Override
		public void run() {
			if (readCharacterFlag){
				Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicSound);
			}
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
			//	    	Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicECG);

		}

	}
	class ecgTask extends TimerTask {

		@Override
		public void run() {
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicPulse);
//			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicSound);
			Tools.mBLEService.mBluetoothGatt.readCharacteristic(gattCharacteristicECG);

		}

	}*/



	// ��ȡ����
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
	/*private Handler connect_fail_handl = new Handler() {
		public void handleMessage(Message msg) {
			Tools.mBLEService.disConectBle();
			Toast.makeText(getApplicationContext(), "����ʧ��",
					Toast.LENGTH_LONG).show();
			finish();
		}
	};*/

	private ProgressDialog pd;
	private Handler reflashDialogMessage = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			pd.setMessage(b.getString("msg"));
		}
	};
	private void getDefaultName() {
		// ����һ������Ի���
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle("���ڼ���...");
		pd.setMessage("��������");
		pd.show();
		device = (BluetoothDevice) getIntent()
				.getParcelableExtra("device");
		new readNameThread().start();//��ʼ����BLE�豸֮��õ����������
	}
	
	// ��ȡ�߳�
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
					if(exit_activity)return;  // ����Ѿ��˳�����������߳�
					Tools.mBLEService.conectBle(device);//����BLE�豸
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
			read_name_flag = false; // ��ȡ�豸��
			BluetoothGattService service = Tools.mBLEService.mBluetoothGatt
					.getService(SampleGattAttributes.HeartService);//ͨ���ض�UUID�õ�BLE�豸�ķ���
			if(service == null){
				if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					readNameFail.sendEmptyMessage(0);
				}
				return;
			}
			b.putString("msg", "��ȡͨ����Ϣ");
			reflashDialogMessage.sendMessage(msg);//����֪ͨ�ı�Dialog�е���ʾ
			//ͨ�������ȡ�ض�������ֵ
		    gattCharacteristicPulse =service
					.getCharacteristic(SampleGattAttributes.PusleCharacteristic);
		    gattCharacteristicECG =service
					.getCharacteristic(SampleGattAttributes.ECGCharacteristic);
			gattCharacteristicSound =service
					.getCharacteristic(SampleGattAttributes.SoundCharacteristic);

			dis_services_handl.sendEmptyMessage(0);//����֪ͨ�ر�Dialog
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
			Log.e(TAG, "��������");
		}
	}
}
