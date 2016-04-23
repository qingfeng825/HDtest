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
				mBluetoothGattCharacteristic = (BluetoothGattCharacteristic)getIntent()
						.getSerializableExtra("characteristic");
				UUID uuid = mBluetoothGattCharacteristic.getUuid();
				Log.e("TAG","�����б仯");
				if(uuid.equals(SampleGattAttributes.PusleCharacteristic)){
					String PulseData16 = String.valueOf(mBluetoothGattCharacteristic.getValue());//�õ�characteristic��ֵ
//					String PulseCutFlag =PulseData16.substring(2);//ȥ�����Ʊ�ʶλ
					String PulseData10 = Integer.valueOf(PulseData16, 16).toString();//��16������ת��Ϊ10����Ȼ��תΪ�ַ���
					Log.e("TAG", "���յ�����");
//					DataPlay.setText(PulseData10);

				}else if(uuid.equals(SampleGattAttributes.ECGCharacteristic)){
					mBluetoothGattCharacteristic.getValue();

				}else if (uuid.equals(SampleGattAttributes.SoundCharacteristic)){
					mBluetoothGattCharacteristic.getValue();
				}
				return;
			}
			// ��ȡ��������
			if (BLEService.ACTION_PULSE_READ_OVER.equals(action)) {
				Log.e(TAG,"׼����ȡ��������ֵ");
				String Pulse = intent.getStringExtra(BLEService.Pulse);
     			Log.e(TAG, Pulse);
				pulsePlay.setText(Pulse);
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



	// ��ȡ����
	private boolean read_name_flag = false;
//	private boolean servicesdiscovered_flag = false;
	private boolean connect_flag = false;
	private boolean bind_flag = false;
	private BluetoothDevice device;
	//һ��Handler��Ӧһ���̣߳�������߳������̡߳�����Ϊʲô����д����Handler?
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
			Toast.makeText(getApplicationContext(), "����ʧ��",
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
		// ����һ������Ի���
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle("���ڼ���...");
		pd.setMessage("��������");
		pd.show();
		
		device = (BluetoothDevice) getIntent()
				.getParcelableExtra("device");
		new readNameThread().start();
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
				//for (int i = 0; i < 10; i++) {
				while(true){
					connect_flag = false;
					System.out.println("conectBle");
					if(exit_activity)return;  // ����Ѿ��˳�����������߳�
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
			
			read_name_flag = false; // ��ȡ�豸��

			BluetoothGattService service = Tools.mBLEService.mBluetoothGatt
					.getService(SampleGattAttributes.HeartService);

			Log.e("TAG", "�õ�����");
			if(service == null){
				Log.e("TAG", "���ӷ���ʧ��");
				if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					readNameFail.sendEmptyMessage(0);
				}
				return;
			}
			b.putString("msg", "��ȡͨ����Ϣ");
			reflashDialogMessage.sendMessage(msg);
		    gattCharacteristicPulse =service
					.getCharacteristic(SampleGattAttributes.PusleCharacteristic);
			Log.e("TAG", "�õ���������");
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
