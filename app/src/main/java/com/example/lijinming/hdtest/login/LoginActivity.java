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
		//隐藏密码
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
		//验证用户信息
		if (isUserNameAndPwdValid()) {
			String userName = mAccount.getText().toString().trim();
			String userPwd = mPwd.getText().toString().trim();
			//与数据库中的用户信息进行匹配
			int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
			if(result==1){
				//登录成功，然后进入导航界面
				Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
				intent.putExtra("username",userName);
				startActivity(intent);
				//提醒登录成功
				Toast.makeText(this, getString(R.string.login_sucess),
						Toast.LENGTH_SHORT).show();
			}else if(result==0){
				//提醒登录失败，此用户不存在
				Toast.makeText(this, getString(R.string.login_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void register() {
		//注册用户
		if (isUserNameAndPwdValid()) {//用户账号及密码是否有效
			String userName = mAccount.getText().toString().trim();
			String userPwd = mPwd.getText().toString().trim();
			//检查用户是否已经存在
			int count=mUserDataManager.findUserByName(userName);
			if(count>0){
				//提醒用户账号已经存在无需再次注册
				Toast.makeText(this, getString(R.string.name_already_exist, userName),
						Toast.LENGTH_SHORT).show();
				return ;
			}
			UserData mUser = new UserData(userName, userPwd);
			mUserDataManager.openDataBase();
			//在数据库中加入一个用户
			long flag = mUserDataManager.insertUserData(mUser);
			if (flag == -1) {
				//提醒用户注册失败
				Toast.makeText(this, getString(R.string.register_fail),
						Toast.LENGTH_SHORT).show();
			}else{
				//提醒用户注册成功
				Toast.makeText(this, getString(R.string.register_sucess),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void cancle() {
		mAccount.setText("");//将输入的用户账号清除
		mPwd.setText("");//将输入的用户密码清除
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