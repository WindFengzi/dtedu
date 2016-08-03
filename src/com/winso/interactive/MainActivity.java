package com.winso.interactive;

import java.io.File;
import java.util.List;

import com.kbeanie.imagechooser.api.ChooserType;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.DownloadFileTask;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.interactive.app.AppContext;
import com.winso.interactive.app.AppManager;
import com.winso.interactive.app.SocketService;
import com.winso.interactive.app.UIHelper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends BaseActivity {

	private AppBroadcastReceiver mAppBroadcastReceiver;
	private String updateMsg = "有新的版本";
	private Button mBtSetting;
	private String sApkName = "";
	private int miServerVersion = -1;
	private String mSApkPath = ""; // APk保存目录
	MyCallBack myCallback;

	// private TextView txGPS;

	public class MyCallBack implements CallbackInterface {
		MainActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {
				installApk(responseText);
				return;
			}

		}

		public void cancel(String responseText) {
			UIHelper.showMsg(MainActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack(MainActivity ac) {
			mAc = ac;
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk(String sFile) {
		File apkfile = new File(sFile);
		if (!apkfile.exists()) {
			return;
		}

		// UIHelper.showMsg(MainActivity.this, "", apkfile.toString());

		Intent i = new Intent(Intent.ACTION_VIEW);

		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
	}

	// new
	// DownloadFileTask(app.m_ice,mContext,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myCallback = new MyCallBack(this);

		checkVersion();

		mBtSetting = (Button) findViewById(R.id.main_btn_setting);
		mBtSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.openSetting(v.getContext());

			}
		});

		initButtonFunction();

		// 安装服务接收
		mAppBroadcastReceiver = new AppBroadcastReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.interactive");

		registerReceiver(mAppBroadcastReceiver, filter);

		this.startService(new Intent(this, SocketService.class));

		// onClickLock();
		// showNotification();
	}

	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;

	}

	

	protected void onStop() {
		super.onStop();

		AppManager am = AppManager.getAppManager();

		int iActivitySize = am.size();

		if (am.findActivity("com.winso.interactive.MainActivity") == null) {
			appContext.insertNetLog("", 1, "切换至后台");
		}

	}

	@Override
	protected void onResume() {
		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		appContext.insertNetLog("", 2, "退出了app");

		clearNotification();

		unregisterReceiver(mAppBroadcastReceiver);
		this.stopService(new Intent(this, SocketService.class));
	}

	void initButtonFunction() {
		// 草搞
		// 点名
		Button btPoint = (Button) findViewById(R.id.main_btn_point);
		btPoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(), PointActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_VIEW_MAIN_POINT);
			}
		});

		// 学生互评
		Button btReiveEachOther = (Button) findViewById(R.id.main_btn_review_eachother);
		btReiveEachOther.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(),
						MainReviewStudentActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_EACH_REVIEW);
			}
		});

		// 请假
		Button btLeave = (Button) findViewById(R.id.main_btn_leave);
		btLeave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						MainLeaveActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_MAIN_LEAVE);
			}
		});

		// 问答
		Button btAnswer = (Button) findViewById(R.id.main_btn_answer);
		btAnswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(), AnswerActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_VIEW_MAIN_ANSWER);
			}
		});

	}

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO) {

			return;
		}

	}

	public void onClickLock() {
		// 设备安全管理服务 2.2之前的版本是没有对外暴露的 只能通过反射技术获取
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// 申请权限
		ComponentName componentName = new ComponentName(this,
				MyLockReceiver.class);
		// 判断该组件是否有系统管理员的权限
		boolean isAdminActive = devicePolicyManager
				.isAdminActive(componentName);
		if (isAdminActive) {

			devicePolicyManager.lockNow(); // 锁屏

			// devicePolicyManager.resetPassword("123", 0); // 设置锁屏密码
			// devicePolicyManager.wipeData(0); 恢复出厂设置 (建议大家不要在真机上测试) 模拟器不支持该操作

		} else {
			Intent intent = new Intent();
			// 指定动作名称
			intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			// 指定给哪个组件授权
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					componentName);
			startActivity(intent);
		}

	}

	// ///////////////////////////////////////////////////////////////////////////
	void checkVersion() {
		mSApkPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		// 判断是否挂载了SD卡
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			mSApkPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/winso/Update/";
			File file = new File(mSApkPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// 检查新版本
		// 网络连接判断
		if (!appContext.isNetworkConnected()){
			UIHelper.showMsg(this, "", "网络未连接");			
		}

		if (appContext.isCheckUp()) {
			// UpdateManager mgr = UpdateManager.getUpdateManager();

			// mgr.checkAppUpdate(app, this, true,myCallback);
			Dialog noticeDialog;

			int iC = getNeedUpgrade();
			if (iC == 1) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("软件版本更新");
				builder.setMessage(updateMsg + " " + miServerVersion);
				builder.setPositiveButton("立即更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								// new
								// DownloadFileTask(app.m_ice,mContext,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
								downloadApk();
							}
						});
				builder.setNegativeButton("以后再说",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				noticeDialog = builder.create();
				noticeDialog.show();
			} else if (iC == 2) {

				installApk(mSApkPath + "/" + sApkName);
			}

		}
	}

	void downloadApk() {
		sApkName = "InternShip" + ".apk";

		// UIHelper.showMsg(this, "", app.DEFAULT_DB_SAVE_PATH());

		new DownloadFileTask(appContext.m_ice, this, "release/" + sApkName,
				mSApkPath, myCallback).execute();
	}

	/*
	 * 用于下载Apk 返回０代表没有新版本，１代表有新版本则没有下载，２代表有新版本但已经下载
	 */

	private int getNeedUpgrade() {
		try {

			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			// curVersionCode = info.versionCode;
			miServerVersion = appContext.getServerVersion();

			sApkName = "InternShip" + ".apk";

			if (miServerVersion <= 0)
				return 0;

			if (miServerVersion > info.versionCode) {
				File ApkFile = new File(mSApkPath + "/" + sApkName);
				if (ApkFile.exists()) {
					SelectHelp help = appContext.m_ice
							.getFileInfo(AppContext.mSUpgradePath + "/"
									+ sApkName);
					if (help.size() > 0) {
						if (FileUtils.getFileSize(mSApkPath + "/" + sApkName) == Integer
								.valueOf(help.valueStringByName(0, "size"))) {
							return 2;
						}
					}

					return 1;
				}
				return 1;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}

		return 0;
	}

	
	
	// 删除通知
	private void clearNotification() {
		// 启动后删除之前我们定义的通知
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

	}

	/**
	 * 在状态栏显示通知
	 */
	private void showNotification(String sCmd, String sMsg, String sTitle) {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		
		//String sTmp = sCmd + "---" + sMsg + "---" ;
		//UIHelper.showMsg(this, "", sTmp);
		
		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.ic_launcher,
				"教学互动系统", System.currentTimeMillis());
		// FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
		// FLAG_NO_CLEAR 该通知不能被状态栏的清除按钮给清除掉
		// FLAG_ONGOING_EVENT 通知放置在正在运行
		// FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		// FLAG_AUTO_CANCEL
		notification.flags |= Notification.FLAG_INSISTENT;
		notification.flags |= Notification.FLAG_AUTO_CANCEL; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		// notification.flags |=Notification.DEFAULT_SOUNDS;

		// DEFAULT_ALL 使用所有默认值，比如声音，震动，闪屏等等
		// DEFAULT_LIGHTS 使用默认闪光提示
		// DEFAULT_SOUNDS 使用默认提示声音
		// DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission
		// android:name="android.permission.VIBRATE" />权限
		notification.defaults = Notification.DEFAULT_LIGHTS;
		// 叠加效果常量
		// notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 1000; // 闪光时间，毫秒

		// 设置通知的事件消息
		CharSequence contentTitle = sTitle; // 通知栏标题
		CharSequence contentText = sMsg; // 通知栏内容

		Intent notificationIntent = null;

		if (sCmd.equals(AppContext.CMD_POINT)) {
			notificationIntent = new Intent(MainActivity.this,
					PointActivity.class); // 点击该通知后要跳转的Activity

		}
		else if (sCmd.equals(AppContext.CMD_UPDATE_COURSE)) {
			
			//sMsg
			
		}
		else if (sCmd.equals(AppContext.CMD_UPDATE_ANSWER)) {
			// findActivity
			
			appContext.mIsSelectPeople = true;
			AppManager.getAppManager().findActivity("");

			notificationIntent = new Intent(MainActivity.this,
					AnswerActivity.class); // 点击该通知后要跳转的Activity
			startActivity(notificationIntent);

		} else {
			notificationIntent = new Intent(MainActivity.this,
					MainActivity.class); // 点击该通知后要跳转的Activity
		}

		PendingIntent contentItent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentItent);

		// 把Notification传递给NotificationManager
		notificationManager.notify(0, notification);
	}
	
	public static boolean isBackground(Context context) {

	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                          Log.i("后台", appProcess.processName);
	                          return true;
	                }else{
	                          Log.i("前台", appProcess.processName);
	                          return false;
	                }
	           }
	    }
	    return false;
	}



	private boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(getPackageName())) {
			return true;
		}

		return false;
	}

	public class AppBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {

				String sMsg = intent.getStringExtra("cmd");

				// 开始点到了
				if (sMsg.equals(AppContext.CMD_POINT)) {
					if (isRunningForeground(context)) {
						Intent dialogIntent = new Intent(context,
								PointActivity.class);
						dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						appContext.mSCheckPointNo = intent
								.getStringExtra("param1");
						dialogIntent.putExtra("check_no",
								intent.getStringExtra("param1"));
						context.startActivity(dialogIntent);
					}

					else {
						showNotification(sMsg, "同学生开始点名了，打开软件直接点名", "点名");
					}
				}
				else if (sMsg.equals(AppContext.CMD_UPDATE_COURSE)) {

					appContext.mICouseID  = Integer.valueOf(intent.getStringExtra("param1"));
					//appContext.updateUserIP(sIP);
					showNotification(sMsg, "当前课程编号:" + appContext.mICouseID, "开始上课了");

					//appContext.insertNetLog(sIP, 0, "切换了网络");

				}
				else if (sMsg.equals(AppContext.CMD_UPDATE_IP)) {

					String sIP = intent.getStringExtra("ip");
					appContext.updateUserIP(sIP);
					showNotification(sMsg, "当前IP地址:" + sIP, "网络变更了");

					appContext.insertNetLog(sIP, 0, "切换了网络");

				} else if (sMsg.equals(AppContext.CMD_UPDATE_ANSWER)) {
					// findActivity

					// Activity aa =
					// AppManager.getAppManager().findActivity("");

					// if (isRunningForeground(context)) {
					//
					// }
					//
					// else {
					showNotification(sMsg, "刷新题目", "答题");
					// }

				}

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

	}

}
