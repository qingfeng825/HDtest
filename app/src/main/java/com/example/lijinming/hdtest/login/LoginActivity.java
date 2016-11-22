package com.example.lijinming.hdtest.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lijinming.hdtest.R;
import com.example.lijinming.hdtest.navigation.NavigationActivity;

public class LoginActivity extends Activity {

	private EditText mAccount;
	private EditText mPwd;
	private Button mRegisterButton;
	private Button mLoginButton;
	private Button mCancleButton;
	private View loginView;
	private View loginSuccessView;
	private TextView loginSuccessShow;
	private UserDataManager mUserDataManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);

		mAccount = (EditText) findViewById(R.id.login_edit_account);
		mPwd = (EditText) findViewById(R.id.login_edit_pwd);
		mRegisterButton = (Button) findViewById(R.id.login_btn_register);
		mLoginButton = (Button) findViewById(R.id.login_btn_login);
		mCancleButton = (Button) findViewById(R.id.login_btn_cancle);
		//��������
		mPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		/*loginView=findViewById(R.id.login_view);
		loginSuccessView=findViewById(R.id.login_success_view);
		loginSuccessShow=(TextView) findViewById(R.id.login_success_show);*/

		mRegisterButton.setOnClickListener(mListener);
		mLoginButton.setOnClickListener(mListener);
		mCancleButton.setOnClickListener(mListener);
		
		if (mUserDataManager == null) {
			mUserDataManager = new UserDataManager(this);
			mUserDataManager.openDataBase();
        }
	}

	OnClickListener mListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_btn_register:
				register();
				break;
			case R.id.login_btn_login:
				login();
				break;
			case R.id.login_btn_cancle:
				cancle();
				break;
			}
		}
	};

	public void login() {
		//��֤�û���Ϣ
		if (isUserNameAndPwdValid()) {
			String userName = mAccount.getText().toString().trim();
			String userPwd = mPwd.getText().toString().trim();
			//�����ݿ��е��û���Ϣ����ƥ��
			int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
			if(result==1){
				//��¼�ɹ���Ȼ����뵼������
				Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
				intent.putExtra("username",userName);
				startActivity(intent);
				//���ѵ�¼�ɹ�
				Toast.makeText(this, getString(R.string.login_sucess),
						Toast.LENGTH_SHORT).show();
			}else if(result==0){
				//���ѵ�¼ʧ�ܣ����û�������
				Toast.makeText(this, getString(R.string.login_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void register() {
		//ע���û�
		if (isUserNameAndPwdValid()) {//�û��˺ż������Ƿ���Ч
			String userName = mAccount.getText().toString().trim();
			String userPwd = mPwd.getText().toString().trim();
			//����û��Ƿ��Ѿ�����
			int count=mUserDataManager.findUserByName(userName);
			if(count>0){
				//�����û��˺��Ѿ����������ٴ�ע��
				Toast.makeText(this, getString(R.string.name_already_exist, userName),
						Toast.LENGTH_SHORT).show();
				return ;
			}
			UserData mUser = new UserData(userName, userPwd);
			mUserDataManager.openDataBase();
			//�����ݿ��м���һ���û�
			long flag = mUserDataManager.insertUserData(mUser);
			if (flag == -1) {
				//�����û�ע��ʧ��
				Toast.makeText(this, getString(R.string.register_fail),
						Toast.LENGTH_SHORT).show();
			}else{
				//�����û�ע��ɹ�
				Toast.makeText(this, getString(R.string.register_sucess),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void cancle() {
		mAccount.setText("");//��������û��˺����
		mPwd.setText("");//��������û��������
	}
	public boolean isUserNameAndPwdValid() {
		if (mAccount.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.account_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (mPwd.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.pwd_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		if (mUserDataManager == null) {
			mUserDataManager = new UserDataManager(this);
			mUserDataManager.openDataBase();
        }
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mUserDataManager != null) {
			mUserDataManager.closeDataBase();
			mUserDataManager = null;
        }
		super.onPause();
	}
}