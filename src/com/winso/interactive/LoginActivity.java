package com.winso.interactive;

import com.winso.interactive.app.*;
import com.winso.interactive.R;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.SelectHelpParam;

import com.winso.comm_library.*;
import com.winso.interactive.app.AppContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;

public class LoginActivity extends BaseActivity {

	private Button mBtnLogin;
	private Button mBtnSetting;
	private EditText mEditUser;
	private EditText mEditUserPasswd;
	private CheckBox mCheckAutoLogin;
	private CheckBox mCheckRemInfo;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mBtnLogin = (Button) findViewById(R.id.login_btn_login);

		mBtnSetting = (Button) findViewById(R.id.login_btn_setting_2);

		// 用户名和密码
		mEditUser = (EditText) findViewById(R.id.login_account);
		mEditUserPasswd = (EditText) findViewById(R.id.login_password);

		// 记住信息
		mCheckAutoLogin = (CheckBox) findViewById(R.id.login_check_box_autologin);
		mCheckRemInfo = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);

		// 加载以前的配置信息
		loadOldUser();

		// 绑定登录按钮点击事件。
		// mBtnLogin.setOnClickListener(new Listener());

		// 登录
		mBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				EasyLog.debug("login");
				new LoginTask().execute(); // 提交登录操作。
			}
		});

		// 设置按扭
		mBtnSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				UIHelper.openSetting(v.getContext());
			}
		});
		//
		mCheckAutoLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appContext.setCookieBoolean("login_auto",
						mCheckAutoLogin.isChecked());
			}
		});

		mCheckRemInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				appContext.setCookieBoolean("login_rem",
						mCheckRemInfo.isChecked());
			}
		});

		if (mCheckAutoLogin.isChecked()) {
			new LoginTask().execute(); // 提交登录操作。
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		appContext.setCookie("login_exit", "exit");
	}
	
	private boolean loadOldUser() {

		mCheckAutoLogin.setChecked(appContext.getCookieBoolean("login_auto"));
		mCheckRemInfo.setChecked(appContext.getCookieBoolean("login_rem"));

		mEditUser.setText(appContext.getCookie("login_user"));

		if (mCheckRemInfo.isChecked())
			mEditUserPasswd.setText(appContext.getCookie("login_pass"));

		return true;
	}

	/**
	 * 显示登录提示对话框。
	 */
	protected void showDialog() {

		progressDialog = ProgressDialog
				.show(this, "正在登陆验证", "请稍后", true, false);
		progressDialog.setCancelable(true);
	}

	/**
	 * 隐藏登录提示对话框。
	 */
	protected void dismissDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	///
	private String Login()
	{
		try {
			
			boolean bNetConnected = appContext.isNetworkConnected();
			if (!bNetConnected) {
				return "connect_fail";
			}
			
			
			// 该ice只作为客户端出现，连接ice服务器，服务器是用C++写的
			appContext.m_ice.login(appContext.getCookie("cent_ip"), AppContext.CENT_PORT);
			
			
			if (!appContext.m_ice.isLogin()) {
				Toast.makeText(getApplicationContext(), "网络未连接",
						Toast.LENGTH_SHORT).show();
				return "connect_fail" ;
			}
			// test uploadfile

			SelectHelpParam helpParam = new SelectHelpParam();
			helpParam.add(mEditUser.getText().toString());
			helpParam.add(mEditUserPasswd.getText().toString());

			SelectHelp v = appContext.m_ice.selectByParam(AppSQLConst.S_SQL_LOGIN,
					helpParam.get());

			EasyLog.debug("return=" + v.mReturnCode);
			if (v.size() > 0) {

				appContext.setCookie("login_user", mEditUser.getText()
						.toString());
				appContext.setCookie("login_pass", mEditUserPasswd.getText()
						.toString());
				/* hzx add exit login */
				appContext.setCookie("login_exit", "login");
				
				appContext.mIStudentID = Integer.valueOf(v.valueStringByName(0, "user_id"));
				
				String sIP = GetIP.getLocalIP(LoginActivity.this);
				appContext.loadInitConfigure(sIP);
				
				return "success";
			} else if (v.mReturnCode < 0) {

				return "connect_fail";
			}
		
			
			return "fail";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
		return "connect_fail";

	}
	/**
	 * 登录管理。
	 * 
	 */
	private class LoginTask extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		protected String doInBackground(String... arg0) {
			return Login();
		}

		protected void onPostExecute(String result) {

			if (result != null) {
				if (result.equals("success")) {

					startActivity(new Intent(LoginActivity.this,
							MainActivity.class)); // 启动 主界面活动类。

					finish(); // 终止当前活动界面。
				} else if (result.equals("connect_fail")) {
					Toast.makeText(getApplicationContext(), "连接失败",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "帐号或密码错误",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "连接失败",
						Toast.LENGTH_SHORT).show();
			}

			dismissDialog();
		}
	}

}
