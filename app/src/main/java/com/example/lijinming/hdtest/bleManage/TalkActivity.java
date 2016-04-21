package com.example.lijinming.hdtest.bleManage;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lijinming.hdtest.DataManage.MyInternalStorage;
import com.example.lijinming.hdtest.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TalkActivity extends Activity implements OnClickListener {

	private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

	MyInternalStorage mMyInternalStorage = new MyInternalStorage(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_activity);

		Intent intent = getIntent();
		mBluetoothGattCharacteristic = Tools.mBLEService.mBluetoothGatt
				.getServices().get(intent.getIntExtra("one", 0))
				.getCharacteristics().get(intent.getIntExtra("two", 0));

		setBroadcastReceiver(); // ���ù㲥����g

		initView(); // ��ʼ���ؼ�
	}


	// ���ù㲥����
	private BroadcastReceiver bluetoothReceiver;

	private void setBroadcastReceiver() {
		// ����һ��IntentFilter���󣬽���actionָ��ΪBluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(
				BLEService.ACTION_DATA_CHANGE);
		intentFilter.addAction(BLEService.ACTION_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_RSSI_READ);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		intentFilter.addAction(BLEService.ACTION_STATE_DISCONNECTED);
		intentFilter.addAction(BLEService.ACTION_WRITE_OVER);
		bluetoothReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// ���ݸı�֪ͨ
				if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
					dis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}
				// ��ȡ����
				if (BLEService.ACTION_READ_OVER.equals(action)) {
					dis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}

				// ����״̬�ı�
				if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
					talking_conect_flag_txt.setText("������");
				}
				if (BLEService.ACTION_STATE_DISCONNECTED.equals(action)) {
					Tools.mBLEService.disConectBle();
					talking_conect_flag_txt.setText("�ѶϿ�");
					Toast.makeText(getApplicationContext(), "�ѶϿ�����",
							Toast.LENGTH_LONG).show();
				}
			}

		};
		// ע��㲥������
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private void dis_recive_msg(byte[] tmp_byte) {
		if (talking_stopdis_btn.isChecked())
			return; // ֹͣ��ʾ

		String tmp = "";
		if (0 == tmp_byte.length) {
			return;
		}

		switch (read_fmt_int) {
		case 0: // �ַ�����ʾ
			try {
				tmp = new String(tmp_byte, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 1: // 16������ʾ
			for (int i = 0; i < tmp_byte.length; i++) {
				String hex = Integer.toHexString(tmp_byte[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				tmp += ' ';
				tmp = tmp + hex;
			}
			break;
		case 2: // 10������ʾ
			int count = 0;
			for (int i = 0; i < tmp_byte.length; i++) {
				count *= 256;
				count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
			}
			tmp = Integer.toString(count);
			break;
		default:
			break;
		}
		readText.setText(tmp);
		try {
			mMyInternalStorage.append(tmp);//������һ�����ݾʹ��뵽SD Card
		} catch (IOException e) {
			e.printStackTrace();
		}

		//		ChatMsgFmt entity2 = new ChatMsgFmt("Device", tmp, MESSAGE_FROM.OTHERS);
//		chat_list.add(entity2);
//		chat_list_adapter.notifyDataSetChanged();
	}

	// ��ʼ���ؼ�
	private TextView talking_conect_flag_txt;
	private Button talking_read_btn;
	private Button talking_clear_btn;
	private Spinner read_fmt_select;
	private ToggleButton talking_stopdis_btn;
	private ListView chatist;
	private Spinner write_fmt_select;
	private EditText edit_string_id;
	private EditText edit_hex_id;
	private EditText edit_shi_id;
	private Button sendbuttonid;
	private CheckBox send_onTime_checkbox;
	private EditText send_time_edit;
	private LinearLayout writeable_Layout;
	private TextView readText;
	private TextView writeText;

//	private List<ChatMsgFmt> chat_list = new ArrayList<ChatMsgFmt>();
//	private ChatAdapater chat_list_adapter;
	private ArrayAdapter<String> fmt_adapter;
	private static final String FMT_SELCET[] = { "Str", "Hex", "Dec" };
	private int write_fmt_int; // �������ݸ�ʽ ����
	private int read_fmt_int = 0; // �������ݸ�ʽ ����
	private int proper = 0; // ͨ��Ȩ��

	private void initView() {
		writeText =(TextView)findViewById(R.id.write);
		readText =(TextView)findViewById(R.id.read);
		talking_conect_flag_txt = (TextView) findViewById(R.id.talking_conect_flag_txt);
		talking_read_btn = (Button) findViewById(R.id.talking_read_btn);
//		talking_clear_btn = (Button) findViewById(R.id.talking_clear_btn);
		read_fmt_select = (Spinner) findViewById(R.id.read_fmt_select);
		talking_stopdis_btn = (ToggleButton) findViewById(R.id.talking_stopdis_btn);
//		chatist = (ListView) findViewById(R.id.chatist);
		write_fmt_select = (Spinner) findViewById(R.id.write_fmt_select);
		edit_string_id = (EditText) findViewById(R.id.edit_string_id);
		edit_hex_id = (EditText) findViewById(R.id.edit_hex_id);
		edit_shi_id = (EditText) findViewById(R.id.edit_shi_id);
		sendbuttonid = (Button) findViewById(R.id.sendbuttonid);
//		send_onTime_checkbox = (CheckBox) findViewById(R.id.send_onTime_checkbox);
//		send_time_edit = (EditText) findViewById(R.id.send_time_edit);
		writeable_Layout = (LinearLayout) findViewById(R.id.writeable_Layout);

		// ��ʼ���ؼ�����
		talking_conect_flag_txt.setText("������");
		fmt_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FMT_SELCET);
		fmt_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		read_fmt_select.setAdapter(fmt_adapter); // ���ͺͶ�ȡ���ݸ�ʽ
		read_fmt_select.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				read_fmt_int = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		write_fmt_select.setAdapter(fmt_adapter);
		write_fmt_select
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						write_fmt_int = arg2;
						switch (write_fmt_int) {
						case 0:
							edit_string_id.setVisibility(View.VISIBLE); // ��ʾ
							edit_hex_id.setVisibility(View.GONE); // ����
							edit_shi_id.setVisibility(View.GONE); // ����
							edit_string_id.setFocusable(true);
							edit_string_id.setFocusableInTouchMode(true);
							edit_string_id.requestFocus();
							break; // �ַ���
						case 1:
							edit_string_id.setVisibility(View.GONE); // ��ʾ
							edit_hex_id.setVisibility(View.VISIBLE); // ����
							edit_shi_id.setVisibility(View.GONE); // ����
							edit_hex_id.setFocusable(true);
							edit_hex_id.setFocusableInTouchMode(true);
							edit_hex_id.requestFocus();
							break; // 16����
						case 2:
							edit_string_id.setVisibility(View.GONE); // ��ʾ
							edit_hex_id.setVisibility(View.GONE); // ����
							edit_shi_id.setVisibility(View.VISIBLE); // ����
							edit_shi_id.setFocusable(true);
							edit_shi_id.setFocusableInTouchMode(true);
							edit_shi_id.requestFocus();
							break; // 10����
						default:
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
//		chat_list_adapter = new ChatAdapater(getApplicationContext());
//		chatist.setAdapter(chat_list_adapter);
		talking_read_btn.setOnClickListener(this);
//		talking_clear_btn.setOnClickListener(this);
		talking_stopdis_btn.setOnClickListener(this);
		sendbuttonid.setOnClickListener(this);
//		send_onTime_checkbox.setOnClickListener(this);

		// �鿴����ʲôȨ��
		proper = mBluetoothGattCharacteristic.getProperties();
		if (0 != (proper & 0x02)) { // �ɶ�
			talking_read_btn.setVisibility(View.VISIBLE);
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE))) { // ��д
			writeable_Layout.setVisibility(View.VISIBLE);
		}
		if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
				|| (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // ֪ͨ
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, true);
			BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
					.getDescriptor(UUID
							.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Tools.mBLEService.mBluetoothGatt.writeDescriptor(descriptor);
		}
	}



	// ��ť����
	@Override
	public void onClick(View v) {
		if (!Tools.mBLEService.isConnected()) {
			Toast.makeText(getApplicationContext(), "�ѶϿ�����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (v == sendbuttonid) { // ���Ͱ�ť
			byte[] sendmsg = getMsgEdit(true);
			if (sendmsg == null) {
				return;
			}

			mBluetoothGattCharacteristic.setValue(sendmsg);
			Tools.mBLEService.mBluetoothGatt
					.writeCharacteristic(mBluetoothGattCharacteristic);
			return;
		}
		if (v == talking_read_btn) { // ��ȡ��ť ����֮��ÿ2ms��ȡһ��

			Timer timer = new Timer();
			timer.schedule(new MyTask(), 0, 2);
			return;
		}
	}
	class MyTask extends TimerTask {

		@Override
		public void run() {
			Tools.mBLEService.mBluetoothGatt
					.readCharacteristic(mBluetoothGattCharacteristic);
		}
	}

	// ��ȡ��������
	private byte[] getMsgEdit(boolean dis_flag) {
		String tmp_str = "";
		byte[] tmp_byte = null;
		byte[] write_msg_byte = null;

		switch (write_fmt_int) {
		case 0:
			tmp_str = edit_string_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			write_msg_byte = tmp_str.getBytes();
			break;

		case 1:
			tmp_str = edit_hex_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			tmp_byte = tmp_str.getBytes();
			write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
			for (int i = 0; i < tmp_byte.length; i++) {
				if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
				} else {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'a' + 10) & 0xFF);
				}
			}
			break;

		case 2:
			tmp_str = edit_shi_id.getText().toString();
			if (0 == tmp_str.length())
				return null;

			int data_int = Integer.parseInt(tmp_str);
			int byte_size = 0;
			for (byte_size = 0; data_int != 0; byte_size++) { // ����ռ���ֽ���
				data_int /= 256;
			}
			write_msg_byte = new byte[byte_size];

			data_int = Integer.parseInt(tmp_str);
			for (int i = 0; i < byte_size; i++) { // ת��
				write_msg_byte[i] = (byte) (0xFF & (data_int % 256));
				data_int /= 256;
			}

			break;
		}

		if (0 == tmp_str.length())
			return null;
		// ��ʾ
		if (dis_flag) {
			writeText.setText(tmp_str);
			/*ChatMsgFmt entity = new ChatMsgFmt("Me", tmp_str, MESSAGE_FROM.ME);
			chat_list.add(entity);
			chat_list_adapter.notifyDataSetChanged();*/
		}

		return write_msg_byte;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Tools.mBLEService.isConnected()) {
			talking_conect_flag_txt.setText("������");
		} else {
			talking_conect_flag_txt.setText("�ѶϿ�");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bluetoothReceiver);

		if (!Tools.mBLEService.isConnected()) {
			return;
		}

		if (0 != (proper & 0x10)) { // ȥ����֪ͨ
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, false);
		}

	}

}
